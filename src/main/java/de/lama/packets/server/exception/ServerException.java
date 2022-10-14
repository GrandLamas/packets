package de.lama.packets.server.exception;

public class ServerException extends RuntimeException {

    public ServerException(String message, Exception cause) {
        super(message, cause);
    }

    public ServerException(String message) {
        super(message);
    }
}
