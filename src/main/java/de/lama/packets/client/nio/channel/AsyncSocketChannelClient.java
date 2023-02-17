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

import de.lama.packets.Packet;
import de.lama.packets.client.Client;
import de.lama.packets.client.io.AbstractCachedIoClient;
import de.lama.packets.client.io.CachedIoPacket;
import de.lama.packets.registry.PacketRegistry;
import de.lama.packets.util.CompletableFutureUtil;
import de.lama.packets.wrapper.PacketWrapper;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.*;

class AsyncSocketChannelClient extends AbstractCachedIoClient implements Client {

    private final InetSocketAddress destination;
    private final ExecutorService singleThreadPool;
    protected AsynchronousSocketChannel channel;
    private ByteBuffer headerCache;

    public AsyncSocketChannelClient(String destination, int port, PacketRegistry registry, PacketWrapper wrapper, int tickrate) {
        super(registry, wrapper);

        this.singleThreadPool = Executors.newSingleThreadExecutor();
        this.destination = new InetSocketAddress(destination, port);
    }

    private int write(ByteBuffer buffer) throws ExecutionException, InterruptedException {
        return this.channel.write(buffer).get();
    }

    @Override
    protected Future<Void> sendData(long packetId, ByteBuffer packet) {
        int limit = packet.limit();
        packet.putChar(Packet.TYPE);
        packet.putLong(packetId);
        packet.putInt(limit - Packet.RESERVED);
        packet.position(0);
        int toWrite = packet.limit();
        return this.singleThreadPool.submit(() -> { // Check if even reqiured
            int written = 0;
            while (written < toWrite) {
                try {
                    written += this.write(packet);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, null);
    }

    protected void readHeader() {
        this.headerCache = ByteBuffer.allocate(Packet.RESERVED);
        this.channel.read(this.headerCache, null, this.buildHeaderHandler());
    }

    private CompletionHandler<Integer, Object> buildBodyHandler(long packetId, int length, ByteBuffer allocated) {
        return new CompletionHandler<>() {

            int read = 0;

            @Override
            public void completed(Integer result, Object attachment) {
                // Receive and handle packet body
                read += result;
                if (allocated.remaining() > 0) {
                    channel.read(allocated, null, this);
                } else {
                    allocated.position(0);
                    found(new CachedIoPacket(packetId, allocated));
                    readHeader();
                }
            }

            @Override
            public void failed(Throwable exc, Object attachment) {
                exc.printStackTrace();
            }
        };
    }

    private CompletionHandler<Integer, Object> buildHeaderHandler() {
        return new CompletionHandler<>() {
            @Override
            public void completed(Integer result, Object attachment) {
                // Receive and handle packet
                if (result != Packet.RESERVED) return;
                headerCache.position(0);
                final char type = headerCache.getChar();
                final long packetId = headerCache.getLong();
                final int length = headerCache.getInt();
                if (type != Packet.TYPE) return;
                final ByteBuffer allocated = ByteBuffer.allocate(length);
                channel.read(allocated, null, buildBodyHandler(packetId, length, allocated));
            }

            @Override
            public void failed(Throwable exc, Object attachment) {
                exc.printStackTrace();
            }
        };
    }

    @Override
    protected CompletableFuture<Void> implConnect() {
        // Connect client to server
        return CompletableFutureUtil.supplyAsync(() -> {
            this.channel = AsynchronousSocketChannel.open();
            this.channel.connect(this.destination).get();
            this.readHeader();
            return null;
        });
    }

    @Override
    protected CompletableFuture<Void> implDisconnect() {
        // disconnect this client from the server
        return CompletableFutureUtil.supplyAsync(() -> {
            this.singleThreadPool.shutdownNow();
            this.channel.close();
            this.channel = null;
            this.headerCache = null;
            return null;
        });
    }

    @Override
    public InetAddress getAddress() {
        return this.destination.getAddress();
    }

    @Override
    public int getPort() {
        return this.destination.getPort();
    }
}
