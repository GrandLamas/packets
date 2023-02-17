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

package de.lama.packets.client.nio.channel;

import de.lama.packets.NetworkAdapter;
import de.lama.packets.client.Client;
import de.lama.packets.client.HandshakeClient;
import de.lama.packets.registry.HashedPacketRegistry;
import de.lama.packets.registry.PacketRegistry;
import de.lama.packets.wrapper.PacketWrapper;
import de.lama.packets.wrapper.WrapperFactory;
import de.lama.packets.wrapper.gson.GsonFactory;

import java.io.IOException;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Supplier;

public class AsyncSocketChannelClientBuilder implements Cloneable {

    private static final ScheduledExecutorService TRANSCEIVER_POOL = Executors.newScheduledThreadPool(10);

    static final Supplier<PacketRegistry> DEFAULT_REGISTRY = HashedPacketRegistry::new;
    static final Supplier<WrapperFactory> DEFAULT_WRAPPER = GsonFactory::new;

    private PacketRegistry registry;
    private WrapperFactory wrapperFactory;
    private int tickrate = NetworkAdapter.DEFAULT_TICKRATE; // TODO: Changable

    private PacketRegistry buildRegistry() {
        return Objects.requireNonNullElseGet(this.registry, DEFAULT_REGISTRY);
    }

    private PacketWrapper buildWrapper(PacketRegistry registry) {
        return Objects.requireNonNullElseGet(this.wrapperFactory, DEFAULT_WRAPPER).create(registry);
    }

    public AsyncSocketChannelClientBuilder registry(PacketRegistry registry) {
        this.registry = Objects.requireNonNull(registry);
        return this;
    }

    public AsyncSocketChannelClientBuilder wrapper(WrapperFactory wrapperFactory) {
        this.wrapperFactory = wrapperFactory;
        return this;
    }

    public Client build(AsynchronousSocketChannel socket, String host, int port) throws IOException {
        PacketRegistry registry = this.buildRegistry();
        return new HandshakeClient(new AsyncSocketChannelOpenClient(socket, host, port, registry, this.buildWrapper(registry), this.tickrate), true);
    }

    public Client build(String address, int port) {
        PacketRegistry registry = this.buildRegistry();
        return new HandshakeClient(new AsyncSocketChannelClient(address, port, registry, this.buildWrapper(registry), this.tickrate), false);
    }

    @Override
    public AsyncSocketChannelClientBuilder clone() {
        try {
            AsyncSocketChannelClientBuilder clone = (AsyncSocketChannelClientBuilder) super.clone();
            clone.registry = this.registry;
            clone.wrapperFactory = this.wrapperFactory;
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
