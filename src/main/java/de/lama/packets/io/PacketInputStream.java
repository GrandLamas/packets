package de.lama.packets.io;

import java.io.IOException;

public interface PacketInputStream {

    IOPacket readPacket() throws IOException;

}
