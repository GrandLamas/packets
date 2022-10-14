package de.lama.packets.wrapper;

import de.lama.packets.Packet;

public interface PacketWrapper {

    byte[] wrap(Packet packet);

    Packet unwrap(byte[] bytes);

}
