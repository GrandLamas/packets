package de.lama.packets;

import de.lama.packets.client.Client;
import de.lama.packets.event.events.PacketReceiveEvent;
import de.lama.packets.operation.Operation;
import de.lama.packets.server.exception.ServerException;

import java.util.UUID;
import java.util.function.Consumer;

public class Handshake implements Operation, Consumer<PacketReceiveEvent> {

    private final Client client;
    private final Runnable onHandshake;
    private UUID listenerId;

    public Handshake(Client client, Runnable onHandshake) {
        this.client = client;
        this.onHandshake = onHandshake;
    }

    private void registerListener() {
        this.listenerId = this.client.getEventHandler().subscribe(PacketReceiveEvent.class, this);
    }

    private Packet buildPacket() {
        return new HandshakePacket(Packet.VERSION);
    }

    @Override
    public void accept(PacketReceiveEvent event) {
        if (event.packetId() != HandshakePacket.ID) {
            this.client.close().complete();
            throw new ServerException("Failed handshake");
        }

        HandshakePacket handshake = (HandshakePacket) event.packet();
        if (!handshake.getVersion().equals(Packet.VERSION)) {
            this.client.close().complete();
            throw new ServerException("Invalid version for client " + this.client.getAddress().toString());
        }

        this.client.getEventHandler().unsubscribe(this.listenerId);
        this.onHandshake.run();
    }

    @Override
    public Operation queue() {
        this.registerListener();
        return this.client.send(this.buildPacket()).queue();
    }

    @Override
    public Operation complete() {
        this.registerListener();
        return this.client.send(this.buildPacket()).complete();
    }
}
