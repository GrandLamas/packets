package de.lama.packets.client;

import de.lama.packets.HandshakePacket;
import de.lama.packets.Packet;
import de.lama.packets.client.transceiver.TransceivablePacket;
import de.lama.packets.client.transceiver.receiver.PacketConsumer;

import java.util.function.Consumer;
import java.util.function.Function;

public class HandshakeListener implements PacketConsumer {

    private final Function<TransceivablePacket, Packet> wrapper;
    private final Consumer<Boolean> onHandshake;

    public HandshakeListener(Function<TransceivablePacket, Packet> wrapper, Consumer<Boolean> onHandshake) {
        this.wrapper = wrapper;
        this.onHandshake = onHandshake;
    }

    @Override
    public void accept(TransceivablePacket packet) {
        if (packet.id() != HandshakePacket.ID) {
            this.onHandshake.accept(false);
            return;
        }

        HandshakePacket handshake = (HandshakePacket) this.wrapper.apply(packet);
        if (!handshake.version().equals(Packet.VERSION)) {
            this.onHandshake.accept(false);
            return;
        }

        this.onHandshake.accept(true);
    }
}
