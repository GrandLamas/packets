package de.lama.packets.client;

import de.lama.packets.HandshakePacket;
import de.lama.packets.Packet;
import de.lama.packets.event.events.PacketReceiveEvent;

import java.util.function.Consumer;

public class HandshakeListener implements Consumer<PacketReceiveEvent> {

    private final Consumer<Boolean> onHandshake;

    public HandshakeListener(Consumer<Boolean> onHandshake) {
        this.onHandshake = onHandshake;
    }

    @Override
    public void accept(PacketReceiveEvent event) {
        if (event.packetId() != HandshakePacket.ID) {
            this.onHandshake.accept(false);
            return;
        }

        if (!((HandshakePacket) event.packet()).version().equals(Packet.VERSION)) {
            this.onHandshake.accept(false);
            return;
        }

        this.onHandshake.accept(true);
    }
}
