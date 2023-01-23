package de.lama.packets.large;

import de.lama.packets.Packet;

public record LargePacket(byte[] bytes) implements Packet {

    public static final long ID = 5;

    public LargePacket(int sizeInMb) {
        this(new byte[1000000 * sizeInMb]); // 100 MB
    }
}
