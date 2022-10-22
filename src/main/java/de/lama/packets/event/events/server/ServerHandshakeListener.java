package de.lama.packets.event.events.server;

import de.lama.packets.HandshakePacket;
import de.lama.packets.Packet;
import de.lama.packets.client.Client;
import de.lama.packets.event.events.PacketReceiveEvent;
import de.lama.packets.server.exception.ServerException;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class ServerHandshakeListener implements Consumer<ClientConnectEvent> {

    private static final long HANDSHAKE_TIMEOUT = 5000;

    private final Map<Client, UUID> handshakeListener = new ConcurrentHashMap<>();

    private void processClientPacket(Client client, PacketReceiveEvent event) {
        if (event.packetId() != HandshakePacket.ID) {
            client.close().complete();
            throw new ServerException("Failed handshake");
        }

        HandshakePacket handshake = (HandshakePacket) event.packet();
        if (!handshake.getVersion().equals(Packet.VERSION)) {
            client.close().complete();
            throw new ServerException("Invalid version for client " + client.getAddress().toString());
        }

        client.getEventHandler().unsubscribe(this.handshakeListener.get(client));
    }

    @Override
    public void accept(ClientConnectEvent event) {
        UUID eventId = event.client().getEventHandler().subscribe(PacketReceiveEvent.class, (r) -> this.processClientPacket(event.client(), r));
        this.handshakeListener.put(event.client(), eventId);
        event.client().send(new HandshakePacket(Packet.VERSION)).complete();
    }
}
