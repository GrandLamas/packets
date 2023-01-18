package de.lama.packets;

/**
 * Needs to be immutable.
 */
public interface Packet extends Cloneable {

    String VERSION = "0.1.3";
    char TYPE = 'p';
}
