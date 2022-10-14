package de.lama.packets.registry;

import de.lama.packets.Packet;

import java.util.HashMap;
import java.util.Map;

public class HashedPacketRegistry extends AbstractRegistry implements PacketRegistry {

    private final Map<Long, Class<? extends Packet>> registry;

    public HashedPacketRegistry() {
        this.registry = new HashMap<>();
        this.registerDefaults();
    }

    @Override
    public <T extends Packet> void registerPacket(long id, Class<T> clazz) {
        if (this.registry.containsKey(id)) throw new IllegalArgumentException("Id already taken");
        this.registry.put(id, clazz);
    }

    @Override
    public Class<? extends Packet> parseId(long id) {
        return this.registry.get(id);
    }
}
