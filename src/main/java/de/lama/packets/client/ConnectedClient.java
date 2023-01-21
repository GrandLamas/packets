package de.lama.packets.client;

import de.lama.packets.AbstractNetworkAdapter;
import de.lama.packets.Packet;
import de.lama.packets.client.transceiver.IoTransceivablePacket;
import de.lama.packets.client.transceiver.TransceivablePacket;
import de.lama.packets.client.transceiver.receiver.PacketReceiver;
import de.lama.packets.client.transceiver.transmitter.PacketTransmitter;
import de.lama.packets.event.events.PacketReceiveEvent;
import de.lama.packets.event.events.PacketSendEvent;
import de.lama.packets.io.CachedIoPacket;
import de.lama.packets.operation.Operation;
import de.lama.packets.operation.SimpleOperation;
import de.lama.packets.registry.PacketRegistry;
import de.lama.packets.util.exception.ExceptionHandler;
import de.lama.packets.wrapper.PacketWrapper;

import java.net.InetAddress;
import java.net.Socket;

class ConnectedClient extends AbstractNetworkAdapter implements Client {

    private final Socket socket;
    private final PacketWrapper wrapper;
    private final PacketTransmitter transmitter;
    private final PacketReceiver receiver;

    public ConnectedClient(Socket socket, PacketRegistry registry, PacketWrapper wrapper, PacketTransmitter transmitter,
                           PacketReceiver receiver, ExceptionHandler exceptionHandler) {
        super(exceptionHandler, registry);
        this.socket = socket;
        this.wrapper = wrapper;
        this.transmitter = transmitter;
        this.receiver = receiver;

        this.receiver.subscribe(this::packetReceived);
        this.transmitter.start();
    }

    private PacketReceiveEvent wrapEvent(TransceivablePacket transceivablePacket) {
        return new PacketReceiveEvent(this, transceivablePacket.id(), this.parsePacket(transceivablePacket));
    }

    private void packetReceived(TransceivablePacket transceivablePacket) {
        this.getEventHandler().notify(this.wrapEvent(transceivablePacket));
    }

    protected Packet parsePacket(TransceivablePacket packet) {
        return this.wrapper.unwrap(packet.id(), packet.data());
    }

    protected TransceivablePacket parsePacket(long packetId, Packet packet) {
        return new IoTransceivablePacket(new CachedIoPacket(packetId, this.wrapper.wrap(packetId, packet)));
    }

    @Override
    protected void executeOpen() {
        this.receiver.start();
    }

    @Override
    protected void executeClose() {
        this.receiver.stop();
    }

    @Override
    protected void executeShutdown() {
        this.transmitter.stop();
        this.getExceptionHandler().operate(this.socket::close, "Failed to close socket");
    }

    @Override
    public int hashCode() {
        return this.socket.hashCode();
    }

    @Override
    public Operation send(Packet packet) {
        return new SimpleOperation((async) -> {
            if (this.hasShutdown()) {
                this.handle(new IllegalStateException("Client already closed"));
                return;
            }

            long packetId = this.getRegistry().parseId(packet.getClass());
            if (this.getEventHandler().isCancelled(new PacketSendEvent(this, packetId, packet))) return;
            TransceivablePacket transceivablePacket = this.parsePacket(packetId, packet);
            if (async) this.transmitter.queue(transceivablePacket);
            else this.transmitter.complete(transceivablePacket);
        });
    }

    @Override
    public boolean isClosed() {
        return this.hasShutdown() || this.receiver == null || !this.receiver.isRunning();
    }

    @Override
    public boolean hasShutdown() {
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
