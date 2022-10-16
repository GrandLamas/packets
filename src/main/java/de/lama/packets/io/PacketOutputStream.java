package de.lama.packets.io;

import java.io.IOException;

public interface PacketOutputStream {

    void write(IoPacket packet) throws IOException;

}
