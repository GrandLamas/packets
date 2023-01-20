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

        if (args.length >= 1 && args[0].equals("client"))
            connectClient(registry);
        else
            startServer(registry);
    }

    private static void startServer(PacketRegistry registry) throws IOException {
        Server server = new ServerBuilder().registry(registry).build(4999);

        server.getEventHandler().subscribe(ClientConnectEvent.class, (connectEvent) -> {
            System.out.println("Connected client " + connectEvent.client().getAddress().toString());

            connectEvent.client().getEventHandler().subscribe(PacketReceiveEvent.class, (event) -> server.broadcast(event.packet()).queue());
        });

        server.open().queue();
    }

    private static void connectClient(PacketRegistry registry) {
        try {
            Client client = new ClientBuilder().registry(registry).build("localhost", 4999);
            client.open().queue();

            client.getEventHandler().subscribe(PacketReceiveEvent.class, (event -> {
                if (event.packetId() != MessagePacket.ID) return;
                System.out.println(((MessagePacket) event.packet()).message());
            }));
            Scanner scanner = new Scanner(System.in);
            while (true) {
                String next = scanner.nextLine();
                if (next.equals("quit")) return;
                client.send(new MessagePacket(next)).complete();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
