package de.lama.packets.event;

public interface EventHandlerContainer<T> {

    EventHandler<T> getEventHandler();

}
