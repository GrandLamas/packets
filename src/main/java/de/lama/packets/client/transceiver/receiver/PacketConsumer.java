package de.lama.packets.client.transceiver.receiver;

import de.lama.packets.client.transceiver.TransceivablePacket;

import java.util.function.Consumer;

public interface PacketConsumer extends Consumer<TransceivablePacket> {

}
