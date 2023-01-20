package de.lama.packets.client.transceiver;

import de.lama.packets.io.IoPacket;

public record IoTransceivablePacket(IoPacket packet) implements TransceivablePacket {

    @Override
    public char type() {
        return this.packet.type();
    }

    @Override
    public long id() {
        return this.packet.id();
    }

    @Override
    public int size() {
        return this.packet.size();
    }

    @Override
    public byte[] data() {
        return this.packet.data();
    }
}
