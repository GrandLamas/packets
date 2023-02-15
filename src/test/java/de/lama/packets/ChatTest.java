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
import de.lama.packets.registry.HashedPacketRegistry;
import de.lama.packets.registry.PacketRegistry;
import de.lama.packets.server.Server;
import de.lama.packets.server.events.ClientConnectEvent;
import de.lama.packets.server.socket.SocketServerBuilder;

import java.util.Scanner;

public class ChatTest {

    public static void main(String[] args) {
        PacketRegistry registry = new HashedPacketRegistry();
        registry.registerPacket(MessagePacket.ID, MessagePacket.class);

        SocketChannelClientBuilder builder = new SocketChannelClientBuilder().registry(registry);

        if (args.length >= 1 && args[0].equals("client"))
            connectClient(builder);
        else
            startServer(builder);
    }

    private static void startServer(SocketChannelClientBuilder builder) {
        Server server = new SocketServerBuilder().clients(builder).build(4999);

        server.getEventHandler().subscribe(ClientConnectEvent.class, (connectEvent) -> {
            System.out.println("Connected client " + connectEvent.client());

            connectEvent.client().getEventHandler().subscribe(PacketReceiveEvent.class, (event) -> server.broadcast(event.packet()));
        });
    }

    private static void connectClient(SocketChannelClientBuilder builder) {
        Client client = builder.build("localhost", 4999);

        client.getEventHandler().subscribe(PacketReceiveEvent.class, (event -> {
            if (event.packetId() != MessagePacket.ID) return;
            System.out.println(((MessagePacket) event.packet()).message());
        }));
        Scanner scanner = new Scanner(System.in);
        while (true) {
            String next = scanner.nextLine();
            if (next.equals("quit")) return;
            client.send(new MessagePacket(next)).join();
        }
    }
}
