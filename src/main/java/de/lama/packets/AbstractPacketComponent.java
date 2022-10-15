package de.lama.packets;

import de.lama.packets.event.EventHandler;
import de.lama.packets.event.EventHandlerContainer;
import de.lama.packets.event.OrderedEventExecutor;
import de.lama.packets.registry.PacketRegistry;
import de.lama.packets.registry.RegistryContainer;
import de.lama.packets.util.ExceptionHandler;

public abstract class AbstractPacketComponent implements RegistryContainer, EventHandlerContainer {

    protected final EventHandler eventHandler;
    protected final PacketRegistry registry;
    protected final ExceptionHandler exceptionHandler;

    public AbstractPacketComponent(ExceptionHandler exceptionHandler, PacketRegistry registry) {
        this.exceptionHandler = exceptionHandler;
        this.registry = registry;
        this.eventHandler = new OrderedEventExecutor();
    }

    @Override
    public PacketRegistry getRegistry() {
        return this.registry;
    }

    @Override
    public EventHandler getEventHandler() {
        return this.eventHandler;
    }
}
