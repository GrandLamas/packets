package de.lama.packets.registry;

import de.lama.packets.Packet;

public interface PacketRegistry {

    /**
     * Registers a packet.
     * Throws IllegalArgumentException if id already taken by another class.
     *
     * @param id The id
     * @param clazz The class
     * @param <T> The type of packet
     */
    <T extends Packet> void registerPacket(long id, Class<T> clazz);

    Class<? extends Packet> parseClass(long id);

    long parseId(Class<? extends Packet> clazz);

}
