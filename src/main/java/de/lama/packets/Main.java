package de.lama.packets;

import de.lama.packets.server.GameServer;
import de.lama.packets.server.event.ClientConnectEvent;
import de.lama.packets.server.event.ClientDisconnectEvent;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.function.Consumer;

public class Main {

    public static void main(String[] args) throws IOException {
        System.out.println("Hello world!");

        ServerSocket serverSocket = new ServerSocket(4999);
        serverSocket.close();
        Socket s = serverSocket.accept();

        Socket socket = new Socket("localhost", 4999);

        GameServer server = null;
    }
}