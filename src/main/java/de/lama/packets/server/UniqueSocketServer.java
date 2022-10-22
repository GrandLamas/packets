package de.lama.packets.server;

import de.lama.packets.AbstractPacketIOComponent;
import de.lama.packets.Packet;
import de.lama.packets.client.Client;
import de.lama.packets.client.ClientBuilder;
import de.lama.packets.event.events.server.ClientCloseEvent;
import de.lama.packets.event.events.server.ClientConnectEvent;
import de.lama.packets.event.events.server.ServerHandshakeListener;
import de.lama.packets.operation.Operation;
import de.lama.packets.operation.operations.ComponentCloseOperation;
import de.lama.packets.operation.operations.SocketCloseOperation;
import de.lama.packets.operation.operations.server.BroadcastOperation;
import de.lama.packets.operation.operations.ClientCloseOperation;
import de.lama.packets.registry.PacketRegistry;
import de.lama.packets.util.ExceptionHandler;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collection;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Stream;

class UniqueSocketServer extends AbstractPacketIOComponent implements Server {

    private final ServerSocket socket;
    private final ExceptionHandler exceptionHandler;
    private final Collection<Client> clients;
    private final ClientBuilder clientFactory;
    private boolean closed;

    UniqueSocketServer(ServerSocket socket, ClientBuilder builder, PacketRegistry registry, ExceptionHandler exceptionHandler) {
        super(exceptionHandler, registry);
        this.exceptionHandler = exceptionHandler;
        this.socket = socket;
        this.clients = new ConcurrentLinkedQueue<>();
        this.clientFactory = builder;
        this.closed = true;

        this.registerDefaultListener();
    }

    private void registerDefaultListener() {
        this.eventHandler.subscribe(ClientConnectEvent.class, new ServerHandshakeListener());
    }

    private boolean register(Socket socket) {
        if (this.closed) return false;
        Client client = this.clientFactory.buildVirtual(socket);
        if (this.eventHandler.isCancelled(new ClientConnectEvent(this, client))) {
            client.close();
        } else {
            this.clients.add(client);
        }
        
        return true;
    }

    @Override
    public Operation open() {
        if (this.socket.isClosed()) throw new IllegalStateException("Socket already shutdown");
        if (!this.closed) throw new IllegalStateException("Server already running");
        this.closed = false;
        return new RepeatingClientAcceptor(this.socket, this::register, this.exceptionHandler);
    }

    @Override
    public Operation close() {
        if (this.socket.isClosed()) throw new IllegalStateException("Socket already shutdown");
        if (this.closed) throw new IllegalStateException("Server not running");
        this.closed = true;
        return new ComponentCloseOperation(this.repeatingOperations, this.exceptionHandler);
    }

    @Override
    public Operation close(Client client) {
        if (this.eventHandler.isCancelled(new ClientCloseEvent(this, client))) return null;
        return new ClientCloseOperation(client, this.clients::remove);
    }

    @Override
    public Operation shutdown() {
        if (this.socket.isClosed()) throw new IllegalStateException("Socket already shutdown");
        this.closed = true;
        return new SocketCloseOperation(this.socket, this.repeatingOperations, this.exceptionHandler);
    }

    @Override
    public Operation broadcast(Packet packet) {
        return new BroadcastOperation(this.clients, packet);
    }

    @Override
    public boolean isClosed() {
        return !this.closed;
    }

    @Override
    public int getPort() {
        return this.socket.getLocalPort();
    }

    @Override
    public Stream<Client> getClients() {
        return this.clients.stream();
    }

}
