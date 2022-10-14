package de.lama.packets.event;

import java.util.UUID;
import java.util.function.Consumer;

public interface EventHandler<T> {

    <R extends T> UUID subscribe(Class<R> type, Consumer<R> eventConsumer);

    void unsubscribe(UUID listenerId);

    boolean isCancelled(T event);

    void notify(T event);

}
