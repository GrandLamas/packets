package de.lama.packets;

import de.lama.packets.event.PacketEvent;

public record PacketReceiveEvent(long packetId, Packet packet) implements PacketEvent {
}
