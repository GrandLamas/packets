package de.lama.packets;

import de.lama.packets.event.EventHandlerContainer;
import de.lama.packets.operation.Operation;
import de.lama.packets.registry.RegistryContainer;
import de.lama.packets.util.exception.ExceptionHandlerContainer;

public interface NetworkAdapter extends IoComponent, EventHandlerContainer, RegistryContainer, ExceptionHandlerContainer {

    Operation open();

    Operation close();

    Operation shutdown();

    boolean isClosed();

    boolean hasShutdown();

}
