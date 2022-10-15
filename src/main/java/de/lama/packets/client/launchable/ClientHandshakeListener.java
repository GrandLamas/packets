package de.lama.packets.client.launchable;

import de.lama.packets.client.Client;
import de.lama.packets.HandshakePacket;
import de.lama.packets.Packet;
import de.lama.packets.event.PacketReceiveEvent;

import java.util.function.Consumer;

public class ClientHandshakeListener implements Consumer<PacketReceiveEvent> {

    private final Client client;

    public ClientHandshakeListener(Client client) {
        this.client = client;
    }

    @Override
    public void accept(PacketReceiveEvent event) {
        if (event.packet().getId() != HandshakePacket.ID) return;
        this.client.send(new HandshakePacket(Packet.VERSION)).complete();

        HandshakePacket handshake = (HandshakePacket) event.packet();
        if (!handshake.getVersion().equals(Packet.VERSION)) this.client.close().complete();
    }
}
