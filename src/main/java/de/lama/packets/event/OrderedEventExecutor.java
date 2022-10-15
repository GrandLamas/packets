package de.lama.packets.event;

import java.util.*;
import java.util.function.Consumer;

public class OrderedEventExecutor implements EventHandler {

    private final Map<Class<? extends Event>, Collection<RegisteredListener<Event>>> events;

    public OrderedEventExecutor() {
        this.events = new HashMap<>();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <R extends Event> UUID subscribe(Class<R> type, Consumer<R> eventConsumer) {
        this.events.putIfAbsent(type, new LinkedHashSet<>());
        RegisteredListener<R> listener = new RegisteredListener<>(UUID.randomUUID(), eventConsumer);
        this.events.get(type).add((RegisteredListener<Event>) listener);
        return listener.id();
    }

    @Override
    public void unsubscribe(UUID listenerId) {
        this.events.values().forEach(list -> list.stream().filter(entry -> entry.id().equals(listenerId)).findFirst().ifPresent(list::remove));
    }

    @Override
    public void notify(Event event) {
        Collection<RegisteredListener<Event>> events = this.events.get(event.getClass());
        if (events != null) events.forEach(iterate -> iterate.consumer().accept(event));
    }

    @Override
    public boolean isCancelled(Event event) {
        if (!Cancellable.class.isAssignableFrom(event.getClass())) throw new IllegalArgumentException("No cancellable event provided");
        this.notify(event);
        return ((Cancellable) event).isCancelled();
    }
}
