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

package de.lama.packets;

import de.lama.packets.client.Client;
import de.lama.packets.client.events.PacketReceiveEvent;
import de.lama.packets.client.nio.channel.SocketChannelClientBuilder;
import de.lama.packets.event.EventHandler;
import de.lama.packets.registry.HashedPacketRegistry;
import de.lama.packets.registry.PacketRegistry;
import de.lama.packets.server.Server;
import de.lama.packets.server.socket.SocketServerBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutionException;

public class DefaultConnection {

    protected static PacketRegistry registry = new HashedPacketRegistry();
    protected static SocketServerBuilder socketServerBuilder;
    protected static SocketChannelClientBuilder clientBuilder;

    protected Server server;
    protected Client client;

    private Set<Long> waitFor;

    protected void listen(EventHandler handler) {
        handler.subscribe(PacketReceiveEvent.class, (event) -> this.waitFor.add(event.packetId()));
    }

    protected void waitForPacket(long packetId) throws InterruptedException {
        do {
            Thread.sleep(10);
        } while (!this.waitFor.remove(packetId));
    }

    @BeforeAll
    public static void initBuilder() {
        registry.registerPacket(MessagePacket.ID, MessagePacket.class);
        clientBuilder = new SocketChannelClientBuilder().registry(registry);
        socketServerBuilder = new SocketServerBuilder().clients(clientBuilder);
    }

    @BeforeEach
    public void initConnection() throws InterruptedException, ExecutionException {
        this.waitFor = Collections.synchronizedSet(new HashSet<>());
        this.server = socketServerBuilder.build(3999);
        this.server.connect().get();
        this.client = clientBuilder.build("localhost", 3999);
        this.client.connect().get();
    }

    @AfterEach
    public void shutdownConnection() throws InterruptedException, ExecutionException {
        this.client.disconnect().get();
        this.server.disconnect().get();
    }
}
