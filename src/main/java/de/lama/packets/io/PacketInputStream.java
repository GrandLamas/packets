package de.lama.packets.io;

import java.io.IOException;

public interface PacketInputStream {

    IoPacket readPacket() throws IOException;

}
