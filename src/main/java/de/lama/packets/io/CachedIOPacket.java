package de.lama.packets.io;

import de.lama.packets.Packet;

public record CachedIOPacket(char type, long id, int size, byte[] data) implements IOPacket {

    public CachedIOPacket(long id, byte[] data) {
        this(Packet.TYPE, id, data.length, data);
    }
}
