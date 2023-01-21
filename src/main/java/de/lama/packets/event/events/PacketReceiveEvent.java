package de.lama.packets.event.events;

import de.lama.packets.Packet;
import de.lama.packets.client.Client;
import de.lama.packets.event.PacketEvent;

public record PacketReceiveEvent(Client source, long packetId, Packet packet) implements PacketEvent {
}
