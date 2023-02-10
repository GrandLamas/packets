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
import de.lama.packets.client.stream.socket.SocketClientBuilder;
import de.lama.packets.registry.HashedPacketRegistry;
import de.lama.packets.registry.PacketRegistry;
import de.lama.packets.server.Server;
import de.lama.packets.server.socket.SocketServerBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import java.io.IOException;

public class DefaultConnection {

    protected static PacketRegistry registry;
    protected static SocketServerBuilder socketServerBuilder;
    protected static SocketClientBuilder clientBuilder;

    protected Server server;
    protected Client client;

    @BeforeAll
    public static void initBuilder() {
        PacketRegistry registry = new HashedPacketRegistry();
        registry.registerPacket(MessagePacket.ID, MessagePacket.class);
        socketServerBuilder = new SocketServerBuilder().registry(registry);
        clientBuilder = new SocketClientBuilder().registry(registry);
    }

    @BeforeEach
    public void initConnection() throws IOException, InterruptedException {
        this.server = socketServerBuilder.build(3999);
        Thread.sleep(10);
        this.client = clientBuilder.build("localhost", 3999);
    }

    @AfterEach
    public void shutdownConnection() throws InterruptedException {
        Thread.sleep(100);
        this.client.shutdown().complete();
        this.server.shutdown().complete();
        Thread.sleep(100);
    }
}
