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

package de.lama.packets.wrapper.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.lama.packets.Packet;
import de.lama.packets.registry.PacketRegistry;
import de.lama.packets.wrapper.PacketCharWrapper;

import java.nio.CharBuffer;

public class GsonWrapper implements PacketCharWrapper {

    private final PacketRegistry registry;
    private final Gson gson;

    public GsonWrapper(GsonBuilder builder, PacketRegistry registry) {
        this.registry = registry;
        this.gson = builder.create();
    }

    @Override
    public CharBuffer wrapString(long packetId, Packet packet, int offsetInBytes, int lenInBytes) {
        CharArrayOffsetWriter writer = new CharArrayOffsetWriter(Math.max(0, lenInBytes), offsetInBytes);
        this.gson.toJson(packet, writer);
        return CharBuffer.wrap(writer.toCharArray());
    }

    @Override
    public Packet unwrapString(long packetId, CharBuffer from) {
        Class<? extends Packet> parsed = this.registry.parseClass(packetId);
        if (parsed == null) throw new NullPointerException("Invalid packet id");
        return this.gson.fromJson(new String(from.array()), parsed);
    }
}
