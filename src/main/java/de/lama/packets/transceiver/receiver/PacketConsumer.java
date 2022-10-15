package de.lama.packets.transceiver.receiver;

import de.lama.packets.Packet;

import java.util.function.Consumer;

public interface PacketConsumer extends Consumer<Packet> {
}
