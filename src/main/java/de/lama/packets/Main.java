package de.lama.packets;

import de.lama.packets.server.PacketServer;
import de.lama.packets.server.ServerBuilder;
import de.lama.packets.server.event.ServerClientConnectEvent;

import java.io.IOException;
import java.net.Socket;

public class Main {

    public static void main(String[] args) throws IOException {
        PacketServer server = new ServerBuilder().exceptionHandler(Throwable::printStackTrace).build();
        server.register(ServerClientConnectEvent.class, (connectEvent) -> System.out.println("Connected!"));

        server.open().queue();

        new Thread(() -> {
            try {
                new Socket("localhost", server.getPort());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).start();

//        server.close();
    }
}