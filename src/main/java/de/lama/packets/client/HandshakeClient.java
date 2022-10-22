package de.lama.packets.client;

import de.lama.packets.Handshake;
import de.lama.packets.HandshakePacket;
import de.lama.packets.Packet;
import de.lama.packets.operation.Operation;
import de.lama.packets.registry.PacketRegistry;
import de.lama.packets.util.ExceptionHandler;
import de.lama.packets.wrapper.PacketWrapper;

import java.net.Socket;

class HandshakeClient extends ThreadedClient {

    private boolean handshake;

    public HandshakeClient(Socket socket, PacketRegistry registry, PacketWrapper wrapper, int tickrate, ExceptionHandler exceptionHandler) {
        super(socket, registry, wrapper, tickrate, exceptionHandler);

        new Handshake(this).queue();
    }

    public void awaitHandshake() throws InterruptedException {
        synchronized (this) {
            this.wait();
        }
    }

    @Override
    public Operation send(Packet packet) {
        long packetId = this.getRegistry().parseId(packet.getClass());
        if (packetId != HandshakePacket.ID && !this.handshake) {
            if (this.exceptionHandler.operate(this::awaitHandshake, "Could not wait for packet"))
                this.handshake = true;
        }

        return super.send(packet);
    }
}
