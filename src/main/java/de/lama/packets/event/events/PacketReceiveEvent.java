package de.lama.packets.event.events;

import de.lama.packets.Packet;
import de.lama.packets.event.PacketEvent;

public record PacketReceiveEvent(long packetId, Packet packet) implements PacketEvent {
}
