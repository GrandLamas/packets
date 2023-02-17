/*
 * MIT License
 *
 * Copyright (c) 2023 Cuuky
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package de.lama.packets.event;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;

public class OrderedEventExecutor implements EventHandler {

    private final Map<Class<? extends Event>, Collection<RegisteredListener<Event>>> events;

    public OrderedEventExecutor() {
        this.events = new HashMap<>();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <R extends Event> UUID subscribe(Class<R> type, Consumer<R> eventConsumer) {
        this.events.putIfAbsent(type, new ConcurrentLinkedQueue<>());
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
        return ((Cancellable) event).behaviour().isCancelled();
    }
}
