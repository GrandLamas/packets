package de.lama.packets.transceiver.receiver;

import de.lama.packets.transceiver.PacketTransceiver;
import de.lama.packets.transceiver.TransceivablePacket;

import java.util.UUID;

public interface PacketReceiver extends PacketTransceiver {

    TransceivablePacket awaitPacket(long timeoutInMillis);

    UUID subscribe(PacketConsumer consumer);

    boolean unsubscribe(UUID uuid);

}
