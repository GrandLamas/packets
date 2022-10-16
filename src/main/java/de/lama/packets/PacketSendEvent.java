package de.lama.packets;

import de.lama.packets.event.CancelContainer;
import de.lama.packets.event.Cancellable;
import de.lama.packets.event.PacketEvent;

public record PacketSendEvent(long packetId, Packet packet, CancelContainer cancelContainer) implements PacketEvent, Cancellable {

    public PacketSendEvent(long packetId, Packet packet) {
        this(packetId, packet, new CancelContainer());
    }

    @Override
    public boolean isCancelled() {
        return this.cancelContainer.isCancelled();
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelContainer.setCancelled(cancelled);
    }
}
