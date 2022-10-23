package de.lama.packets.wrapper;

import com.google.gson.Gson;
import de.lama.packets.Packet;
import de.lama.packets.registry.PacketRegistry;
import de.lama.packets.wrapper.cache.PacketCache;
import de.lama.packets.wrapper.cache.ScheduledPacketCache;

public class CachedGsonWrapper implements PacketStringWrapper {

    private final PacketRegistry registry;
    private final PacketCache cache;
    private final Gson gson;

    public CachedGsonWrapper(PacketRegistry registry) {
        this.registry = registry;
        this.cache = new ScheduledPacketCache();
        this.gson = new Gson();
    }

    @Override
    public byte[] wrap(long packetId, Packet packet) {
        byte[] cached = this.cache.load(packetId, packet);
        return cached != null ? cached : PacketStringWrapper.super.wrap(packetId, packet);
    }

    @Override
    public Packet unwrap(long packetId, byte[] bytes) {
        Packet cached = this.cache.load(packetId, bytes);
        return cached != null ? cached : PacketStringWrapper.super.unwrap(packetId, bytes);
    }

    @Override
    public String wrapString(long packetId, Packet packet) {
        return this.gson.toJson(packet);
    }

    @Override
    public Packet unwrapString(long packetId, String from) {
        Class<? extends Packet> parsed = this.registry.parseClass(packetId);
        if (parsed == null) throw new NullPointerException("Invalid packet id");
        return this.gson.fromJson(from, parsed);
    }
}
