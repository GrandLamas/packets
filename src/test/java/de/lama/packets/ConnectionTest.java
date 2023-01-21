package de.lama.packets;

import de.lama.packets.client.Client;
import de.lama.packets.client.ClientBuilder;
import de.lama.packets.server.Server;
import de.lama.packets.server.ServerBuilder;

import java.io.IOException;

public class ConnectionTest {

    protected Server server;
    protected Client client;

    public ConnectionTest() throws IOException {
        this.server = new ServerBuilder().build(3999);
        this.server.open().complete();

        this.client = new ClientBuilder().build("localhost", 3999);
        this.client.open().complete();
    }
}
