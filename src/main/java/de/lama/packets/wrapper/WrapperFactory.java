package de.lama.packets.wrapper;

import de.lama.packets.registry.PacketRegistry;

public interface WrapperFactory {

    PacketWrapper create(PacketRegistry registry);

}
