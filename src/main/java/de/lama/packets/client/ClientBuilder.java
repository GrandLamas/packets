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

package de.lama.packets.client;

import de.lama.packets.client.transceiver.receiver.PacketReceiver;
import de.lama.packets.client.transceiver.receiver.PacketReceiverBuilder;
import de.lama.packets.client.transceiver.transmitter.PacketTransmitter;
import de.lama.packets.client.transceiver.transmitter.PacketTransmitterBuilder;
import de.lama.packets.registry.HashedPacketRegistry;
import de.lama.packets.registry.PacketRegistry;
import de.lama.packets.util.exception.ExceptionHandler;
import de.lama.packets.wrapper.GsonFactory;
import de.lama.packets.wrapper.PacketWrapper;
import de.lama.packets.wrapper.WrapperFactory;

import java.io.IOException;
import java.net.Socket;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Supplier;

public class ClientBuilder implements Cloneable {

    private static final ScheduledExecutorService TRANSCEIVER_POOL = Executors.newScheduledThreadPool(10);

    private static final int TICKRATE = (int) Math.pow(2, 4); // 16
    static final int TICKRATE_LIMIT = 1000;
    static final Supplier<PacketRegistry> DEFAULT_REGISTRY = HashedPacketRegistry::new;
    static final Supplier<WrapperFactory> DEFAULT_WRAPPER = GsonFactory::new;

    private ExceptionHandler exceptionHandler;
    private PacketRegistry registry;
    private WrapperFactory wrapperFactory;
    private int tickrate;

    public ClientBuilder() {
        this.tickrate = TICKRATE;
        this.exceptionHandler = Exception::printStackTrace;
    }

    private PacketRegistry buildRegistry() {
        return Objects.requireNonNullElseGet(this.registry, DEFAULT_REGISTRY);
    }

    private PacketTransmitter buildTransmitter(Socket socket) throws IOException {
        return new PacketTransmitterBuilder().threadPool(TRANSCEIVER_POOL).tickrate(this.tickrate)
                .exceptionHandler(this.exceptionHandler).build(socket.getOutputStream());
    }

    private PacketReceiver buildReceiver(Socket socket) throws IOException {
        return new PacketReceiverBuilder().threadPool(TRANSCEIVER_POOL).tickrate(this.tickrate)
                .exceptionHandler(this.exceptionHandler).build(socket.getInputStream());
    }

    private PacketWrapper buildWrapper(PacketRegistry registry) {
        return Objects.requireNonNullElseGet(this.wrapperFactory, DEFAULT_WRAPPER).create(registry);
    }

    private ExceptionHandler buildHandler() {
        return Objects.requireNonNullElse(this.exceptionHandler, Exception::printStackTrace);
    }

    public ClientBuilder registry(PacketRegistry registry) {
        this.registry = Objects.requireNonNull(registry);
        return this;
    }

    public ClientBuilder wrapper(WrapperFactory wrapperFactory) {
        this.wrapperFactory = wrapperFactory;
        return this;
    }

    public ClientBuilder tickrate(int tickrate) {
        if (tickrate > TICKRATE_LIMIT) throw new IllegalArgumentException("Tickrate out of bounds");
        this.tickrate = tickrate;
        return this;
    }

    public ClientBuilder exceptionHandler(ExceptionHandler exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
        return this;
    }

    public Client build(Socket socket) throws IOException {
        PacketRegistry registry = this.buildRegistry();
        return new HandshakeClient(new ConnectedClient(socket, registry, this.buildWrapper(registry), this.buildTransmitter(socket), this.buildReceiver(socket), this.buildHandler()));
    }

    public Client build(String address, int port) throws IOException {
        return this.build(new Socket(address, port));
    }

    @Override
    public ClientBuilder clone() {
        try {
            ClientBuilder clone = (ClientBuilder) super.clone();
            clone.exceptionHandler = this.exceptionHandler;
            clone.registry = this.registry;
            clone.tickrate = this.tickrate;
            clone.wrapperFactory = this.wrapperFactory;
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
