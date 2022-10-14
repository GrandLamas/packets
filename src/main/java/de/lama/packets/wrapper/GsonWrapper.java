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
        return this.gson.fromJson(from, this.parse(packet.getId()));
    }
}
