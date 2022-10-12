package de.lama.packets.server;

import de.lama.packets.Packet;
import de.lama.packets.action.Operation;
import de.lama.packets.server.client.ServerClient;
import de.lama.packets.server.event.ServerEvent;

import java.util.function.Consumer;
import java.util.stream.Stream;

public interface PacketServer {

    Operation open();

    void close();

    Operation send(Packet packet);

    <T extends ServerEvent> void register(Class<T> eventType, Consumer<T> consumer);

    boolean isOpen();

    int getTickrate();

    int getPort();

    Stream<ServerClient> getClients();

}
