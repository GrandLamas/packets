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

import de.lama.packets.MessagePacket;
import de.lama.packets.Packet;
import de.lama.packets.large.LargePacket;
import de.lama.packets.registry.HashedPacketRegistry;
import de.lama.packets.registry.PacketRegistry;
import de.lama.packets.wrapper.gson.GsonFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;

public class WrapperTest {

    static LargePacket LARGE_PACKET = new LargePacket(19);

    private PacketWrapper toTest;

    @BeforeEach
    public void initWrapper() {
        PacketRegistry registry = new HashedPacketRegistry();
        registry.registerPacket(MessagePacket.ID, MessagePacket.class);
        registry.registerPacket(LargePacket.ID, LargePacket.class);
        this.toTest = new GsonFactory().create(registry);
    }

    @Test
    public void testWrap() {
        ByteBuffer byteBuffer = this.toTest.wrap(MessagePacket.ID, new MessagePacket("Test"), Packet.RESERVED, -1);
        byteBuffer.position(Packet.RESERVED);
        Assertions.assertTrue(new String(byteBuffer.array()).contains("Test"));
    }

    @Test
    public void test() {
        MessagePacket packet = new MessagePacket("Test");
        ByteBuffer byteBuffer = this.toTest.wrap(MessagePacket.ID, packet, Packet.RESERVED, packet.length());
        byteBuffer.position(Packet.RESERVED);
        Packet unwrapped = this.toTest.unwrap(MessagePacket.ID, byteBuffer);
        Assertions.assertEquals(packet, unwrapped);
    }

    @Test
    public void testLarge() {
        ByteBuffer byteBuffer = this.toTest.wrap(LargePacket.ID, LARGE_PACKET, Packet.RESERVED, LARGE_PACKET.length());
        byteBuffer.position(Packet.RESERVED);
        LargePacket unwrapped = (LargePacket) this.toTest.unwrap(LargePacket.ID, byteBuffer);
        Assertions.assertArrayEquals(LARGE_PACKET.bytes(), unwrapped.bytes());
    }
}
