package de.lama.packets.io;

import de.lama.packets.Packet;

public record CachedIoPacket(char type, long id, int size, byte[] data) implements IoPacket {

    public CachedIoPacket(long id, byte[] data) {
        this(Packet.TYPE, id, data.length, data);
    }
}
