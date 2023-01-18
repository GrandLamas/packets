package de.lama.packets.server;

import de.lama.packets.NetworkAdapter;
import de.lama.packets.Packet;
import de.lama.packets.IoComponent;
import de.lama.packets.client.Client;
import de.lama.packets.operation.Operation;

import java.util.stream.Stream;

public interface Server extends NetworkAdapter {

    Operation broadcast(Packet packet);

    Stream<Client> getClients();

}
