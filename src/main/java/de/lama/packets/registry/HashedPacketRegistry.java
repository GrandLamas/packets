package de.lama.packets.registry;

import de.lama.packets.Packet;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class HashedPacketRegistry extends AbstractRegistry implements PacketRegistry {

    private final Map<Long, Class<? extends Packet>> registry;
    private final Map<Class<? extends Packet>, Long> reverseRegistry;

    public HashedPacketRegistry() {
        this.registry = new HashMap<>();
        this.reverseRegistry = new HashMap<>();
    }

    @Override
    public <T extends Packet> void registerPacket(long id, Class<T> clazz) {
        if (Objects.equals(this.registry.get(id), clazz)) throw new IllegalArgumentException("Id already taken");
        this.registry.put(id, clazz);
        this.reverseRegistry.put(clazz, id);
    }

    @Override
    public Class<? extends Packet> parseClass(long id) {
        return this.registry.get(id);
    }

    @Override
    public long parseId(Class<? extends Packet> clazz) {
        return this.reverseRegistry.get(clazz);
    }
}
