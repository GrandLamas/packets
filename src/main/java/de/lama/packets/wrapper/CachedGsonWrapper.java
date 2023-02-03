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
        byte[] bytes = this.cache.load(packetId, packet);
        if (bytes == null) {
            bytes = PacketStringWrapper.super.wrap(packetId, packet);
            this.cache.cache(packetId, bytes, packet);
        }
        return bytes;
    }

    @Override
    public Packet unwrap(long packetId, byte[] bytes) {
        Packet packet = this.cache.load(packetId, bytes);
        if (packet == null) {
            packet = PacketStringWrapper.super.unwrap(packetId, bytes);
            this.cache.cache(packetId, bytes, packet);
        }
        return packet;
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
