package de.lama.packets.client;

import de.lama.packets.HandshakePacket;
import de.lama.packets.Packet;
import de.lama.packets.operation.Operation;
import de.lama.packets.registry.PacketRegistry;
import de.lama.packets.transceiver.receiver.PacketReceiver;
import de.lama.packets.transceiver.transmitter.PacketTransmitter;
import de.lama.packets.util.exception.ExceptionHandler;
import de.lama.packets.wrapper.PacketWrapper;

import java.net.Socket;
import java.util.UUID;

class HandshakeClient extends ThreadedClient {

    private boolean handshake;
    private UUID handshakeSubscription;

    public HandshakeClient(Socket socket, PacketRegistry registry, PacketWrapper wrapper, PacketTransmitter transmitter, PacketReceiver receiver, ExceptionHandler exceptionHandler) {
        super(socket, registry, wrapper, transmitter, receiver, exceptionHandler);

        this.getReceiver().subscribe(new HandshakeListener(this::parsePacket, (succeeded) -> this.handshakeReceived()));
    }

    private void handshakeReceived() {
        this.getReceiver().unsubscribe(this.handshakeSubscription);
        this.handshake = true;
        this.handshakeSubscription = null;
        synchronized (this) {
            this.notifyAll();
        }
    }

    public void awaitHandshake() throws InterruptedException {
        synchronized (this) {
            this.wait();
        }
    }

    @Override
    public Operation send(Packet packet) {
        long packetId = this.registry.parseId(packet.getClass());
        if (packetId != HandshakePacket.ID && !this.handshake) {
            this.exceptionHandler.operate(this::awaitHandshake, "Could not wait for handshake");
        }

        return super.send(packet);
    }

    @Override
    public Operation open() {
        if (this.handshakeSubscription != null) {
            throw new IllegalStateException("Already connecting...");
        }

        this.getTransmitter().complete(this.parsePacket(HandshakePacket.ID, new HandshakePacket(HandshakePacket.VERSION)));
        return null;
    }
}
