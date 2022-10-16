package de.lama.packets.client;

import de.lama.packets.*;
import de.lama.packets.event.events.PacketReceiveEvent;
import de.lama.packets.event.events.PacketSendEvent;
import de.lama.packets.io.CachedIOPacket;
import de.lama.packets.operation.Operation;
import de.lama.packets.operation.operations.PacketSendOperation;
import de.lama.packets.operation.operations.SocketCloseOperation;
import de.lama.packets.registry.PacketRegistry;
import de.lama.packets.transceiver.IoTransceivablePacket;
import de.lama.packets.transceiver.TransceivablePacket;
import de.lama.packets.transceiver.receiver.PacketReceiver;
import de.lama.packets.transceiver.receiver.ScheduledPacketReceiver;
import de.lama.packets.transceiver.transmitter.PacketTransmitter;
import de.lama.packets.transceiver.transmitter.PacketTransmitterBuilder;
import de.lama.packets.util.ExceptionHandler;
import de.lama.packets.wrapper.GsonWrapper;
import de.lama.packets.wrapper.PacketWrapper;

import java.net.InetAddress;
import java.net.Socket;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public abstract class AbstractGsonClient extends AbstractPacketIOComponent implements Client {

    private static final ScheduledExecutorService TRANSCEIVER_POOL = Executors.newScheduledThreadPool(10);

    protected final Socket socket;
    protected final PacketTransmitter transmitter;
    protected final PacketReceiver receiver;
    protected final PacketWrapper wrapper;

    public AbstractGsonClient(Socket socket, PacketRegistry registry, int tickrate, ExceptionHandler exceptionHandler) {
        super(exceptionHandler, registry);
        this.socket = socket;
        this.wrapper = new GsonWrapper(registry);

        this.transmitter = new PacketTransmitterBuilder().threadPool(TRANSCEIVER_POOL).tickrate(tickrate)
                .exceptionHandler(exceptionHandler).build(exceptionHandler.operate(socket::getOutputStream, "Could not get output"));

        this.receiver = new ScheduledPacketReceiver(exceptionHandler.operate(socket::getInputStream, "Could not get input"),
                tickrate, TRANSCEIVER_POOL, exceptionHandler, this::packetReceived);

        this.registerOperation(transmitter);
        this.registerOperation(receiver);
        this.queueOperations();
    }

    private PacketReceiveEvent wrapEvent(TransceivablePacket transceivablePacket) {
        return new PacketReceiveEvent(transceivablePacket.id(), this.parsePacket(transceivablePacket));
    }

    private void packetReceived(TransceivablePacket transceivablePacket) {
        this.eventHandler.notify(this.wrapEvent(transceivablePacket));
    }

    private TransceivablePacket parsePacket(long packetId, Packet packet) {
        return new IoTransceivablePacket(new CachedIOPacket(packetId, this.wrapper.wrap(packet)));
    }

    private Packet parsePacket(TransceivablePacket packet) {
        return this.wrapper.unwrap(packet.id(), packet.data());
    }

    @Override
    public int hashCode() {
        return this.getAddress().hashCode();
    }

    @Override
    public Operation send(Packet packet) {
        long packetId = this.registry.parseId(packet.getClass());
        if (this.eventHandler.isCancelled(new PacketSendEvent(packetId, packet))) return null;
        return new PacketSendOperation(this.transmitter, this.parsePacket(packetId, packet));
    }

    @Override
    public Operation close() {
        if (this.socket.isClosed()) throw new IllegalStateException("Socket already closed");
        return new SocketCloseOperation(this.socket, this.threadedOperations, this.exceptionHandler);
    }

    @Override
    public boolean isClosed() {
        return this.socket.isClosed();
    }

    @Override
    public PacketReceiveEvent awaitPacket(long timeoutInMillis) {
        return this.wrapEvent(this.receiver.awaitPacket(timeoutInMillis));
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
