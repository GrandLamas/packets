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
import de.lama.packets.client.HandshakePacket;
import de.lama.packets.server.Server;
import de.lama.packets.server.ServerBuilder;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class BrokenPipeTest {

    @Test
    public void clientClosedTest() throws IOException {
        Server server = new ServerBuilder().build(3999);
        server.open().complete();

        Client client = new SocketClientBuilder().exceptionHandler(throwable ->
                System.out.println("Successfully failed to send packet")).build("localhost", 3999);
        client.open().complete();

        client.shutdown().complete();
        client.send(new HandshakePacket("AMOGUS")).complete();

        server.shutdown().complete();
    }

    @Test
    public void serverClosedTest() throws IOException {
        Server server = new ServerBuilder().build(3999);
        server.open().complete();

        Client client = new SocketClientBuilder().exceptionHandler(throwable ->
                System.out.println("Successfully threw exception")).build("localhost", 3999);
        client.open().complete();

        server.shutdown().complete();
        client.send(new HandshakePacket("AMOGUS")).complete();
    }
}
