package de.lama.packets.server.client;

import de.lama.packets.Packet;

interface PacketTransmitter {

    void queue(Packet packet);

    void complete(Packet packet);

}
