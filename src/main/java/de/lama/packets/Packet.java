package de.lama.packets;

/**
 * Immutable
 */
public class Packet implements Cloneable {

    public static final String VERSION = "v0.1";

    private long id;

    public Packet(long id) {
        this.id = id;
    }

    public long getId() {
        return this.id;
    }

    @Override
    public Packet clone() {
        try {
            Packet clone = (Packet) super.clone();
            clone.id = this.id;
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
