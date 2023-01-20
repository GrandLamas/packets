package de.lama.packets.server;

import de.lama.packets.AbstractNetworkAdapter;
import de.lama.packets.Packet;
import de.lama.packets.client.Client;
import de.lama.packets.client.ClientBuilder;
import de.lama.packets.event.events.server.ClientConnectEvent;
import de.lama.packets.operation.Operation;
import de.lama.packets.operation.RepeatingOperation;
import de.lama.packets.operation.SimpleOperation;
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
    private final Collection<Client> clients;
    private final ClientBuilder clientFactory;
    private final RepeatingOperation clientAcceptor;

    UniqueSocketServer(ServerSocket socket, ClientBuilder builder, PacketRegistry registry, ExceptionHandler exceptionHandler) {
        super(exceptionHandler, registry);
        this.socket = socket;
        this.clients = new ConcurrentLinkedQueue<>();
        this.clientFactory = builder;
        this.clientAcceptor = new RepeatingClientAcceptor(this.socket, this::register, this.getExceptionHandler());
    }

    private boolean register(Socket socket) {
        Client client = this.getExceptionHandler().operate(() -> this.clientFactory.build(socket), "Could not create client");
        if (this.getEventHandler().isCancelled(new ClientConnectEvent(this, client))) {
            client.shutdown().complete();
            return false;
        }

        client.open().complete();
        this.clients.add(client);
        return true;
    }

    @Override
    protected void executeOpen() {
        this.clientAcceptor.start();
    }

    @Override
    protected void executeClose() {
        this.clientAcceptor.stop();
    }

    @Override
    protected void executeShutdown() {
        this.getExceptionHandler().operate(this.socket::close, "Could not close socket");
    }

    @Override
    public Operation broadcast(Packet packet) {
        return new SimpleOperation((async) -> this.clients.forEach(client -> {
            Operation send = client.send(packet);
            if (async) send.queue();
            else send.complete();
        }));
    }

    @Override
    public boolean isClosed() {
        return this.hasShutdown() || !this.clientAcceptor.isRunning();
    }

    @Override
    public boolean hasShutdown() {
        return this.socket.isClosed();
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
