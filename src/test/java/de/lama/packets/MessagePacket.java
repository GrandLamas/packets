package de.lama.packets;

public class MessagePacket implements Packet {

    public static final long ID = 0;

    private final String message;

    public MessagePacket(String msg) {
        this.message = msg;
    }

    public String getMessage() {
        return this.message;
    }
}
