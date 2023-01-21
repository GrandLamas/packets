package de.lama.packets.large;

import de.lama.packets.Packet;

public record LargePacket(byte[] bytes) implements Packet {

    public LargePacket() {
        this(new byte[1000000 * 100]); // 100 MB
    }
}
