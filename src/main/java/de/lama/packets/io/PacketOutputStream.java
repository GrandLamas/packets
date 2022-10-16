package de.lama.packets.io;

import java.io.IOException;

public interface PacketOutputStream {

    void write(IOPacket packet) throws IOException;

}
