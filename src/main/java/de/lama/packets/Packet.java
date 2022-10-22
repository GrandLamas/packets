package de.lama.packets;

/**
 * Immutable
 */
public interface Packet extends Cloneable {

    String VERSION = "0.1.2";
    char TYPE = 'p';
    int PORT = 4999;
}
