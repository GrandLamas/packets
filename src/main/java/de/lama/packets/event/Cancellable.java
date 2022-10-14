package de.lama.packets.event;

public interface Cancellable {

    boolean isCancelled();

    void setCancelled(boolean cancelled);

}
