package de.lama.packets.event;

import java.util.UUID;
import java.util.function.Consumer;

public record RegisteredListener<T>(UUID id, Consumer<T> consumer) {

}
