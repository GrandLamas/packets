package de.lama.packets.server;

import de.lama.packets.AbstractPacketComponent;
import de.lama.packets.Packet;
import de.lama.packets.operation.Operation;
import de.lama.packets.registry.HashedPacketRegistry;
import de.lama.packets.registry.PacketRegistry;
import de.lama.packets.server.client.ServerClient;
import de.lama.packets.server.client.ServerClientBuilder;
import de.lama.packets.server.event.ServerClientConnectEvent;
import de.lama.packets.server.event.ServerClientDisconnectEvent;
import de.lama.packets.server.event.listener.HandshakeListener;
import de.lama.packets.util.ExceptionHandler;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collection;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Stream;

class LinkedClientServer extends AbstractPacketComponent implements PacketServer {

    private final ServerSocket socket;
    private final int tickrate;
    private final Collection<ServerClient> clients;
    private final ServerClientBuilder clientBuilder;
    private boolean open;

    LinkedClientServer(ServerSocket socket, int tickrate, ExceptionHandler exceptionHandler) {
        super(exceptionHandler, new HashedPacketRegistry());
        this.socket = socket;
        this.tickrate = tickrate;
        this.clients = new ConcurrentLinkedQueue<>();
        this.clientBuilder = new ServerClientBuilder().exceptionHandler(this.exceptionHandler);

        this.registerDefaultListener();
    }

    private void registerDefaultListener() {
        this.eventHandler.subscribe(ServerClientConnectEvent.class, new HandshakeListener());
    }

    protected void register(Socket socket) {
        ServerClient client = this.clientBuilder.server(this).exceptionHandler(this.exceptionHandler).build(socket);
        if (this.eventHandler.isCancelled(new ServerClientConnectEvent(this, client))) {
            client.close();
            return;
        }

        this.clients.add(client);
    }

    protected ExceptionHandler getExceptionHandler() {
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
        if (this.socket.isClosed()) throw new IllegalStateException("Socket already shutdown");
        if (this.open) throw new IllegalStateException("Server already running");
        return new ServerOpenOperation(this);
    }

    @Override
    public Operation close() {
        if (this.socket.isClosed()) throw new IllegalStateException("Socket already shutdown");
        if (!this.open) throw new IllegalStateException("Server not running");
        return new ServerCloseOperation(this);
    }

    @Override
    public Operation close(ServerClient client) {
        if (this.eventHandler.isCancelled(new ServerClientDisconnectEvent(this, client))) return null;
        return new ClientCloseOperation(client, this.clients::remove);
    }

    @Override
    public Operation shutdown() {
        if (this.socket.isClosed()) throw new IllegalStateException("Socket already shutdown");
        return new ServerShutdownOperation(this);
    }

    @Override
    public Operation broadcast(Packet packet) {
        return new BroadcastOperation(this, packet);
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

    @Override
    public PacketRegistry getRegistry() {
        return this.registry;
    }
}
