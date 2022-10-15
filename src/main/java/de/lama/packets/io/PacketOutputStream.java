package de.lama.packets.io;

import de.lama.packets.Packet;
import de.lama.packets.wrapper.PacketWrapper;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class PacketOutputStream extends OutputStream {

    private final DataOutputStream out;
    private final PacketWrapper wrapper;

    public PacketOutputStream(DataOutputStream out, PacketWrapper wrapper) {
        this.out = out;
        this.wrapper = wrapper;
    }

    @Override
    public void write(int b) throws IOException {
        this.out.write(b);
    }

    public void write(Packet packet) throws IOException {
        byte[] data = this.wrapper.wrap(packet);
        this.out.writeChar(Packet.TYPE);
        this.out.writeInt(data.length);
        this.out.write(data);
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
