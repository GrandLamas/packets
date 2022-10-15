package de.lama.packets.transceiver.receiver;

import de.lama.packets.Packet;
import de.lama.packets.transceiver.PacketTransceiver;

public interface PacketReceiver extends PacketTransceiver {

    Packet awaitPacket(long timeoutInMillis);

}
