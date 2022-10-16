package de.lama.packets.transceiver.receiver;

import de.lama.packets.transceiver.TransceivablePacket;

import java.util.function.Consumer;

public interface PacketConsumer extends Consumer<TransceivablePacket> {

}
