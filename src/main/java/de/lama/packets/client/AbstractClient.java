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
import de.lama.packets.event.events.PacketReceiveEvent;
import de.lama.packets.event.events.PacketSendEvent;
import de.lama.packets.operation.Operation;
import de.lama.packets.operation.SimpleOperation;
import de.lama.packets.registry.PacketRegistry;
import de.lama.packets.stream.CachedIoPacket;
import de.lama.packets.stream.IoPacket;
import de.lama.packets.util.exception.ExceptionHandler;
import de.lama.packets.wrapper.PacketWrapper;

import java.util.Objects;

public abstract class AbstractClient extends AbstractNetworkAdapter implements Client {

    private final PacketWrapper wrapper;

    public AbstractClient(ExceptionHandler exceptionHandler, PacketRegistry registry, PacketWrapper wrapper) {
        super(exceptionHandler, registry);

        this.wrapper = wrapper;
    }

    protected PacketReceiveEvent wrapEvent(IoPacket ioPacket) {
        return new PacketReceiveEvent(this, ioPacket.id(), this.parsePacket(ioPacket));
    }

    protected void packetReceived(IoPacket ioPacket) {
        this.getEventHandler().notify(this.wrapEvent(ioPacket));
    }

    protected Packet parsePacket(IoPacket packet) {
        return this.wrapper.unwrap(packet.id(), packet.data());
    }

    protected IoPacket parsePacket(long packetId, Packet packet) {
        return new CachedIoPacket(packetId, this.wrapper.wrap(packetId, packet));
    }

    protected abstract void executeSend(boolean async, long packedId, Packet packet);

    @Override
    public Operation send(Packet packet) {
        Objects.requireNonNull(packet);
        return new SimpleOperation((async) -> {
            if (this.hasShutdown()) {
                this.handle(new IllegalStateException("Client already closed"));
                return;
            }

            long packetId = this.getRegistry().parseId(packet.getClass());
            if (this.getEventHandler().isCancelled(new PacketSendEvent(this, packetId, packet))) return;
            this.executeSend(async, packetId, packet);
        });
    }

    @Override
    public boolean isClosed() {
        return this.hasShutdown();
    }
}
