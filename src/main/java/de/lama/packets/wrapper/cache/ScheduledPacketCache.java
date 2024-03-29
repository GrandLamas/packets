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

package de.lama.packets.wrapper.cache;

import de.lama.packets.Packet;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ScheduledPacketCache implements PacketCache {

    private static final ScheduledExecutorService POOL = Executors.newScheduledThreadPool(10);
    private static final long COLLECT_DELAY_SECS = 30;
    private static final long REMOVE_TIME_SECS = COLLECT_DELAY_SECS;

    private final Map<Long, Map<Integer, CachedObject<byte[]>>> byteCache;
    private final Map<Long, Map<Integer, CachedObject<Packet>>> packetCache;

    public ScheduledPacketCache() {
        this.byteCache = new ConcurrentHashMap<>();
        this.packetCache = new ConcurrentHashMap<>();

        this.startTimer();
    }

    private void collectGarbage() {
        var current = System.currentTimeMillis();
        long removeTimeMillis = REMOVE_TIME_SECS * 1000;
        for (var id : this.byteCache.keySet()) {
            var cached = this.byteCache.get(id);
            for (var key : cached.keySet()) {
                var finalCache = cached.get(key);
                if (finalCache.lastAccess() + removeTimeMillis >= current) {
                    cached.remove(key);
                    if (cached.size() == 0) {
                        this.byteCache.remove(id);
                    }
                }
            }
        }

        for (var id : this.packetCache.keySet()) {
            var cached = this.packetCache.get(id);
            for (var key : cached.keySet()) {
                var finalCache = cached.get(key);
                if (finalCache.lastAccess() + removeTimeMillis >= current) {
                    cached.remove(key);
                    if (cached.size() == 0) {
                        this.packetCache.remove(id);
                    }
                }
            }
        }
    }

    private void startTimer() {
        POOL.scheduleWithFixedDelay(this::collectGarbage, COLLECT_DELAY_SECS, COLLECT_DELAY_SECS, TimeUnit.SECONDS);
    }

    @Override
    public void cache(long id, byte[] data, Packet packet) {
        this.byteCache.putIfAbsent(id, new ConcurrentHashMap<>());
        this.byteCache.get(id).put(packet.hashCode(), new CachedObject<>(data, System.currentTimeMillis()));

        this.packetCache.putIfAbsent(id, new ConcurrentHashMap<>());
        this.packetCache.get(id).put(Arrays.hashCode(data), new CachedObject<>(packet, System.currentTimeMillis()));
    }

    @Override
    public Packet load(long id, byte[] data) {
        var cache = this.packetCache.get(id);
        var cachedData = cache != null ? cache.get(Arrays.hashCode(data)) : null;
        return cachedData == null ? null : cachedData.object();
    }

    @Override
    public byte[] load(long id, Packet packet) {
        var cache = this.byteCache.get(id);
        var cachedData = cache != null ? cache.get(packet.hashCode()) : null;
        return cachedData == null ? null : cachedData.object();
    }
}
