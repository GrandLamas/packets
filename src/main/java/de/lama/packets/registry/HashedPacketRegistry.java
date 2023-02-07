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

package de.lama.packets.registry;

import de.lama.packets.Packet;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class HashedPacketRegistry implements PacketRegistry {

    private final Map<Long, Class<? extends Packet>> registry;
    private final Map<Class<? extends Packet>, Long> reverseRegistry;

    public HashedPacketRegistry(RegistryEntry<?>... entries) {
        this.registry = new ConcurrentHashMap<>();
        this.reverseRegistry = new ConcurrentHashMap<>();

        Arrays.stream(entries).forEach(e -> this.registerPacket(e.id(), e.clazz()));
    }

    @Override
    public <T extends Packet> boolean registerPacket(long id, Class<T> clazz) {
        if (Objects.equals(this.registry.get(id), clazz)) return false;
        this.registry.put(id, clazz);
        this.reverseRegistry.put(clazz, id);
        return true;
    }

    @Override
    public Class<? extends Packet> parseClass(long id) {
        return this.registry.getOrDefault(id, null);
    }

    @Override
    public long parseId(Class<? extends Packet> clazz) {
        return this.reverseRegistry.getOrDefault(clazz, PACKET_NOT_FOUND);
    }
}
