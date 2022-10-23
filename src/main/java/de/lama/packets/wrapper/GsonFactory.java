package de.lama.packets.wrapper;

import de.lama.packets.registry.PacketRegistry;

public class GsonFactory implements WrapperFactory {

    @Override
    public PacketWrapper create(PacketRegistry registry) {
        return new CachedGsonWrapper(registry);
    }
}
