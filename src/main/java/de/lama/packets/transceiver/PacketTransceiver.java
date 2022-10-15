package de.lama.packets.transceiver;

public interface PacketTransceiver {

    char PACKET_DATA_TYPE = 's';

    void start();

    void stop();

    boolean isRunning();

    long getTickrate();

}
