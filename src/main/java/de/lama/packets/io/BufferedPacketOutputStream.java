package de.lama.packets.io;

import java.io.*;

public class BufferedPacketOutputStream extends OutputStream implements PacketOutputStream {

    private final DataOutputStream out;

    public BufferedPacketOutputStream(OutputStream out) {
        this.out = new DataOutputStream(new BufferedOutputStream(out));
    }

    @Override
    public void write(IoPacket packet) throws IOException {
        // HEAD
        this.out.writeChar(packet.type());
        this.out.writeLong(packet.id());
        this.out.writeInt(packet.size());

        // DATA
        this.out.write(packet.data());
    }

    @Override
    public void write(int b) throws IOException {
        this.out.write(b);
    }

    @Override
    public void close() throws IOException {
        this.out.close();
    }

    @Override
    public void flush() throws IOException {
        this.out.flush();
    }
}
