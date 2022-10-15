package de.lama.packets.event;

import java.util.UUID;
import java.util.function.Consumer;

public interface EventHandler {

    <R extends Event> UUID subscribe(Class<R> type, Consumer<R> eventConsumer);

    void unsubscribe(UUID listenerId);

    boolean isCancelled(Event event);

    void notify(Event event);

}
