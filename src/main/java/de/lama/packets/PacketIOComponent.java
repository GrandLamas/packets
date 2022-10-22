package de.lama.packets;

import de.lama.packets.event.EventHandlerContainer;
import de.lama.packets.operation.Operation;
import de.lama.packets.registry.RegistryContainer;

public interface PacketIOComponent extends EventHandlerContainer, RegistryContainer {

    Operation close();

    boolean isClosed();

    int getPort();

}
