package de.lama.packets.server;

import de.lama.packets.client.Client;
import de.lama.packets.Packet;
import de.lama.packets.event.EventHandlerContainer;
import de.lama.packets.operation.Operation;

import java.util.stream.Stream;

public interface PacketServer extends EventHandlerContainer {

    Operation open();

    Operation close();

    Operation close(Client client);

    Operation shutdown();

    Operation broadcast(Packet packet);

    boolean isClosed();

    int getPort();

    Stream<Client> getClients();

}
