package de.lama.packets.event.events;

import de.lama.packets.Packet;
import de.lama.packets.client.Client;
import de.lama.packets.event.CancelEventBehaviour;
import de.lama.packets.event.Cancellable;
import de.lama.packets.event.EventBehaviour;
import de.lama.packets.event.PacketEvent;

public record PacketSendEvent(Client target, long packetId, Packet packet, EventBehaviour behaviour) implements PacketEvent, Cancellable {

    public PacketSendEvent(Client target, long packetId, Packet packet) {
        this(target, packetId, packet, new CancelEventBehaviour());
    }
}
