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

package de.lama.packets.server.socket;

import de.lama.packets.client.Client;
import de.lama.packets.client.stream.socket.SocketClientBuilder;
import de.lama.packets.operation.RepeatingOperation;
import de.lama.packets.registry.PacketRegistry;
import de.lama.packets.server.AbstractServer;
import de.lama.packets.server.Server;
import de.lama.packets.util.exception.ExceptionHandler;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

class SocketServer extends AbstractServer implements Server {

    private final ServerSocket socket;
    private final SocketClientBuilder clientFactory;
    private final RepeatingOperation clientAcceptor;

    SocketServer(ServerSocket socket, SocketClientBuilder builder, PacketRegistry registry, ExceptionHandler exceptionHandler) {
        super(exceptionHandler, registry);
        this.socket = socket;

        this.clientFactory = builder;
        this.clientAcceptor = new RepeatingClientAcceptor(this.socket, this::build, this.getExceptionHandler());

        this.open().complete();
    }

    private boolean build(Socket socket) {
        Client client = this.getExceptionHandler().operate(() -> this.clientFactory.build(socket), "Could not create client");
        return client != null && super.register(client);
    }

    @Override
    public int hashCode() {
        return this.socket.hashCode();
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
    protected void shutdownConnection() throws IOException {
        this.socket.close();
    }

    @Override
    public boolean isClosed() {
        return super.isClosed() || !this.clientAcceptor.isRunning();
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

}
