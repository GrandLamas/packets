package de.lama.packets.transceiver.transmitter;

import de.lama.packets.Packet;
import de.lama.packets.transceiver.PacketTransceiver;

public interface PacketTransmitter extends PacketTransceiver {

    void queue(Packet packet);

    void complete(Packet packet);

}
