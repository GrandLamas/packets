package de.lama.packets.client;

import java.io.IOException;
import java.net.Socket;
import java.util.Objects;

public class ConnectionBuilder {

    private String address;
    private int port;

    private Socket createSocket() throws IOException {
        return new Socket(Objects.requireNonNull(this.address), this.port);
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

    public PacketClient build() throws IOException {
        return new GsonPacketClient(this.createSocket());
    }
}
