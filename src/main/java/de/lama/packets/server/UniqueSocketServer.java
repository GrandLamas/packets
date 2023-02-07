/*
 * MIT License
 *
 * Copyright (c) 2023 Cuuky
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package de.lama.packets.server;

import de.lama.packets.AbstractNetworkAdapter;
import de.lama.packets.Packet;
import de.lama.packets.client.Client;
import de.lama.packets.client.stream.socket.SocketClientBuilder;
import de.lama.packets.event.events.AdapterShutdownEvent;
import de.lama.packets.event.events.ClientConnectEvent;
import de.lama.packets.operation.Operation;
import de.lama.packets.operation.RepeatingOperation;
import de.lama.packets.operation.SimpleOperation;
import de.lama.packets.registry.PacketRegistry;
import de.lama.packets.util.exception.ExceptionHandler;

import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collection;
import java.util.Objects;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Stream;

class UniqueSocketServer extends AbstractNetworkAdapter implements Server {

    private final ServerSocket socket;
    private final Collection<Client> clients;
    private final SocketClientBuilder clientFactory;
    private final RepeatingOperation clientAcceptor;

    UniqueSocketServer(ServerSocket socket, SocketClientBuilder builder, PacketRegistry registry, ExceptionHandler exceptionHandler) {
        super(exceptionHandler, registry);
        this.socket = socket;
        this.clients = new ConcurrentLinkedQueue<>();
        this.clientFactory = builder;
        this.clientAcceptor = new RepeatingClientAcceptor(this.socket, this::register, this.getExceptionHandler());

        this.open().complete();
    }

    private boolean register(Socket socket) {
        Client client = this.getExceptionHandler().operate(() -> this.clientFactory.build(socket), "Could not create client");
        if (client == null) {
            return false;
        }

        if (this.getEventHandler().isCancelled(new ClientConnectEvent(this, client))) {
            client.shutdown().complete();
            return false;
        }

        client.getEventHandler().subscribe(AdapterShutdownEvent.class, event -> this.unregister(client));
        this.clients.add(client);
        return true;
    }

    private void unregister(Client client) {
        this.clients.remove(client);
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
        this.clients.forEach(client -> client.shutdown().complete());
        this.getExceptionHandler().operate(this.socket::close, "Could not close socket");
    }

    @Override
    public Operation broadcast(Packet packet) {
        Objects.requireNonNull(packet);
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
