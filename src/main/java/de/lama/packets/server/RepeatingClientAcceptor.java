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

package de.lama.packets.server;

import de.lama.packets.operation.AbstractRepeatingOperation;
import de.lama.packets.operation.Operation;
import de.lama.packets.util.exception.ExceptionHandler;

import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Function;

public class RepeatingClientAcceptor extends AbstractRepeatingOperation {

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final ExecutorService registerPool = Executors.newCachedThreadPool();
    private final ServerSocket socket;
    private final Function<Socket, Boolean> clientRegister;
    private final ExceptionHandler exceptionHandler;

    public RepeatingClientAcceptor(ServerSocket socket, Function<Socket, Boolean> clientRegister, ExceptionHandler exceptionHandler) {
        this.socket = socket;
        this.clientRegister = clientRegister;
        this.exceptionHandler = exceptionHandler;
    }

    @Override
    public Operation queue() {
        this.executor.submit(this::complete);
        return this;
    }

    @Override
    public Operation complete() {
        try {
            Socket socket = this.socket.accept();
            this.registerPool.submit(() -> this.clientRegister.apply(socket));
        } catch (SocketException e) {
            return this;
        } catch (Exception e) {
            this.exceptionHandler.accept(e);
        }
        return this;
    }

    @Override
    protected Future<?> createRepeatingTask() {
        return this.executor.submit(() -> {
            while (!this.socket.isClosed()) {
                this.complete();
            }
        });
    }
}
