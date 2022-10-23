package de.lama.packets;

/**
 * Needs to be immutable.
 */
public interface Packet extends Cloneable {

    String VERSION = "0.1.2";
    char TYPE = 'p';
}
