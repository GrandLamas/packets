package de.lama.packets.event;

import java.util.*;
import java.util.function.Consumer;

public class OrderedEventExecutor<T> implements EventHandler<T> {

    private final Map<Class<T>, Collection<RegisteredListener<T>>> events;

    public OrderedEventExecutor() {
        this.events = new HashMap<>();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <R extends T> UUID subscribe(Class<R> type, Consumer<R> eventConsumer) {
        this.events.putIfAbsent((Class<T>) type, new LinkedHashSet<>());
        RegisteredListener<R> listener = new RegisteredListener<>(UUID.randomUUID(), eventConsumer);
        this.events.get(type).add((RegisteredListener<T>) listener);
        return listener.getId();
    }

    @Override
    public void unsubscribe(UUID listenerId) {
        this.events.values().forEach(list -> list.stream().filter(entry -> entry.getId().equals(listenerId)).findFirst().ifPresent(list::remove));
    }

    @Override
    public void notify(T event) {
        Collection<RegisteredListener<T>> events = this.events.get(event.getClass());
        if (events != null) events.forEach(iterate -> iterate.getConsumer().accept(event));
    }

    @Override
    public boolean isCancelled(T event) {
        if (!Cancellable.class.isAssignableFrom(event.getClass())) throw new IllegalArgumentException("No cancellable event provided");
        this.notify(event);
        return ((Cancellable) event).isCancelled();
    }
}
