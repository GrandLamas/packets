package de.lama.packets.wrapper.cache;

import de.lama.packets.Packet;

public interface PacketCache {

    void cache(long id, byte[] data, Packet packet);

    Packet load(long id, byte[] data);

    byte[] load(long id, Packet packet);

}
