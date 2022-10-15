package de.lama.packets;

import de.lama.packets.server.PacketServer;
import de.lama.packets.server.ServerBuilder;
import de.lama.packets.server.event.ServerClientConnectEvent;
import de.lama.packets.transceiver.transmitter.PacketTransmitter;
import de.lama.packets.transceiver.transmitter.PacketTransmitterBuilder;
import de.lama.packets.wrapper.GsonWrapper;

import java.io.IOException;
import java.net.Socket;

public class ConnectionTest {

    public static void main(String[] args) throws IOException, InterruptedException {
        PacketServer server = new ServerBuilder().exceptionHandler(Throwable::printStackTrace).build();

        server.getEventHandler().subscribe(ServerClientConnectEvent.class, (connectEvent) -> {
            System.out.println("Connected client " + connectEvent.client().getAddress().toString());
            connectEvent.client().send(new HandshakePacket("Test1")).queue();
            connectEvent.client().send(new HandshakePacket("Test2")).queue();
        });

        server.open().queue();

        Thread.sleep(100);
        connectClient(server);
    }

    private static void connectClient(PacketServer server) {
        new Thread(() -> {
            try {
                Socket socket = new Socket("localhost", server.getPort());
                PacketTransmitter transmitter = new PacketTransmitterBuilder().exceptionHandler(System.out::println)
                        .packetWrapper(new GsonWrapper(server.getRegistry())).build(socket);
                transmitter.complete(new HandshakePacket("v0.1"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }
}
