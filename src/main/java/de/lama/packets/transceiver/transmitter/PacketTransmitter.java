package de.lama.packets.transceiver.transmitter;

import de.lama.packets.transceiver.PacketTransceiver;
import de.lama.packets.transceiver.TransceivablePacket;

public interface PacketTransmitter extends PacketTransceiver {

    void queue(TransceivablePacket packet);

    void complete(TransceivablePacket packet);

}
