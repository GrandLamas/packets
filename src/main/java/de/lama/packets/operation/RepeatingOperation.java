package de.lama.packets.operation;

public interface RepeatingOperation extends Operation {

    void stop();

    boolean isRunning();

}
