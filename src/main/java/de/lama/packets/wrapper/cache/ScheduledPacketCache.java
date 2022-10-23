package de.lama.packets.wrapper.cache;

import de.lama.packets.Packet;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ScheduledPacketCache implements PacketCache {

    private static final ScheduledExecutorService POOL = Executors.newScheduledThreadPool(10);
    private static final long DELAY_SECONDS = 60; // 60s
    private static final long NO_ACCESS_REMOVE = 10 * 60 * 1000; // 10min

    private final Map<Long, Map<Packet, CachedObject<byte[]>>> byteCache;
    private final Map<Long, Map<byte[], CachedObject<Packet>>> packetCache;

    public ScheduledPacketCache() {
        this.byteCache = new ConcurrentHashMap<>();
        this.packetCache = new ConcurrentHashMap<>();

        this.startTimer();
    }

    private void collectGarbage() {
        var current = System.currentTimeMillis();
        for (var id : this.byteCache.keySet()) {
            var cached = this.byteCache.get(id);
            for (var key : cached.keySet()) {
                var finalCache = cached.get(key);
                if (finalCache.lastAccess() + NO_ACCESS_REMOVE >= current) {
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
                if (finalCache.lastAccess() + NO_ACCESS_REMOVE >= current) {
                    cached.remove(key);
                    if (cached.size() == 0) {
                        this.packetCache.remove(id);
                    }
                }
            }
        }
    }

    private void startTimer() {
        POOL.scheduleWithFixedDelay(this::collectGarbage, DELAY_SECONDS, DELAY_SECONDS, TimeUnit.SECONDS);
    }

    @Override
    public void cache(long id, Packet packet, byte[] data) {
        this.byteCache.putIfAbsent(id, new ConcurrentHashMap<>());
        this.byteCache.get(id).put(packet, new CachedObject<>(data, System.currentTimeMillis()));
    }

    @Override
    public void cache(long id, byte[] data, Packet packet) {
        this.packetCache.putIfAbsent(id, new ConcurrentHashMap<>());
        this.packetCache.get(id).put(data, new CachedObject<>(packet, System.currentTimeMillis()));
    }

    @Override
    public Packet load(long id, byte[] data) {
        var cache = this.packetCache.get(id);
        return cache == null ? null : cache.get(data).object();
    }

    @Override
    public byte[] load(long id, Packet packet) {
        var cache = this.byteCache.get(id);
        return cache == null ? null : cache.get(packet).object();
    }
}