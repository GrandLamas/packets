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

package de.lama.packets.stream.transceiver.receiver;

import java.io.*;

public class BufferedFileReceiver implements FileReceiver {

    private final File file;
    private final BufferedOutputStream stream;

    public BufferedFileReceiver(File file) throws IOException {
        if (!file.exists() && !file.createNewFile()) throw new IOException("Could not create file");
        this.file = file;
        this.stream = new BufferedOutputStream(new FileOutputStream(file));
    }

    @Override
    public void write(byte[] data) throws IOException {
        this.stream.write(data);
    }

    @Override
    public void finish() throws IOException {
        this.stream.flush();
        this.stream.close();
    }

    @Override
    public File getFile() {
        return this.file;
    }
}
