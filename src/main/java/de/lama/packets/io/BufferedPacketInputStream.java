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

package de.lama.packets.io;

import de.lama.packets.Packet;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

public class BufferedPacketInputStream extends InputStream implements PacketInputStream {

    private final DataInputStream in;

    public BufferedPacketInputStream(InputStream in) {
        this.in = new DataInputStream(new BufferedInputStream(in));
    }

    @Override
    public IoPacket readPacket() throws IOException {
        char type = this.in.readChar();
        if (type != Packet.TYPE) return null;

        // HEAD
        long packetId = this.in.readLong();
        int length = this.in.readInt();
        byte[] buffer = new byte[length];

        // DATA
        int read = 0;
        while (read < length) {
            read += this.in.read(buffer, read, buffer.length - read);
        }

        return new CachedIoPacket(type, packetId, length, buffer);
    }

    @Override
    public int available() throws IOException {
        return this.in.available();
    }

    @Override
    public int read() throws IOException {
        return this.in.read();
    }

    @Override
    public void close() throws IOException {
        this.in.close();
    }
}
