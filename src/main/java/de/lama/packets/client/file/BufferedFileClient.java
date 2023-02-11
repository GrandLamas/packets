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

package de.lama.packets.client.file;

import de.lama.packets.Packet;
import de.lama.packets.client.Client;
import de.lama.packets.client.events.PacketReceiveEvent;
import de.lama.packets.client.file.events.FileReceivedEvent;
import de.lama.packets.event.EventHandler;
import de.lama.packets.operation.AsyncOperation;
import de.lama.packets.operation.Operation;
import de.lama.packets.operation.Operations;
import de.lama.packets.registry.PacketRegistry;
import de.lama.packets.util.exception.ExceptionHandler;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class BufferedFileClient implements FileClient {

    private static final int DEFAULT_PACKET_LENGTH = (int) Math.pow(2, 14);

    private final Client child;
    private final FileMapper fileMapper;
    private final int packetLength;

    private final Map<UUID, FileReceiver> receiverMap;

    public BufferedFileClient(Client child, FileMapper fileMapper, int packetLength) {
        this.child = child;
        this.fileMapper = fileMapper;
        this.packetLength = packetLength;
        this.receiverMap = new ConcurrentHashMap<>();

        this.getRegistry().registerPacket(FileHeaderPacket.ID, FileHeaderPacket.class);
        this.getRegistry().registerPacket(FileDataPacket.ID, FileDataPacket.class);
        this.getRegistry().registerPacket(FileTransferredPacket.ID, FileTransferredPacket.class);

        this.getEventHandler().subscribe(PacketReceiveEvent.class, this::onEvent);
    }

    public BufferedFileClient(Client child, FileMapper fileMapper) {
        this(child, fileMapper, DEFAULT_PACKET_LENGTH);
    }

    private void onEvent(PacketReceiveEvent event) {
        if (event.packetId() == FileHeaderPacket.ID) this.acceptHeader((FileHeaderPacket) event.packet());
        else if (event.packetId() == FileDataPacket.ID) this.acceptData((FileDataPacket) event.packet());
        else if (event.packetId() == FileTransferredPacket.ID)
            this.acceptTransferred((FileTransferredPacket) event.packet());
    }

    private void acceptHeader(FileHeaderPacket packet) {
        File file = this.fileMapper.apply(packet.file());
        BufferedFileReceiver receiver = this.getExceptionHandler().operate(() -> new BufferedFileReceiver(file), "Could receive file");
        this.receiverMap.put(packet.uuid(), receiver);
    }

    private void acceptData(FileDataPacket packet) {
        FileReceiver receiver = this.receiverMap.get(packet.uuid());
        if (receiver == null) return;
        if (!this.getExceptionHandler().operate(() -> receiver.write(packet.data()), "Could not write bytes")) {
            this.receiverMap.remove(packet.uuid());
        }
    }

    private void acceptTransferred(FileTransferredPacket packet) {
        FileReceiver receiver = this.receiverMap.remove(packet.uuid());
        if (this.getExceptionHandler().operate(receiver::finish, "Could not close receiver"))
            this.getEventHandler().notify(new FileReceivedEvent(receiver.getFile()));
    }

    private void sendFile(boolean async, File file) throws IOException {
        UUID uuid = UUID.randomUUID();
        // Header
        Operations.execute(async, this.send(new FileHeaderPacket(uuid, file.getName(), file.length())));

        // Data
        BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(file));
        for (int i = 0; i < file.length(); i += this.packetLength) {
            FileDataPacket packet = new FileDataPacket(uuid, inputStream.readNBytes(this.packetLength));
            Operations.execute(async, this.send(packet));
        }

        // Finalize
        Operations.execute(async, this.send(new FileTransferredPacket(uuid)));
    }

    @Override
    public Operation open() {
        return this.child.open();
    }

    @Override
    public Operation close() {
        return this.child.close();
    }

    @Override
    public Operation shutdown() {
        return this.child.shutdown();
    }

    @Override
    public boolean isClosed() {
        return this.child.isClosed();
    }

    @Override
    public boolean hasShutdown() {
        return this.child.hasShutdown();
    }

    @Override
    public InetAddress getAddress() {
        return this.child.getAddress();
    }

    @Override
    public int getPort() {
        return this.child.getPort();
    }

    @Override
    public Operation send(Packet packet) {
        return this.child.send(packet);
    }

    @Override
    public PacketReceiveEvent read(long timeoutInMillis) {
        return this.child.read(timeoutInMillis);
    }

    @Override
    public Operation send(File file) {
        if (!Objects.requireNonNull(file).exists()) {
            throw new IllegalArgumentException("No such file");
        }

        return new AsyncOperation((async) -> this.getExceptionHandler().operate(() ->
                this.sendFile(async, file), "Could not transfer file"));
    }

    @Override
    public EventHandler getEventHandler() {
        return this.child.getEventHandler();
    }

    @Override
    public PacketRegistry getRegistry() {
        return this.child.getRegistry();
    }

    @Override
    public ExceptionHandler getExceptionHandler() {
        return this.child.getExceptionHandler();
    }
}
