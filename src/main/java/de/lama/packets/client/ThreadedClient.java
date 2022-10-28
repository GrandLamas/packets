package de.lama.packets.client;

import de.lama.packets.AbstractPacketIOComponent;
import de.lama.packets.DefaultPackets;
import de.lama.packets.Packet;
import de.lama.packets.event.events.PacketReceiveEvent;
import de.lama.packets.event.events.PacketSendEvent;
import de.lama.packets.io.CachedIoPacket;
import de.lama.packets.operation.Operation;
import de.lama.packets.operation.operations.PacketSendOperation;
import de.lama.packets.operation.operations.SocketCloseOperation;
import de.lama.packets.registry.PacketRegistry;
import de.lama.packets.transceiver.IoTransceivablePacket;
import de.lama.packets.transceiver.TransceivablePacket;
import de.lama.packets.transceiver.receiver.PacketReceiver;
import de.lama.packets.transceiver.transmitter.PacketTransmitter;
import de.lama.packets.util.ExceptionHandler;
import de.lama.packets.wrapper.PacketWrapper;

import java.net.InetAddress;
import java.net.Socket;
import java.util.Arrays;

public class ThreadedClient extends AbstractPacketIOComponent implements Client {

    private final Socket socket;
    private final PacketWrapper wrapper;
    private final PacketTransmitter transmitter;
    private final PacketReceiver receiver;

    public ThreadedClient(Socket socket, PacketRegistry registry, PacketWrapper wrapper, PacketTransmitter transmitter,
                          PacketReceiver receiver, ExceptionHandler exceptionHandler) {
        super(exceptionHandler, registry);
        this.socket = socket;
        this.wrapper = wrapper;
        this.transmitter = transmitter;
        this.receiver = receiver;

        this.receiver.subscribe(this::packetReceived);
        this.start();
    }

    private void start() {
        this.registerPackets();
        this.registerTask(this.transmitter);
        this.registerTask(this.receiver);
        this.queueOperations();
    }

    private void registerPackets() {
        Arrays.stream(DefaultPackets.values()).forEach(packet -> this.registry.registerPacket(packet.getId(), packet.getClazz()));
    }

    private PacketReceiveEvent wrapEvent(TransceivablePacket transceivablePacket) {
        return new PacketReceiveEvent(transceivablePacket.id(), this.parsePacket(transceivablePacket));
    }

    private void packetReceived(TransceivablePacket transceivablePacket) {
        this.eventHandler.notify(this.wrapEvent(transceivablePacket));
    }

    private TransceivablePacket parsePacket(long packetId, Packet packet) {
        return new IoTransceivablePacket(new CachedIoPacket(packetId, this.wrapper.wrap(packetId, packet)));
    }

    private Packet parsePacket(TransceivablePacket packet) {
        return this.wrapper.unwrap(packet.id(), packet.data());
    }

    @Override
    public int hashCode() {
        return this.socket.hashCode();
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
        return new SocketCloseOperation(this.socket, this.repeatingOperations, this.exceptionHandler);
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
