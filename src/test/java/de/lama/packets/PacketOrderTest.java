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

import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;

public class PacketOrderTest extends DefaultConnection {

    static int AMOUNT = 10000;

    private AtomicInteger order;

    private void packetReceived(PacketReceiveEvent event) {
        if (event.packetId() == MessagePacket.ID) {
            String shallBe = String.valueOf(order.getAndIncrement());
            Assertions.assertEquals(shallBe, ((MessagePacket) event.packet()).message());
        }
    }

    @BeforeEach
    public void initOrder() {
        this.order = new AtomicInteger();
    }

    @Test
    public void sendPacketsClientToServer() throws ExecutionException, InterruptedException {
        this.server.getEventHandler().subscribe(PacketReceiveEvent.class, this::packetReceived);
        for (int i = 0; i < AMOUNT; i++) {
            this.client.send(new MessagePacket(String.valueOf(i))).get();
        }
    }

    @Test
    public void sendPacketsServerToClient() throws ExecutionException, InterruptedException {
        this.client.getEventHandler().subscribe(PacketReceiveEvent.class, this::packetReceived);
        for (int i = 0; i < AMOUNT; i++) {
            this.server.broadcast(new MessagePacket(String.valueOf(i))).get();
        }
    }
}
