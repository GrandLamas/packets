package de.lama.packets.wrapper;

import de.lama.packets.Packet;
import de.lama.packets.registry.PacketRegistry;

public abstract class AbstractPacketWrapper {

    private final PacketRegistry registry;

    public AbstractPacketWrapper(PacketRegistry registry) {
        this.registry = registry;
    }

    protected Class<? extends Packet> parse(long id) {
        return this.registry.parseId(id);
    }
}
