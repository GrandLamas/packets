package de.lama.packets.util;

public interface ThrowingSupplier<T> {

    T get() throws Exception;
}
