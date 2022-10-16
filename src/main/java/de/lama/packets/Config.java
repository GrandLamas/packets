package de.lama.packets;

import de.lama.packets.util.ExceptionHandler;

import java.util.logging.Logger;

// TODO: Logger
public record Config(Logger logger, ExceptionHandler exceptionHandler) {
}
