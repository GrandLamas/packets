package de.lama.packets.registry;

import de.lama.packets.Packet;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class HashedPacketRegistry implements PacketRegistry {

    private final Map<Long, Class<? extends Packet>> registry;
    private final Map<Class<? extends Packet>, Long> reverseRegistry;

    public HashedPacketRegistry(RegistryEntry<?>... entries) {
        this.registry = new HashMap<>();
        this.reverseRegistry = new HashMap<>();

        Arrays.stream(entries).forEach(e -> this.registerPacket(e.id(), e.clazz()));
    }

    @Override
    public <T extends Packet> boolean registerPacket(long id, Class<T> clazz) {
        if (Objects.equals(this.registry.get(id), clazz)) return false;
        this.registry.put(id, clazz);
        this.reverseRegistry.put(clazz, id);
        return true;
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
