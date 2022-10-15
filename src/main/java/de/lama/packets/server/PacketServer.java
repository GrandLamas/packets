package de.lama.packets.server;

import de.lama.packets.Packet;
import de.lama.packets.event.EventHandlerContainer;
import de.lama.packets.operation.Operation;
import de.lama.packets.registry.RegistryContainer;
import de.lama.packets.server.client.ServerClient;

import java.util.stream.Stream;

public interface PacketServer extends RegistryContainer, EventHandlerContainer {

    Operation open();

    Operation close();

    Operation close(ServerClient client);

    Operation shutdown();

    Operation broadcast(Packet packet);

    boolean isOpen();

    int getTickrate();

    int getPort();

    Stream<ServerClient> getClients();

}
