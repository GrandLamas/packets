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

package de.lama.packets.file;

import de.lama.packets.DefaultConnection;
import de.lama.packets.client.events.PacketReceiveEvent;
import de.lama.packets.client.file.BufferedFileClient;
import de.lama.packets.client.file.FileClient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class SendFileTest extends DefaultConnection {

    private static final File SOURCE = new File("100MB.bin");
    private static final File DEST = new File("received.txt");

    private FileClient fileClient;

    @BeforeEach
    public void initFileClient() throws IOException {
        this.fileClient = new BufferedFileClient(this.client, s -> DEST);

        if (DEST.exists() && !DEST.delete()) {
            throw new IOException("Could not delete old file");
        }
    }

    @Test
    public void sendFileTest() throws IOException, InterruptedException {
        this.server.getClients().forEach(client -> client.getEventHandler().subscribe(PacketReceiveEvent.class, (event) ->
                event.source().send(event.packet()).complete()));
        this.fileClient.send(SOURCE).complete();
        Thread.sleep(1000);
        Assertions.assertEquals(-1L, Files.mismatch(SOURCE.toPath(), DEST.toPath()));
    }
}
