package de.lama.packets.io;

public interface IoPacket {

    char type();

    long id();

    int size();

    byte[] data();

}
