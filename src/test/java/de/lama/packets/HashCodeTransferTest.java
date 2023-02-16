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
import de.lama.packets.client.nio.data.DataPacket;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;
import java.util.concurrent.ExecutionException;

public class HashCodeTransferTest extends DefaultConnection {

    @BeforeAll
    public static void registerPackets() {
        registry.registerPacket(DataPacket.ID, DataPacket.class);
    }

    @Test
    public void sendAndTestHashCode() throws ExecutionException, InterruptedException {
        ByteBuffer random = ByteBuffer.allocate(1000000);
        // put random bytes into random buffer
        for (int i = 0; i < random.capacity(); i++) {
            random.put((byte) (Math.random() * 255));
        }
        random.flip();
        int hashCode = random.hashCode();
        this.server.getEventHandler().subscribe(PacketReceiveEvent.class, event -> {
            if (event.packetId() == DataPacket.ID) {
                Assertions.assertEquals(hashCode, event.packet().hashCode());
            }
        });
        this.client.send(new DataPacket(null, 0, random.array())).get();
    }
}
