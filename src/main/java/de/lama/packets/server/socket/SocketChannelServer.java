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
import de.lama.packets.client.nio.channel.AsyncSocketChannelClientBuilder;
import de.lama.packets.server.AbstractServer;
import de.lama.packets.server.Server;
import de.lama.packets.util.CompletableFutureUtil;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.channels.*;
import java.util.concurrent.CompletableFuture;

class SocketChannelServer extends AbstractServer<Client> implements Server {

    private final AsyncSocketChannelClientBuilder clientFactory;
    private final InetSocketAddress address;
    private AsynchronousServerSocketChannel channel;
    private AsynchronousServerSocketChannel listener;

    SocketChannelServer(int port, int tickrate, AsyncSocketChannelClientBuilder builder) {
        super(tickrate);
        this.address = new InetSocketAddress(port);
        this.clientFactory = builder;
    }

    @Override
    protected CompletableFuture<Void> implConnect() {
        return CompletableFutureUtil.supplyAsync(() -> {
            this.channel = AsynchronousServerSocketChannel.open();
            this.listener = this.channel.bind(this.address);
            this.listener.accept(null, new CompletionHandler<>() {
                @Override
                public void completed(AsynchronousSocketChannel result, Object attachment) {
                    // Accept next
                    listener.accept(null, this);

                    // Process
                    try {
                        found(clientFactory.build(result, getAddress().getHostAddress(), getPort()));
                    } catch (IOException e) {
                        throw new RuntimeException(e); // TODO
                    }
                }

                @Override
                public void failed(Throwable exc, Object attachment) {
                    // TODO
                    exc.printStackTrace();
                }
            });
        }, null);
    }

    @Override
    protected CompletableFuture<Void> implDisconnect() {
        return CompletableFutureUtil.supplyAsync(() -> {
            this.listener.close();
            this.channel.close();
        }, null);
    }

    @Override
    public int hashCode() {
        return this.channel.hashCode();
    }

    @Override
    public InetAddress getAddress() {
        return this.address.getAddress();
    }

    @Override
    public int getPort() {
        return this.address.getPort();
    }
}
