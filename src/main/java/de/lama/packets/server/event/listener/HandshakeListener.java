package de.lama.packets.server.event.listener;

import de.lama.packets.HandshakePacket;
import de.lama.packets.Packet;
import de.lama.packets.server.event.ServerClientConnectEvent;
import de.lama.packets.server.exception.ServerException;

import java.util.function.Consumer;

public class HandshakeListener implements Consumer<ServerClientConnectEvent> {

    private static final long HANDSHAKE_TIMEOUT = 5000;

    @Override
    public void accept(ServerClientConnectEvent event) {
        event.client().send(new HandshakePacket(Packet.VERSION)).complete();
        Packet packet = event.client().awaitPacket(HANDSHAKE_TIMEOUT);
        if (!(packet instanceof HandshakePacket handshake)) {
            event.setCancelled(true);
            throw new ServerException("Failed handshake");
        }

        if (!handshake.getVersion().equals(Packet.VERSION)) {
            event.setCancelled(true);
            throw new ServerException("Invalid version for client " + event.client().getAddress().toString());
        }
    }
}
