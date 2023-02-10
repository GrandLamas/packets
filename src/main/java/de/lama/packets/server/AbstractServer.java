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
import de.lama.packets.events.AdapterShutdownEvent;
import de.lama.packets.server.events.ClientConnectEvent;
import de.lama.packets.operation.Operation;
import de.lama.packets.operation.ParentOperation;
import de.lama.packets.operation.AsyncOperation;
import de.lama.packets.registry.PacketRegistry;
import de.lama.packets.util.exception.ExceptionHandler;

import java.util.Collection;
import java.util.Objects;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Stream;

public abstract class AbstractServer extends AbstractNetworkAdapter implements Server {

    private final Collection<Client> clients;

    public AbstractServer(ExceptionHandler exceptionHandler, PacketRegistry registry) {
        super(exceptionHandler, registry);

        this.clients = new ConcurrentLinkedQueue<>();
    }

    private void unregister(Client client) {
        this.clients.remove(client);
    }

    protected abstract Operation shutdownConnection();

    protected boolean register(Client client) {
        if (this.getEventHandler().isCancelled(new ClientConnectEvent(this, client))) {
            client.shutdown().complete();
            return false;
        }

        client.getEventHandler().subscribe(AdapterShutdownEvent.class, event -> this.unregister(client));
        this.clients.add(client);
        return true;
    }

    @Override
    protected Operation executeShutdown() {
        return new ParentOperation(this.shutdownConnection(), () -> {
            this.clients.forEach(client -> client.shutdown().complete());
            return true;
        });
    }

    @Override
    public Operation broadcast(Packet packet) {
        Objects.requireNonNull(packet);
        return new AsyncOperation((async) -> this.clients.forEach(client -> {
            Operation send = client.send(packet);
            if (async) send.queue();
            else send.complete();
        }));
    }

    @Override
    public boolean isClosed() {
        return this.hasShutdown();
    }

    @Override
    public Stream<Client> getClients() {
        return this.clients.stream();
    }
}
