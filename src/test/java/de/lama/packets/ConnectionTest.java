package de.lama.packets;

import de.lama.packets.server.PacketServer;
import de.lama.packets.server.ServerBuilder;
import de.lama.packets.server.event.ServerClientConnectEvent;

import java.io.IOException;

public class ConnectionTest {

    public static void main(String[] args) throws IOException {
        PacketServer server = new ServerBuilder().exceptionHandler(Throwable::printStackTrace).build();

        server.getEventHandler().subscribe(ServerClientConnectEvent.class, (connectEvent) -> {
            System.out.println("Connected client " + connectEvent.client().getAddress().toString());
            connectEvent.client().send(new HandshakePacket("Test1")).queue();
            connectEvent.client().send(new HandshakePacket("Test2")).queue();
        });

        server.open().queue();

//        connectClient(server);
    }

    // Debug
//    private static void connectClient(PacketServer server) {
//        new Thread(() -> {
//            try {
//                Socket socket = new Socket("localhost", server.getPort());
//                char[] buffer = new char[2048];
//                int charsRead;
//                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//                while ((charsRead = in.read(buffer)) != -1) {
//                    String message = new String(buffer).substring(0, charsRead);
//                    Packet packet = new GsonWrapper(server.getRegistry()).unwrapString(message);
//                    System.out.println(((HandshakePacket) packet).getVersion());
//                }
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//        }).start();
//    }
}
