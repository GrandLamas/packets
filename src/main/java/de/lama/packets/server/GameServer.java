package de.lama.packets.server;

import de.lama.packets.server.event.ClientEvent;

import java.util.function.Consumer;

public interface GameServer {

    boolean listen();

    boolean stopListening();

    <T extends ClientEvent> void register(Class<T> eventType, Consumer<T> consumer);

}
