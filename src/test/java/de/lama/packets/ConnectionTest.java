package de.lama.packets;

import de.lama.packets.client.launchable.ClientBuilder;
import de.lama.packets.client.launchable.PacketClient;
import de.lama.packets.event.PacketReceiveEvent;
import de.lama.packets.server.PacketServer;
import de.lama.packets.server.ServerBuilder;
import de.lama.packets.server.event.ClientConnectEvent;

import java.io.IOException;

public class ConnectionTest {

    public static void main(String[] args) throws IOException, InterruptedException {
        startServer();
        Thread.sleep(100);
        connectClient();
    }

    private static void startServer() throws IOException {
        PacketServer server = new ServerBuilder().build();
        server.getRegistry().registerPacket(MessagePacket.ID, MessagePacket.class);

        server.getEventHandler().subscribe(ClientConnectEvent.class, (connectEvent) -> {
            System.out.println("Connected client " + connectEvent.client().getAddress().toString());

            connectEvent.client().getEventHandler().subscribe(PacketReceiveEvent.class, (event) -> {
                if (event.packet().getId() == MessagePacket.ID) {
                    System.out.println(((MessagePacket) event.packet()).getMessage());
                }
            });
        });

        server.open().queue();
    }

    private static void connectClient() {
        try {
            PacketClient client = new ClientBuilder().localhost().build();
            Thread.sleep(100);
            client.send(new MessagePacket("Laura suckt")).queue();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
