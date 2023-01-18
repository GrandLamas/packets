package de.lama.packets.util.exception;

public interface ThrowingFunction<T, R> {

    R apply(T t) throws Exception;

}
