package de.lama.packets;

public record HandshakePacket(String version) implements Packet {

    public static final long ID = 69420;

}
