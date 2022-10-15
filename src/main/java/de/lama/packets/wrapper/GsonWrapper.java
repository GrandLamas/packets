package de.lama.packets.wrapper;

import com.google.gson.Gson;
import de.lama.packets.Packet;
import de.lama.packets.registry.PacketRegistry;

public class GsonWrapper extends AbstractPacketWrapper implements PacketStringWrapper {

    private final Gson gson;

    public GsonWrapper(PacketRegistry registry) {
        super(registry);
        this.gson = new Gson();
    }

    @Override
    public String wrapString(Packet packet) {
        return this.gson.toJson(packet);
    }

    @Override
    public Packet unwrapString(String from) {
        Packet packet = this.gson.fromJson(from, Packet.class);
        Class<? extends Packet> parsed = this.parse(packet.getId());
        if (parsed == null) throw new NullPointerException("Invalid packet id");
        return this.gson.fromJson(from, parsed);
    }
}
