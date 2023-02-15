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

package de.lama.packets.client;

import de.lama.packets.AbstractNetworkAdapter;
import de.lama.packets.Packet;
import de.lama.packets.client.events.PacketSendEvent;
import de.lama.packets.client.io.IdPacket;
import de.lama.packets.registry.PacketRegistry;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public abstract class AbstractClient<C> extends AbstractNetworkAdapter<C> implements Client {

    private final PacketRegistry registry;

    public AbstractClient(PacketRegistry registry, int tickrate) {
        super(tickrate);
        this.registry = registry;
    }

    public AbstractClient(PacketRegistry registry) {
        this(registry, DEFAULT_TICKRATE);
    }

    protected abstract CompletableFuture<Void> implSend(IdPacket packet);

    @Override
    public CompletableFuture<Void> send(Packet packet) {
        final long packetId = this.getRegistry().parseId(Objects.requireNonNull(packet).getClass());
        if (packetId == PacketRegistry.PACKET_NOT_FOUND)
            throw new IllegalArgumentException("No such packet");

        if (!this.isConnected()) {
            throw new IllegalStateException("Cannot send packet while closed");
        } else if (this.getEventHandler().isCancelled(new PacketSendEvent(this, packetId, packet))) {
            return null;
        }

        return this.implSend(new IdPacket(packetId, packet));
    }

    @Override
    public PacketRegistry getRegistry() {
        return this.registry;
    }
}
