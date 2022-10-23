package de.lama.packets.wrapper;

import de.lama.packets.Packet;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public interface PacketStringWrapper extends PacketWrapper {

    Charset CHARSET = StandardCharsets.UTF_8;

    @Override
    default Packet unwrap(long packetId, byte[] bytes) {
        return this.unwrapString(packetId, new String(bytes, CHARSET));
    }

    @Override
    default byte[] wrap(long packetId, Packet packet) {
        return this.wrapString(packetId, packet).getBytes(CHARSET);
    }

    String wrapString(long packetId, Packet packet);

    Packet unwrapString(long packetId, String from);

}
