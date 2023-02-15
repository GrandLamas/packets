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

package de.lama.packets.client.nio.file;

import de.lama.packets.client.Client;
import de.lama.packets.client.events.FileReceivedEvent;
import de.lama.packets.client.nio.data.BufferedDataClient;
import de.lama.packets.client.nio.data.DataTransferredPacket;
import de.lama.packets.client.nio.data.FileClient;
import de.lama.packets.util.CompletableFutureUtil;

import java.io.File;
import java.io.IOException;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class BufferedFileClient extends BufferedDataClient implements FileClient {

    private final FileMapper mapper;
    private final Map<String, File> fileMap;

    public BufferedFileClient(Client child, FileMapper fileMapper, int packetLengthBytes) {
        super(child, packetLengthBytes);

        this.mapper = fileMapper;
        this.fileMap = new ConcurrentHashMap<>();
    }

    public BufferedFileClient(Client child, FileMapper fileMapper) {
        super(child);
        this.mapper = fileMapper;
        this.fileMap = new ConcurrentHashMap<>();
    }

    @Override
    protected void acceptTransferred(DataTransferredPacket packet) {
        super.acceptTransferred(packet);
        this.fileMap.remove(packet.uuid());
        this.getEventHandler().notify(new FileReceivedEvent(this.fileMap.get(packet.uuid())));
    }

    @Override
    protected SeekableByteChannel buildChannel(String id) throws IOException {
        File file = this.mapper.apply(id);
        Files.createFile(file.toPath());
        this.fileMap.put(id, file);
        return Files.newByteChannel(file.toPath(), StandardOpenOption.WRITE);
    }

    @Override
    public CompletableFuture<Boolean> send(File file) {
        return CompletableFutureUtil.supplyAsync(() -> this.send(Files.newByteChannel(file.toPath(), StandardOpenOption.READ)), true);
    }
}
