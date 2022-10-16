package de.lama.packets.transceiver.receiver;

import de.lama.packets.transceiver.PacketTransceiver;
import de.lama.packets.transceiver.TransceivablePacket;

public interface PacketReceiver extends PacketTransceiver {

    TransceivablePacket awaitPacket(long timeoutInMillis);

}
