package de.lama.packets.client.packet;

import de.lama.packets.client.AbstractGsonClient;
import de.lama.packets.event.events.PacketReceiveEvent;
import de.lama.packets.event.events.client.ClientHandshakeListener;
import de.lama.packets.registry.PacketRegistry;
import de.lama.packets.util.ExceptionHandler;

import java.net.Socket;

public class GsonClient extends AbstractGsonClient implements PacketClient {

    public GsonClient(Socket socket, PacketRegistry registry, int tickrate, ExceptionHandler exceptionHandler) {
        super(socket, registry, tickrate, exceptionHandler);

        this.getEventHandler().subscribe(PacketReceiveEvent.class, new ClientHandshakeListener(this));
    }
}
