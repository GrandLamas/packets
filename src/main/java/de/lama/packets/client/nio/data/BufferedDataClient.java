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

package de.lama.packets.client.nio.data;

import de.lama.packets.Packet;
import de.lama.packets.client.Client;
import de.lama.packets.client.events.FileReceivedEvent;
import de.lama.packets.client.events.PacketReceiveEvent;
import de.lama.packets.client.io.CachedIoClient;
import de.lama.packets.event.EventHandler;
import de.lama.packets.registry.PacketRegistry;
import de.lama.packets.util.CompletableFutureUtil;

import java.io.IOException;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public abstract class BufferedDataClient implements DataClient {

    private static final int DEFAULT_PACKET_LENGTH = (int) (Packet.MAX_PACKET_LENGTH - Packet.RESERVED); // This seems as the most performant for java.net.Socket

    private final Client child;
    private final int packetLength;
    private final Map<String, SeekableByteChannel> channelMap;

    public BufferedDataClient(Client child, int packetLengthBytes) {
        this.child = child;
        this.packetLength = packetLengthBytes;
        this.channelMap = new ConcurrentHashMap<>();

        this.register(DataHeaderPacket.ID, DataHeaderPacket.class, this::acceptHeader);
        this.register(DataPacket.ID, DataPacket.class, this::acceptData);
        this.register(DataTransferredPacket.ID, DataTransferredPacket.class, this::acceptTransferred);
    }

    public BufferedDataClient(Client child) {
        this(child, DEFAULT_PACKET_LENGTH);
    }

    public BufferedDataClient(CachedIoClient client) {
        this((Client) client);

        client.ignoreFromCache(DataPacket.ID);
    }

    protected abstract SeekableByteChannel buildChannel(String id) throws IOException;

    @SuppressWarnings("unchecked")
    protected <T> void register(long id, Class<? extends Packet> clazz, Consumer<T> eventConsumer) {
        this.getRegistry().registerPacket(id, clazz);
        this.getEventHandler().subscribe(PacketReceiveEvent.class, (event) -> {
            if (event.packetId() == id)
                eventConsumer.accept((T) event.packet());
        });
    }

    private void acceptHeader(DataHeaderPacket packet) {
        try {
            this.channelMap.put(packet.uuid(), this.buildChannel(packet.uuid()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void acceptData(DataPacket packet) {
        SeekableByteChannel out = this.channelMap.get(packet.uuid());
        if (out == null) return;
        try {
            out.position(packet.position());
            out.write(ByteBuffer.wrap(packet.data()));
        } catch (IOException e) {
            this.channelMap.remove(packet.uuid());
        }

    }

    protected void acceptTransferred(DataTransferredPacket packet) {
        SeekableByteChannel channel = this.channelMap.remove(packet.uuid());
        if (channel != null) {
            try {
                channel.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        this.child.getEventHandler().notify(new FileReceivedEvent(null)); // TODO: Fix
    }

    @Override
    public CompletableFuture<Boolean> send(SeekableByteChannel channel) {
        return this.send(UUID.randomUUID().toString(), channel);
    }

    @Override
    public CompletableFuture<Boolean> send(String uuid, SeekableByteChannel channel) {
        return CompletableFutureUtil.supplyAsync(() -> {
            // Header
            long size = channel.size();
            this.send(new DataHeaderPacket(uuid, size)).get();

            // Data
            for (int i = 0; i < size; i += this.packetLength) {
                ByteBuffer buffer = ByteBuffer.allocate(this.packetLength);
                channel.read(buffer);
                Thread.sleep(10);
                this.child.send(new DataPacket(uuid, i, buffer.array())).get();
            }

            // Finalize
            this.send(new DataTransferredPacket(uuid)).get();
        }, true);
    }

    @Override
    public CompletableFuture<Void> connect() {
        return this.child.connect();
    }

    @Override
    public CompletableFuture<Void> disconnect() {
        return this.child.disconnect();
    }

    @Override
    public boolean isConnected() {
        return this.child.isConnected();
    }

    @Override
    public CompletableFuture<Void> send(Packet packet) {
        return this.child.send(packet);
    }

    @Override
    public InetAddress getAddress() {
        return this.child.getAddress();
    }

    @Override
    public int getPort() {
        return this.child.getPort();
    }

    @Override
    public EventHandler getEventHandler() {
        return this.child.getEventHandler();
    }

    @Override
    public PacketRegistry getRegistry() {
        return this.child.getRegistry();
    }
}