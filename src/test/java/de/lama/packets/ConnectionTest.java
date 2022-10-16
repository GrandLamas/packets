package de.lama.packets;

import de.lama.packets.client.packet.ClientBuilder;
import de.lama.packets.client.packet.PacketClient;
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

        startServer(registry);
        Thread.sleep(100);
        connectClient(registry);
    }

    private static void startServer(PacketRegistry registry) throws IOException {
        PacketServer server = new ServerBuilder().registry(registry).build();

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

    private static void connectClient(PacketRegistry registry) {
        try {
            PacketClient client = new ClientBuilder().registry(registry).localhost().build();
//            Thread.sleep(1000);
            long nanos = System.currentTimeMillis();
//            for (int i = 1; i <= 10000; i++) {
                client.send(new MessagePacket("sussy amogus balls obama burger")).queue(); // very political
//            }
            System.out.println("Took: " + (System.currentTimeMillis() - nanos));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
