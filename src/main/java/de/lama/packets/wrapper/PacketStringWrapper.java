package de.lama.packets.wrapper;

import de.lama.packets.Packet;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public interface PacketStringWrapper extends PacketWrapper {

    Charset CHARSET = StandardCharsets.UTF_8;

    @Override
    default Packet unwrap(byte[] bytes) {
        return this.unwrapString(new String(bytes, CHARSET));
    }

    @Override
    default byte[] wrap(Packet packet) {
        return this.wrapString(packet).getBytes(CHARSET);
    }

    String wrapString(Packet packet);

    Packet unwrapString(String from);

}
