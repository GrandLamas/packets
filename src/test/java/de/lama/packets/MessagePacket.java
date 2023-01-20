package de.lama.packets;

public record MessagePacket(String message) implements Packet {

    public static final long ID = 0;

    @Override
    public int hashCode() {
        return this.message.hashCode();
    }
}
