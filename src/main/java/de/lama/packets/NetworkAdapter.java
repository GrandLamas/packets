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

package de.lama.packets;

import de.lama.packets.event.EventHandlerContainer;

import java.net.InetAddress;
import java.util.concurrent.CompletableFuture;

public interface NetworkAdapter extends EventHandlerContainer {

    int DEFAULT_TICKRATE = 0;

    CompletableFuture<Void> connect();

    /**
     * Shuts the adapter down indefinitely.
     * A shutdown is definite and cannot be undone.
     *
     * @return the operation of the shutdown
     */
    CompletableFuture<Void> disconnect();

    /**
     * Returns whether the adapter has been shutdown or not.
     *
     * @return if the adapter has been shutdown
     */
    boolean isConnected();

    /**
     * Returns the address of the server.
     *
     * @return the address of the server.
     */
    InetAddress getAddress();

    /**
     * Returns the port of the server.
     *
     * @return the port of the server
     */
    int getPort();

}
