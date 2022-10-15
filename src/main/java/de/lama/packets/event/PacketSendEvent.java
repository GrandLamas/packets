package de.lama.packets.event;

import de.lama.packets.Packet;

public record PacketSendEvent(Packet packet, CancelContainer cancelContainer) implements PacketEvent, Cancellable {

    public PacketSendEvent(Packet packet) {
        this(packet, new CancelContainer());
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
