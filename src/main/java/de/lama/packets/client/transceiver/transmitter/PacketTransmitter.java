package de.lama.packets.client.transceiver.transmitter;

import de.lama.packets.client.transceiver.TransceivablePacket;
import de.lama.packets.client.transceiver.PacketTransceiver;

public interface PacketTransmitter extends PacketTransceiver {

    void queue(TransceivablePacket packet);

    void complete(TransceivablePacket packet);

}
