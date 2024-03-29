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

import de.lama.packets.NetworkAdapter;
import de.lama.packets.Packet;
import de.lama.packets.client.Client;
import de.lama.packets.operation.Operation;

import java.util.stream.Stream;

public interface Server extends NetworkAdapter {

    /**
     * Sends the given packet to every connected client.
     *
     * @param packet the packet to be sent
     * @return the operation of sending
     */
    Operation broadcast(Packet packet);

    /**
     * Returns every connected client.
     *
     * @return the stream of connected clients
     */
    Stream<Client> getClients();

}
