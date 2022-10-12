package de.lama.packets.server.client;

import de.lama.packets.Packet;
import de.lama.packets.action.Operation;
import de.lama.packets.server.client.event.ServerClientEvent;

import java.net.Socket;
import java.util.function.Consumer;

public interface ServerClient {

    Socket getSocket();

    Operation send(Packet packet);

    Operation queue(Packet packet);

    <T extends ServerClientEvent> void hook(Class<T> hookClass, Consumer<T> consumer);

}
