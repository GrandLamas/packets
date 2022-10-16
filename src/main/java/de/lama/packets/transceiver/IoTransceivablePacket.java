package de.lama.packets.transceiver;

import de.lama.packets.io.IOPacket;

public record IoTransceivablePacket(IOPacket packet) implements TransceivablePacket {

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
