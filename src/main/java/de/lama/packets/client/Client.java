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

import de.lama.packets.NetworkAdapter;
import de.lama.packets.Packet;
import de.lama.packets.client.events.PacketReceiveEvent;
import de.lama.packets.event.EventHandlerContainer;
import de.lama.packets.operation.Operation;
import de.lama.packets.registry.RegistryContainer;
import de.lama.packets.util.exception.ExceptionHandlerContainer;

public interface Client extends NetworkAdapter, EventHandlerContainer, ExceptionHandlerContainer, RegistryContainer {

    /**
     * Sends a packet to the server.
     *
     * @param packet The packet to be sent
     * @return The operation, which can be queued or executed directly
     */
    Operation send(Packet packet);

    /**
     * Performs a read of the next packet.
     * Will terminate after given timeout.
     *
     * @param timeoutInMillis timeout after which the process will be terminated
     * @return the packet received event
     */
    PacketReceiveEvent read(long timeoutInMillis);

    boolean ignoreFromCache(long packedId);
}
