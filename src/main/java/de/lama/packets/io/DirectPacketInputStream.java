package de.lama.packets.io;

import de.lama.packets.Packet;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

public class DirectPacketInputStream extends InputStream implements PacketInputStream {

    private final DataInputStream in;

    public DirectPacketInputStream(DataInputStream in) {
        this.in = in;
    }

    @Override
    public IOPacket readPacket() throws IOException {
        char type = this.in.readChar();
        if (type != Packet.TYPE) return null;
        long packetId = this.in.readLong();
        int length = this.in.readInt();
        byte[] buffer = new byte[length];

        int read = 0;
        while (read < length) {
            read += this.in.read(buffer, read, buffer.length - read);
        }

        return new CachedIOPacket(type, packetId, length, buffer);
    }

    @Override
    public int available() throws IOException {
        return this.in.available();
    }

    @Override
    public int read() throws IOException {
        return this.in.read();
    }

    @Override
    public void close() throws IOException {
        this.in.close();
    }
}
