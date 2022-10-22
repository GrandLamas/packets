package de.lama.packets;

import de.lama.packets.client.Client;
import de.lama.packets.client.ClientBuilder;
import de.lama.packets.event.events.PacketReceiveEvent;
import de.lama.packets.event.events.server.ClientConnectEvent;
import de.lama.packets.server.Server;
import de.lama.packets.server.ServerBuilder;

import java.io.IOException;

public class ConnectionTest {

    public static void main(String[] args) throws IOException {
        ClientBuilder clientBuilder = new ClientBuilder();
        clientBuilder.registry().registerPacket(MessagePacket.ID, MessagePacket.class);

        startServer(clientBuilder);
        connectClient(clientBuilder);
    }

    private static void startServer(ClientBuilder builder) throws IOException {
        Server server = new ServerBuilder().clients(builder).build();

        server.getEventHandler().subscribe(ClientConnectEvent.class, (connectEvent) -> {
            System.out.println("Connected client " + connectEvent.client().getAddress().toString());

            connectEvent.client().getEventHandler().subscribe(PacketReceiveEvent.class, (event) -> System.out.println("Received: " + event.packetId()));
        });

        server.open().queue();
    }

    private static void connectClient(ClientBuilder clientBuilder) {
        try {
            Client client = clientBuilder.build();
            for (int i = 1; i <= 10000; i++) {
                client.send(new MessagePacket("sussy amogus balls obama burger")).queue(); // very political
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
