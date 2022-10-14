package de.lama.packets.server.client;

import de.lama.packets.Packet;
import de.lama.packets.AbstractPacketComponent;
import de.lama.packets.operation.Operation;
import de.lama.packets.server.PacketServer;
import de.lama.packets.server.client.event.ServerClientPacketReceiveEvent;
import de.lama.packets.server.client.event.ServerClientPacketSendEvent;
import de.lama.packets.server.client.event.ServerClientEvent;
import de.lama.packets.util.ExceptionHandler;
import de.lama.packets.util.ExceptionUtils;
import de.lama.packets.wrapper.GsonWrapper;
import de.lama.packets.wrapper.PacketWrapper;

import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;

public class ServerGsonClient extends AbstractPacketComponent<ServerClientEvent> implements ServerClient {

    private static final ScheduledExecutorService SERVICE = Executors.newScheduledThreadPool(10);

    private final PacketServer server;
    private final Socket socket;
    private final PacketWrapper packetWrapper;
    private final Queue<Packet> packetQueue;

    ServerGsonClient(PacketServer server, Socket socket, ExceptionHandler exceptionHandler) {
        super(exceptionHandler, server.getRegistry());
        this.server = server;
        this.socket = socket;
        this.packetWrapper = new GsonWrapper(server.getRegistry());
        this.packetQueue = new ConcurrentLinkedQueue<>();

        this.startSending();
    }

    private OutputStream getOutput() {
        return ExceptionUtils.operate(this.exceptionHandler, this.socket::getOutputStream, "No outputstream defined");
    }

    private void flushOutput(OutputStream outputStream) {
        ExceptionUtils.operate(this.exceptionHandler, outputStream::flush, "Could not flush output");
    }

    private OutputStream write(Packet packet) {
        OutputStream stream = this.getOutput();
        if (stream == null) return null;
        byte[] data = this.packetWrapper.wrap(packet);
        if (!ExceptionUtils.operate(this.exceptionHandler, () -> stream.write(data),"Could not write data")) return null;
        return stream;
    }

    private void pushPackets() {
        Packet send;
        while ((send = this.packetQueue.poll()) != null) {
            this.complete(send);
        }
    }

    private long getTicksInMs() {
        return 1000 / this.server.getTickrate();
    }

    private void startSending() {
        long rate = this.getTicksInMs();
        SERVICE.scheduleAtFixedRate(this::pushPackets, rate, rate, TimeUnit.MILLISECONDS);
    }

    @Override
    public int hashCode() {
        return this.getAddress().hashCode();
    }

    @Override
    public Operation send(Packet packet) {
        if (this.eventHandler.isCancelled(new ServerClientPacketSendEvent(this, packet))) return null;
        return new ServerClientPacketSendOperation(this, packet);
    }

    @Override
    public Operation close() {
        if (this.socket.isClosed()) throw new IllegalStateException("Socket already closed");
        return new ServerClientCloseOperation(this.socket, this.exceptionHandler);
    }

    @Override
    public Packet awaitPacket(long timeoutInMs) {
        AtomicReference<Packet> packet = new AtomicReference<>();
        UUID listenerId = this.eventHandler.subscribe(ServerClientPacketReceiveEvent.class, (event) -> packet.set(event.packet()));
        long over = System.currentTimeMillis() + timeoutInMs;
        while (packet.get() == null) {
            ExceptionUtils.operate(this.exceptionHandler, () -> Thread.sleep(this.getTicksInMs()), "Could not sleep");
            if (over <= System.currentTimeMillis()) return null;
        }

        this.eventHandler.unsubscribe(listenerId);
        return packet.get();
    }

    @Override
    public InetAddress getAddress() {
        return this.socket.getInetAddress();
    }

    @Override
    public int getPort() {
        return this.socket.getPort();
    }

    @Override
    public void queue(Packet packet) {
        this.packetQueue.add(packet);
    }

    @Override
    public void complete(Packet packet) {
        OutputStream stream = this.write(packet);
        if (stream == null) return;
        this.flushOutput(stream);
    }

    @Override
    public PacketServer getServer() {
        return this.server;
    }
}
