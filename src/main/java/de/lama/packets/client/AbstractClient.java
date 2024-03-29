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
import de.lama.packets.client.events.PacketReceiveEvent;
import de.lama.packets.client.events.PacketSendEvent;
import de.lama.packets.operation.Operation;
import de.lama.packets.operation.ParentOperation;
import de.lama.packets.registry.PacketRegistry;
import de.lama.packets.util.exception.ExceptionHandler;

import java.util.Objects;

public abstract class AbstractClient extends AbstractNetworkAdapter implements Client {

    public AbstractClient(ExceptionHandler exceptionHandler, PacketRegistry registry) {
        super(exceptionHandler, registry);
    }

    protected PacketReceiveEvent packetReceived(long packetId, Packet packet) {
        PacketReceiveEvent event = new PacketReceiveEvent(this, packetId, packet);
        this.getEventHandler().notify(event);
        return event;
    }

    protected abstract Operation executeSend(long packedId, Packet packet);

    @Override
    public Operation send(Packet packet) {
        final long packetId = this.getRegistry().parseId(Objects.requireNonNull(packet).getClass());
        if (packetId == PacketRegistry.PACKET_NOT_FOUND)
            throw new IllegalArgumentException("No such packet");

        return new ParentOperation(this.executeSend(packetId, packet), () -> {
            if (this.hasShutdown()) {
                this.handle(new IllegalStateException("Client already closed"));
                return false;
            }

            return !this.getEventHandler().isCancelled(new PacketSendEvent(this, packetId, packet));
        });
    }

    @Override
    public boolean isClosed() {
        return this.hasShutdown();
    }
}
