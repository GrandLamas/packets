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

package de.lama.packets.client.io;

import de.lama.packets.Packet;
import de.lama.packets.client.AbstractClient;
import de.lama.packets.client.events.PacketReceiveEvent;
import de.lama.packets.registry.PacketRegistry;
import de.lama.packets.util.CompletableFutureUtil;
import de.lama.packets.wrapper.PacketWrapper;
import de.lama.packets.wrapper.cache.PacketCache;
import de.lama.packets.wrapper.cache.ScheduledPacketCache;

import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public abstract class AbstractCachedIoClient extends AbstractClient<IoPacket> implements CachedIoClient {

    private final PacketWrapper wrapper;
    private final PacketCache cache;
    private final Collection<Long> ignored;

    public AbstractCachedIoClient(PacketRegistry registry, PacketWrapper wrapper, int tickrate) {
        super(registry, tickrate);

        this.wrapper = wrapper;
        this.cache = new ScheduledPacketCache();
        this.ignored = Collections.synchronizedSet(new HashSet<>());
    }

    public AbstractCachedIoClient(PacketRegistry registry, PacketWrapper wrapper) {
        this(registry, wrapper, DEFAULT_TICKRATE);
    }

    protected abstract Future<Void> sendData(long packetId, ByteBuffer packet);

    @Override
    protected Future<Boolean> process(IoPacket cached) {
        return this.unwrap(cached).thenApply((idPacket) -> {
            if (idPacket.packet() == null)
                throw new IllegalArgumentException("Could not unwrap packet with id " + idPacket.id() + "!");

            this.getEventHandler().notify(new PacketReceiveEvent(this, idPacket.id(), idPacket.packet()));
            return true;
        });
    }

    protected CompletableFuture<IdPacket> unwrap(IoPacket packet) {
        return CompletableFutureUtil.supplyAsync(() -> {
            int hashCode = packet.data().hashCode();
            Packet unwrapped = this.ignored.contains(packet.id()) ? null : this.cache.loadPacket(packet.id(), hashCode);
            if (unwrapped == null) {
                unwrapped = this.wrapper.unwrap(packet.id(), packet.data());
                this.cache.cachePacket(packet.id(), hashCode, unwrapped);
            }
            return new IdPacket(packet.id(), unwrapped);
        });
    }

    protected CompletableFuture<ByteBuffer> wrap(long packetId, Packet packet) {
        return CompletableFutureUtil.supplyAsync(() -> {
            int hashCode = packet.hashCode();
            ByteBuffer data = this.ignored.contains(packetId) ? null : this.cache.loadBytes(packetId, hashCode);
            if (data == null) {
                int len = packet.length() != Packet.NO_LENGTH ? packet.length() : -1;
                data = this.wrapper.wrap(packetId, packet, Packet.RESERVED, len);
                this.cache.cacheBytes(packetId, hashCode, data);
            }
            data.position(0);
            return data;
        });
    }

    @Override
    protected CompletableFuture<Void> implSend(IdPacket packet) {
        return this.wrap(packet.id(), packet.packet()).thenApply((buffer) ->  {
            try {
                this.sendData(packet.id(), buffer).get();
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
            return null;
        });
    }

    @Override
    public boolean ignoreFromCache(long packedId) {
        return this.ignored.add(packedId);
    }
}
