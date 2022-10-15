package de.lama.packets.io;

import de.lama.packets.Packet;
import de.lama.packets.wrapper.PacketWrapper;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

public class PacketInputStream extends InputStream {

    private final DataInputStream in;
    private final PacketWrapper wrapper;

    public PacketInputStream(DataInputStream in, PacketWrapper wrapper) {
        this.in = in;
        this.wrapper = wrapper;
    }

    public Packet readPacket() throws IOException {
        char type = this.in.readChar();
        if (type != Packet.TYPE) return null;
        int length = this.in.readInt();
        byte[] buffer = new byte[length];

        int read = 0;
        while (read < length) {
            read += this.in.read(buffer, read, buffer.length - read);
        }

        return this.wrapper.unwrap(buffer);
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
