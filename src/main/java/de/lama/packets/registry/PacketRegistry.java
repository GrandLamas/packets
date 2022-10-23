package de.lama.packets.registry;

import de.lama.packets.Packet;

public interface PacketRegistry {

    /**
     * Registers a packet.
     *
     * @param id The id
     * @param clazz The class
     * @param <T> The type of packet
     */
    <T extends Packet> boolean registerPacket(long id, Class<T> clazz);

    Class<? extends Packet> parseClass(long id);

    long parseId(Class<? extends Packet> clazz);

}
