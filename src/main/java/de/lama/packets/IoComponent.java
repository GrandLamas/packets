package de.lama.packets;

import java.net.InetAddress;

public interface IoComponent {

    InetAddress getAddress();

    /**
     * Returns the port of the server.
     * @return the port of the server
     */
    int getPort();

}
