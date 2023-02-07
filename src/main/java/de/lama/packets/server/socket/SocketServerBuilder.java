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

package de.lama.packets.server.socket;

import de.lama.packets.client.stream.socket.SocketClientBuilder;
import de.lama.packets.registry.HashedPacketRegistry;
import de.lama.packets.registry.PacketRegistry;
import de.lama.packets.server.Server;
import de.lama.packets.util.exception.ExceptionHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Objects;
import java.util.function.Supplier;

public class SocketServerBuilder {

    private static final Supplier<PacketRegistry> DEFAULT_REGISTRY = HashedPacketRegistry::new;

    private SocketClientBuilder socketClientBuilder;
    private ExceptionHandler exceptionHandler;
    private PacketRegistry registry;

    private PacketRegistry buildRegistry() {
        return Objects.requireNonNullElseGet(this.registry, DEFAULT_REGISTRY);
    }

    private SocketClientBuilder buildClientBuilder() {
        return Objects.requireNonNullElseGet(this.socketClientBuilder, SocketClientBuilder::new).clone();
    }

    private ExceptionHandler buildExceptionHandler() {
        return Objects.requireNonNullElse(this.exceptionHandler, Exception::printStackTrace);
    }

    private ServerSocket createSocket(int port) throws IOException {
        return new ServerSocket(port);
    }

    public SocketServerBuilder registry(PacketRegistry registry) {
        this.registry = registry;
        return this;
    }

    public SocketServerBuilder clients(SocketClientBuilder clientFactory) {
        this.socketClientBuilder = clientFactory;
        return this;
    }

    public SocketServerBuilder exceptionHandler(ExceptionHandler exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
        return this;
    }

    public Server build(int port) throws IOException {
        ExceptionHandler exceptionHandler = this.buildExceptionHandler();
        PacketRegistry registry = this.buildRegistry();
        SocketClientBuilder socketClientBuilder = this.buildClientBuilder().exceptionHandler(exceptionHandler).registry(registry);
        return new UniqueSocketServer(this.createSocket(port), socketClientBuilder, registry, exceptionHandler);
    }
}
