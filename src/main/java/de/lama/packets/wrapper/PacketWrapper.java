package de.lama.packets.wrapper;

import de.lama.packets.Packet;

public interface PacketWrapper {

    byte[] wrap(long packetId, Packet packet);

    Packet unwrap(long packetId, byte[] bytes);

}
