package de.lama.packets.operation;

public interface ThreadedOperation extends Operation {

    void stop();

    boolean isRunning();

}
