package de.lama.packets.client.launchable;

import de.lama.packets.client.AbstractGsonClient;
import de.lama.packets.event.PacketReceiveEvent;
import de.lama.packets.registry.PacketRegistry;
import de.lama.packets.util.ExceptionHandler;

import java.net.Socket;

public class LocalGsonClient extends AbstractGsonClient implements PacketClient {

    public LocalGsonClient(Socket socket, PacketRegistry registry, int tickrate, ExceptionHandler exceptionHandler) {
        super(socket, registry, tickrate, exceptionHandler);

        this.getEventHandler().subscribe(PacketReceiveEvent.class, new ClientHandshakeListener(this));
    }
}
