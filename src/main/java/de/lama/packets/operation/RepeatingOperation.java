package de.lama.packets.operation;

public interface RepeatingOperation extends Operation {

    void start();

    void stop();

    boolean isRunning();

}
