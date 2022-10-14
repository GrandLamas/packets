package de.lama.packets.registry;

import de.lama.packets.Packet;

public interface PacketRegistry {

    /**
     * Registers a packet.
     * Throws IllegalArgumentException if id already taken.
     *
     * @param id The id
     * @param clazz The class
     * @param <T> The type of packet
     */
    <T extends Packet> void registerPacket(long id, Class<T> clazz);

    Class<? extends Packet> parseId(long id);

}
