package de.lama.packets.server.client;

import de.lama.packets.Packet;
import de.lama.packets.event.EventHandlerContainer;
import de.lama.packets.operation.Operation;
import de.lama.packets.server.ServerContainer;
import de.lama.packets.server.client.event.ServerClientEvent;

import java.net.InetAddress;

public interface ServerClient extends ServerContainer, PacketTransmitter, EventHandlerContainer<ServerClientEvent> {

    Operation send(Packet packet);

    Operation close();

    Packet awaitPacket(long timeoutInMs);

    InetAddress getAddress();

    int getPort();

}
