package de.lama.packets;

public class MessagePacket extends Packet {

    public static final long ID = 1;

    private final String message;

    public MessagePacket(String message) {
        super(ID);

        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
