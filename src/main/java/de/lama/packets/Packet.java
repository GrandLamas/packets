package de.lama.packets;

/**
 * Immutable
 */
public interface Packet extends Cloneable {

    String VERSION = "0.1";
    char TYPE = 'p';
    int PORT = 4999;
}
