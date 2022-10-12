package de.lama.packets.client;

import de.lama.packets.server.PacketServer;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.Objects;

public class ConnectionBuilder {

    private String address;
    private int port;

    private Socket createSocket() {
//        return new ServerSocket(this.port, Objects.requireNonNull(this.address));
        return null;
    }

    public ConnectionBuilder localhost() {
        this.address = "localhost";
        return this;
    }

    public ConnectionBuilder address(String address) {
        this.address = address;
        return this;
    }

    public ConnectionBuilder port(int port) {
        this.port = port;
        return this;
    }

    public PacketServer build() {
//        return new PacketClientImpl(this.createSocket());
        return null;
    }
}
