package de.lama.packets;

import de.lama.packets.client.ClientBuilder;
import de.lama.packets.client.Client;
import de.lama.packets.client.SocketBuilder;
import de.lama.packets.event.events.PacketReceiveEvent;
import de.lama.packets.registry.HashedPacketRegistry;
import de.lama.packets.registry.PacketRegistry;
import de.lama.packets.server.PacketServer;
import de.lama.packets.server.ServerBuilder;
import de.lama.packets.event.events.server.ClientConnectEvent;

import java.io.IOException;

public class ConnectionTest {

    public static void main(String[] args) throws IOException, InterruptedException {
        PacketRegistry registry = new HashedPacketRegistry();
        registry.registerPacket(MessagePacket.ID, MessagePacket.class);

        ClientBuilder clientBuilder = new ClientBuilder().registry(registry);
        startServer(clientBuilder);
        Thread.sleep(100);
        connectClient(clientBuilder);
    }

    private static void startServer(ClientBuilder builder) throws IOException {
        PacketServer server = new ServerBuilder().clientBuilder(builder).build();

        server.getEventHandler().subscribe(ClientConnectEvent.class, (connectEvent) -> {
            System.out.println("Connected client " + connectEvent.client().getAddress().toString());

            connectEvent.client().getEventHandler().subscribe(PacketReceiveEvent.class, (event) -> {
                if (event.packetId() == MessagePacket.ID) {
                    System.out.println(((MessagePacket) event.packet()).getMessage());
                }
            });
        });

        server.open().queue();
    }

    private static void connectClient(ClientBuilder clientBuilder) {
        try {
            Client client = clientBuilder.build(new SocketBuilder().localhost());
            Thread.sleep(1000);
            long nanos = System.currentTimeMillis();
//            for (int i = 1; i <= 10000; i++) {
                client.send(new MessagePacket("sussy amogus balls obama burger")).complete(); // very political
//            }
            System.out.println("Took " + (System.currentTimeMillis() - nanos) + "ms");
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
