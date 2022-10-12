package de.lama.packets.server.client;

import de.lama.packets.Packet;
import de.lama.packets.action.Operation;
import de.lama.packets.server.client.event.ServerClientEvent;

import java.net.Socket;
import java.util.function.Consumer;

public class ServerClientImpl implements ServerClient {

    @Override
    public int hashCode() {
        return this.getSocket().getInetAddress().hashCode();
    }

    @Override
    public Socket getSocket() {
        return null;
    }

    @Override
    public Operation send(Packet packet) {
        return null;
    }

    @Override
    public Operation queue(Packet packet) {
        return null;
    }

    @Override
    public <T extends ServerClientEvent> void hook(Class<T> hookClass, Consumer<T> consumer) {

    }
}
