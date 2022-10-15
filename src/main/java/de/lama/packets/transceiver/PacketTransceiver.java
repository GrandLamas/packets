package de.lama.packets.transceiver;

public interface PacketTransceiver {

    default long calculateTickrate(int tickrate) {
        return 1000L / tickrate;
    }

    void start();

    void stop();

    boolean isRunning();

}
