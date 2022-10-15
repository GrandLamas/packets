package de.lama.packets.client;

import de.lama.packets.AbstractPacketComponent;
import de.lama.packets.Packet;
import de.lama.packets.event.PacketReceiveEvent;
import de.lama.packets.event.PacketSendEvent;
import de.lama.packets.operation.Operation;
import de.lama.packets.registry.PacketRegistry;
import de.lama.packets.operation.PacketSendOperation;
import de.lama.packets.operation.SocketCloseOperation;
import de.lama.packets.transceiver.receiver.ScheduledPacketReceiver;
import de.lama.packets.transceiver.receiver.PacketReceiver;
import de.lama.packets.transceiver.transmitter.PacketTransmitter;
import de.lama.packets.transceiver.transmitter.PacketTransmitterBuilder;
import de.lama.packets.util.ExceptionHandler;
import de.lama.packets.wrapper.GsonWrapper;
import de.lama.packets.wrapper.PacketWrapper;

import java.net.InetAddress;
import java.net.Socket;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public abstract class AbstractGsonClient extends AbstractPacketComponent implements Client {

    private static final ScheduledExecutorService SERVICE = Executors.newScheduledThreadPool(10);

    protected final Socket socket;
    protected final PacketTransmitter transmitter;
    protected final PacketReceiver receiver;

    public AbstractGsonClient(Socket socket, PacketRegistry registry, int tickrate, ExceptionHandler exceptionHandler) {
        super(exceptionHandler, registry);
        this.socket = socket;

        PacketWrapper packetWrapper = new GsonWrapper(registry);
        this.transmitter = new PacketTransmitterBuilder().packetWrapper(packetWrapper)
                .threadPool(SERVICE).tickrate(tickrate)
                .exceptionHandler(exceptionHandler).build(exceptionHandler.operate(socket::getOutputStream, "Could not get output"));

        this.receiver = new ScheduledPacketReceiver(exceptionHandler.operate(socket::getInputStream, "Could not get input"),
                tickrate, SERVICE, packetWrapper, exceptionHandler, packet -> this.eventHandler.notify(new PacketReceiveEvent(packet)));

        this.transmitter.start();
        this.receiver.start();
    }

    @Override
    public int hashCode() {
        return this.getAddress().hashCode();
    }

    @Override
    public Operation send(Packet packet) {
        if (this.eventHandler.isCancelled(new PacketSendEvent(packet))) return null;
        return new PacketSendOperation(this.transmitter, packet);
    }

    @Override
    public Operation close() {
        if (this.socket.isClosed()) throw new IllegalStateException("Socket already closed");
        this.transmitter.stop();
        this.receiver.stop();
        // TODO: Stop packet transmitter
        return new SocketCloseOperation(this.socket, this.exceptionHandler);
    }

    @Override
    public Packet awaitPacket(long timeoutInMillis) {
        return this.receiver.awaitPacket(timeoutInMillis);
    }

    @Override
    public InetAddress getAddress() {
        return this.socket.getInetAddress();
    }

    @Override
    public int getPort() {
        return this.socket.getPort();
    }

}
