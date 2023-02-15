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
import de.lama.packets.events.AdapterDisconnectEvent;
import de.lama.packets.server.events.ClientConnectEvent;

import java.util.Collection;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Future;
import java.util.stream.Stream;

public abstract class AbstractServer<C extends Client> extends AbstractNetworkAdapter<C> implements Server {

    private final Collection<C> clients;

    public AbstractServer(int tickrate) {
        super(tickrate);

        this.clients = new ConcurrentLinkedQueue<>();
    }

    private void unregister(C client) {
        this.clients.remove(client);
    }

    @Override
    protected Future<Boolean> process(C client) {
        return CompletableFuture.supplyAsync(() -> {
            if (this.getEventHandler().isCancelled(new ClientConnectEvent(this, client))) {
                client.disconnect().join();
                return false;
            }

            client.getEventHandler().subscribe(AdapterDisconnectEvent.class, event -> this.unregister(client));
            this.clients.add(client);
            client.connect().join(); // TODO: HANDLE EXCP
            return true;
        });
    }

    @Override
    public CompletableFuture<Void> broadcast(Packet packet) {
        Objects.requireNonNull(packet);
        return CompletableFuture.supplyAsync(() -> {
            this.clients.stream().map(client -> client.send(packet)).forEach(CompletableFuture::join);
            return null;
        });
    }

    @Override
    public Stream<Client> getClients() {
        return this.clients.stream().map(c -> c);
    }
}
