package de.lama.packets.io;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class PacketOutputStream extends OutputStream {

    private final DataOutputStream out;

    public PacketOutputStream(DataOutputStream out) {
        this.out = out;
    }

    public void write(IOPacket packet) throws IOException {
        this.out.writeChar(packet.type());
        this.out.writeLong(packet.id());
        this.out.writeInt(packet.size());
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
