package de.lama.packets.wrapper;

import com.google.gson.Gson;
import de.lama.packets.Packet;
import de.lama.packets.registry.PacketRegistry;

public class GsonWrapper implements PacketStringWrapper {

    private final PacketRegistry registry;
    private final Gson gson;

    public GsonWrapper(PacketRegistry registry) {
        this.registry = registry;
        this.gson = new Gson();
    }

    @Override
    public String wrapString(Packet packet) {
        return this.gson.toJson(packet);
    }

    @Override
    public Packet unwrapString(long packetId, String from) {
        Class<? extends Packet> parsed = this.registry.parseClass(packetId);
        if (parsed == null) throw new NullPointerException("Invalid packet id");
        return this.gson.fromJson(from, parsed);
    }
}
