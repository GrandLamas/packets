package de.lama.packets.server;

import de.lama.packets.AbstractNetworkAdapter;
import de.lama.packets.Packet;
import de.lama.packets.client.Client;
import de.lama.packets.client.ClientBuilder;
import de.lama.packets.event.events.server.ClientConnectEvent;
import de.lama.packets.operation.Operation;
import de.lama.packets.operation.operations.TransceiverCloseOperation;
import de.lama.packets.operation.operations.AdapterCloseOperation;
import de.lama.packets.operation.operations.server.BroadcastOperation;
import de.lama.packets.operation.operations.server.RepeatingClientAcceptor;
import de.lama.packets.registry.PacketRegistry;
import de.lama.packets.util.exception.ExceptionHandler;

import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collection;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Stream;

class UniqueSocketServer extends AbstractNetworkAdapter implements Server {

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
    }

    private boolean register(Socket socket) {
        if (this.closed) return false;
        Client client = this.exceptionHandler.operate(() -> this.clientFactory.build(socket), "Could not create client");
        if (this.eventHandler.isCancelled(new ClientConnectEvent(this, client))) {
            client.close();
        } else {
//            new HandshakeListener(client, () -> this.clients.add(client)).complete();
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
        return new TransceiverCloseOperation(this.repeatingOperations, this.exceptionHandler);
    }

    @Override
    public Operation shutdown() {
        if (this.socket.isClosed()) throw new IllegalStateException("Socket already shutdown");
        this.closed = true;
        return new AdapterCloseOperation(this.socket, this.repeatingOperations, this.exceptionHandler);
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
    public InetAddress getAddress() {
        return this.socket.getInetAddress();
    }

    @Override
    public Stream<Client> getClients() {
        return this.clients.stream();
    }

}
