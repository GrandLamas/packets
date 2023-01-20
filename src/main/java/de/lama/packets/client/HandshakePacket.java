package de.lama.packets.client;

import de.lama.packets.Packet;

public record HandshakePacket(String version) implements Packet {

    public static final long ID = 69420;

}
