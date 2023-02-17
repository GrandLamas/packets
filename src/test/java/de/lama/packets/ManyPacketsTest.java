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

package de.lama.packets;

import de.lama.packets.client.events.PacketReceiveEvent;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;

public class ManyPacketsTest extends DefaultConnection {

    static int AMOUNT = 100;

    private static final Packet PACKET = new MessagePacket(":)");

    private AtomicInteger received;

    @BeforeEach
    public void resetReceived() {
        this.received = new AtomicInteger();
    }

    @Test
    public void sendPacketsClientToServer() throws ExecutionException, InterruptedException {
        this.server.getEventHandler().subscribe(PacketReceiveEvent.class, (event) -> {
            if (event.packetId() == MessagePacket.ID)
                received.incrementAndGet();
        });
        for (int i = 0; i < AMOUNT; i++) {
            CompletableFuture<Void> sent = this.client.send(PACKET);
            sent.get();
        }
        while (this.received.get() != AMOUNT) {
            Thread.sleep(1);
        }
        Assertions.assertEquals(this.received.get(), AMOUNT);
    }

    @Test
    public void sendPacketsServerToClient() throws ExecutionException, InterruptedException {
        this.client.getEventHandler().subscribe(PacketReceiveEvent.class, (event) -> {
            if (event.packetId() == MessagePacket.ID)
                received.incrementAndGet();
        });
        for (int i = 0; i < AMOUNT; i++) {
            this.server.broadcast(PACKET).get();
        }
        while (this.received.get() != AMOUNT) {
            Thread.sleep(1);
        }
        Assertions.assertEquals(this.received.get(), AMOUNT);
    }
}
