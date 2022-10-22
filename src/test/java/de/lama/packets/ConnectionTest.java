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

public class ConnectionTest {

    public static void main(String[] args) throws IOException {
        PacketRegistry registry = new HashedPacketRegistry();
        registry.registerPacket(MessagePacket.ID, MessagePacket.class);

        startServer(registry);
        connectClient(registry);
    }

    private static void startServer(PacketRegistry registry) throws IOException {
        Server server = new ServerBuilder().registry(registry).build();

        server.getEventHandler().subscribe(ClientConnectEvent.class, (connectEvent) -> {
            System.out.println("Connected client " + connectEvent.client().getAddress().toString());

            connectEvent.client().getEventHandler().subscribe(PacketReceiveEvent.class, (event) -> System.out.println("Received: " + event.packetId()));
        });

        server.open().queue();
    }

    private static void connectClient(PacketRegistry registry) {
        try {
            Client client = new ClientBuilder().registry(registry).build();
            for (int i = 1; i <= 10000; i++) {
                client.send(new MessagePacket("sussy amogus balls obama burger")).queue(); // very political
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
