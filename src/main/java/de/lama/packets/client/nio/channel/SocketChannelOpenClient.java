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

package de.lama.packets.client.nio.channel;

import de.lama.packets.registry.PacketRegistry;
import de.lama.packets.util.CompletableFutureUtil;
import de.lama.packets.wrapper.PacketWrapper;

import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.CompletableFuture;

public class SocketChannelOpenClient extends SocketChannelClient {

    public SocketChannelOpenClient(AsynchronousSocketChannel channel, String host, int port, PacketRegistry registry, PacketWrapper wrapper, int tickrate) {
        super(host, port, registry, wrapper, tickrate);

        this.channel = channel;
    }

    @Override
    public CompletableFuture<Void> implConnect() {
        return this.channel != null ? CompletableFutureUtil.supplyAsync(() -> {
            this.read();
            return null;
        }) : super.implConnect();
    }

    @Override
    protected CompletableFuture<Void> implDisconnect() {
        return super.implDisconnect().thenApply(unused -> {
            this.channel = null;
            return null;
        });
    }
}
