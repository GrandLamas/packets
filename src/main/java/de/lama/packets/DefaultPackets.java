package de.lama.packets;

public enum DefaultPackets {

    HANDSHAKE(HandshakePacket.ID, HandshakePacket.class);

    private final long id;
    private final Class<? extends Packet> clazz;

    DefaultPackets(long id, Class<? extends Packet> clazz) {
        this.id = id;
        this.clazz = clazz;
    }

    public long getId() {
        return this.id;
    }

    public Class<? extends Packet> getClazz() {
        return this.clazz;
    }
}
