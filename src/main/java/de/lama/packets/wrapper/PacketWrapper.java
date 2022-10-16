package de.lama.packets.wrapper;

import de.lama.packets.Packet;
import de.lama.packets.registry.RegistryContainer;

public interface PacketWrapper extends RegistryContainer {

    byte[] wrap(Packet packet);

    Packet unwrap(long packetId, byte[] bytes);

}
