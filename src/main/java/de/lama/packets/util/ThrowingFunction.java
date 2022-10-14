package de.lama.packets.util;

public interface ThrowingFunction<T, R> {

    R apply(T t) throws Exception;

}
