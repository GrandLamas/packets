package de.lama.packets;

public class HandshakePacket implements Packet {

    public static final long ID = 0;

    private final String version;

    public HandshakePacket(String version) {
        this.version = version;
    }

    public String getVersion() {
        return this.version;
    }
}
