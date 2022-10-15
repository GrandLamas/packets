package de.lama.packets.client.server;

import de.lama.packets.client.AbstractGsonClient;
import de.lama.packets.client.Client;
import de.lama.packets.registry.PacketRegistry;
import de.lama.packets.util.ExceptionHandler;

import java.net.Socket;

public class ServerGsonClient extends AbstractGsonClient implements Client {

    public ServerGsonClient(Socket socket, PacketRegistry registry, int tickrate, ExceptionHandler exceptionHandler) {
        super(socket, registry, tickrate, exceptionHandler);
    }
}
