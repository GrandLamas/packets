package de.lama.packets;

import de.lama.packets.client.Client;
import de.lama.packets.client.ClientBuilder;
import de.lama.packets.event.events.PacketReceiveEvent;
import de.lama.packets.event.events.server.ClientConnectEvent;
import de.lama.packets.registry.HashedPacketRegistry;
import de.lama.packets.registry.PacketRegistry;
import de.lama.packets.server.Server;
import de.lama.packets.server.ServerBuilder;

import java.io.IOException;
import java.util.Scanner;

public class ConnectionTest {

    public static void main(String[] args) throws IOException {
        PacketRegistry registry = new HashedPacketRegistry();
        registry.registerPacket(MessagePacket.ID, MessagePacket.class);

        startServer(registry);
        connectClient(registry);
    }

    private static void startServer(PacketRegistry registry) throws IOException {
        Server server = new ServerBuilder().registry(registry).build(4999);

        server.getEventHandler().subscribe(ClientConnectEvent.class, (connectEvent) -> {
            System.out.println("Connected client " + connectEvent.client().getAddress().toString());

            connectEvent.client().getEventHandler().subscribe(PacketReceiveEvent.class, (event) -> System.out.println("Server Received: " + event.packetId()));
        });

        server.open().queue();
    }

    private static void connectClient(PacketRegistry registry) {
        try {
            Client client = new ClientBuilder().registry(registry).build("localhost", 4999);

            client.getEventHandler().subscribe(PacketReceiveEvent.class, (event -> System.out.println("Client received: " + event.packetId())));
            long millis = System.currentTimeMillis();
            Scanner scanner = new Scanner(System.in);
            while (true) {
                String next = scanner.next();
                if (next.equals("quit")) return;
                client.send(new MessagePacket(next)).complete();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
