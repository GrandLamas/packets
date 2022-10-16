package de.lama.packets;

import de.lama.packets.event.EventHandlerContainer;
import de.lama.packets.operation.Operation;

public interface PacketIOComponent extends EventHandlerContainer {

    Operation close();

    boolean isClosed();

    int getPort();

}
