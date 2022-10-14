package de.lama.packets.event;

import java.util.UUID;
import java.util.function.Consumer;

public class RegisteredListener<T> {

    private final UUID id;
    private final Consumer<T> consumer;

    public RegisteredListener(UUID id, Consumer<T> consumer) {
        this.id = id;
        this.consumer = consumer;
    }

    public UUID getId() {
        return this.id;
    }

    public Consumer<T> getConsumer() {
        return this.consumer;
    }
}
