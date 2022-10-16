package de.lama.packets.client;

import de.lama.packets.Packet;
import de.lama.packets.PacketIOComponent;
import de.lama.packets.event.events.PacketReceiveEvent;
import de.lama.packets.event.EventHandlerContainer;
import de.lama.packets.operation.Operation;
import de.lama.packets.registry.RegistryContainer;

import java.net.InetAddress;

public interface Client extends PacketIOComponent, RegistryContainer, EventHandlerContainer {

    Operation send(Packet packet);

    PacketReceiveEvent awaitPacket(long timeoutInMillis);

    InetAddress getAddress();

}
