package de.lama.packets.client;

import de.lama.packets.Packet;
import de.lama.packets.event.EventHandlerContainer;
import de.lama.packets.operation.Operation;

import java.net.InetAddress;

public interface Client extends EventHandlerContainer {

    Operation send(Packet packet);

    Operation close();

    Packet awaitPacket(long timeoutInMillis);

    InetAddress getAddress();

    int getPort();

}
