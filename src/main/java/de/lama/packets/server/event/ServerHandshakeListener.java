package de.lama.packets.server.event;

import de.lama.packets.HandshakePacket;
import de.lama.packets.Packet;
import de.lama.packets.server.exception.ServerException;

import java.util.function.Consumer;

public class ServerHandshakeListener implements Consumer<ClientConnectEvent> {

    private static final long HANDSHAKE_TIMEOUT = 5000;

    @Override
    public void accept(ClientConnectEvent event) {
        event.client().send(new HandshakePacket(Packet.VERSION)).complete();
        Packet packet = event.client().awaitPacket(HANDSHAKE_TIMEOUT);
        if (packet.getId() != HandshakePacket.ID) {
            event.setCancelled(true);
            throw new ServerException("Failed handshake");
        }

        HandshakePacket handshake = (HandshakePacket) packet;
        if (!handshake.getVersion().equals(Packet.VERSION)) {
            event.setCancelled(true);
            throw new ServerException("Invalid version for client " + event.client().getAddress().toString());
        }
    }
}
