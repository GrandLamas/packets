package de.lama.packets.client;

import de.lama.packets.Packet;

import java.io.IOException;
import java.net.Socket;
import java.util.Objects;

public class SocketBuilder {

    static final String LOCALHOST = "localhost";
    static final int PORT = Packet.PORT;

    private String address;
    private int port;

    public SocketBuilder() {
        this.port = PORT;
    }

    public SocketBuilder localhost() {
        return this.address(LOCALHOST);
    }

    public SocketBuilder address(String address) {
        this.address = address;
        return this;
    }

    public SocketBuilder port(int port) {
        this.port = port;
        return this;
    }

    public Socket build() throws IOException {
        return new Socket(Objects.requireNonNull(this.address), this.port);
    }

    public String address() {
        return this.address;
    }

    public int port() {
        return this.port;
    }
}
