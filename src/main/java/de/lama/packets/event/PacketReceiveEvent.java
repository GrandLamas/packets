package de.lama.packets.event;

import de.lama.packets.Packet;

public record PacketReceiveEvent(Packet packet) implements PacketEvent {
}
