package de.lama.packets.client.virtual;

import de.lama.packets.client.AbstractGsonClient;
import de.lama.packets.client.Client;
import de.lama.packets.registry.PacketRegistry;
import de.lama.packets.util.ExceptionHandler;

import java.net.Socket;

public class VirtualGsonClient extends AbstractGsonClient implements Client {

    public VirtualGsonClient(Socket socket, PacketRegistry registry, int tickrate, ExceptionHandler exceptionHandler) {
        super(socket, registry, tickrate, exceptionHandler);
    }
}
