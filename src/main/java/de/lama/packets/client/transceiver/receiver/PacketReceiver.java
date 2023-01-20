package de.lama.packets.client.transceiver.receiver;

import de.lama.packets.client.transceiver.PacketTransceiver;
import de.lama.packets.client.transceiver.TransceivablePacket;

import java.util.UUID;

public interface PacketReceiver extends PacketTransceiver {

    TransceivablePacket awaitPacket(long timeoutInMillis);

    UUID subscribe(PacketConsumer consumer);

    boolean unsubscribe(UUID uuid);

}
