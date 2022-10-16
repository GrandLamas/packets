package de.lama.packets.registry;

import de.lama.packets.DefaultPackets;

import java.util.Arrays;

public abstract class AbstractRegistry implements PacketRegistry {

    protected void registerDefaults() {
        Arrays.stream(DefaultPackets.values()).forEach(packet -> this.registerPacket(packet.getId(), packet.getClazz()));
    }
}
