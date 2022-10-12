package de.lama.packets.server;

import de.lama.packets.Packet;
import de.lama.packets.action.Operation;
import de.lama.packets.server.client.ServerClient;
import de.lama.packets.server.client.ServerClientImpl;
import de.lama.packets.server.event.ServerClientConnectEvent;
import de.lama.packets.server.event.ServerEvent;
import de.lama.packets.server.exception.ServerException;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Stream;

class PacketServerImpl implements PacketServer {

    private final ServerSocket socket;
    private final int tickrate;
    private final Consumer<ServerException> exceptionHandler;
    private final Collection<ServerClient> clients;
    private boolean open;

    private final Map<Class<? extends ServerEvent>, Collection<Consumer<? extends ServerEvent>>> events;

    PacketServerImpl(ServerSocket socket, int tickrate, Consumer<ServerException> exceptionHandler) {
        this.socket = socket;
        this.tickrate = tickrate;
        this.exceptionHandler = exceptionHandler;
        this.clients = new LinkedHashSet<>();
        this.events = new HashMap<>();
    }

    protected void register(Socket socket) {
        ServerEvent event = new ServerClientConnectEvent(new ServerClientImpl());
        this.events.get(event.getClass()).forEach(e -> ((Consumer<ServerEvent>) e).accept(event));
        // TODO: Build client
    }

    protected Consumer<ServerException> getExceptionHandler() {
        return this.exceptionHandler;
    }

    protected ServerSocket getSocket() {
        return this.socket;
    }

    protected void setOpen(boolean open) {
        this.open = open;
    }

    @Override
    public Operation open() {
        if (this.open) throw new IllegalStateException("Server already open");
        if (this.socket.isClosed()) throw new IllegalStateException("Socket already closed");
        return new ServerOpenAction(this);
    }

    @Override
    public void close() {
        if (this.socket.isClosed()) throw new IllegalStateException("Server already closed");
        try {
            this.open = false;
            this.socket.close();
        } catch (IOException e) {
            throw new ServerException("Could not close server properly", e);
        }
    }

    @Override
    public Operation send(Packet packet) {
        return null;
    }

    @Override
    public <T extends ServerEvent> void register(Class<T> eventType, Consumer<T> consumer) {
        if (!this.events.containsKey(eventType)) {
            this.events.put(eventType, new LinkedHashSet<>(Collections.singletonList(consumer)));
        } else this.events.get(eventType).add(consumer);
    }

    @Override
    public boolean isOpen() {
        return this.open;
    }

    @Override
    public int getTickrate() {
        return this.tickrate;
    }

    @Override
    public int getPort() {
        return this.socket.getLocalPort();
    }

    @Override
    public Stream<ServerClient> getClients() {
        return this.clients.stream();
    }
}
