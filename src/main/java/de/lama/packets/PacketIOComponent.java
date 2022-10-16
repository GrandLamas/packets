package de.lama.packets;

import de.lama.packets.event.EventHandlerContainer;
import de.lama.packets.operation.Operation;
import de.lama.packets.registry.RegistryContainer;

public interface PacketIOComponent extends RegistryContainer, EventHandlerContainer {

    Operation close();

    boolean isClosed();

    int getPort();

}
