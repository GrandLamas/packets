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

package de.lama.packets.registry;

import de.lama.packets.Packet;

public interface PacketRegistry {

    long PACKET_NOT_FOUND = Long.MIN_VALUE;

    /**
     * Registers a packet.
     *
     * @param id The id
     * @param clazz The class
     * @param <T> The type of packet
     */
    <T extends Packet> boolean registerPacket(long id, Class<T> clazz);

    /**
     * Returns the class of the packet id
     *
     * @param id the id of the packet class
     * @return the class of the packet id, else null
     */
    Class<? extends Packet> parseClass(long id);

    /**
     * Returns the id of the packet class.
     *
     * @param clazz the class of the packet id
     * @return the id of the packet class, else PACKET_NOT_FOUND
     */
    long parseId(Class<? extends Packet> clazz);

}
