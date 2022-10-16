package de.lama.packets.io;

public interface IOPacket {

    char type();

    long id();

    int size();

    byte[] data();

}
