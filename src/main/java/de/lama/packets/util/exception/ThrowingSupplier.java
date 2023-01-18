package de.lama.packets.util.exception;

public interface ThrowingSupplier<T> {

    T get() throws Exception;
}
