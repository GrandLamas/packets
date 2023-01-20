package de.lama.packets.util.exception;

import de.lama.packets.server.ServerException;

import java.util.function.Consumer;

public interface ExceptionHandler extends Consumer<Exception> {

    default <R> R operate(ThrowingSupplier<R> runnable, String failMessage) {
        try {
            return runnable.get();
        } catch (Exception e) {
            this.accept(new ServerException(failMessage, e));
        }
        return null;
    }

    default <L, R> R operate(ThrowingFunction<L, R> runnable, L type, String failMessage) {
        try {
            return runnable.apply(type);
        } catch (Exception e) {
            this.accept(new ServerException(failMessage, e));
        }
        return null;
    }

    default <R> boolean operate(ThrowingConsumer<R> runnable, R r, String failMessage) {
        try {
            runnable.accept(r);
            return true;
        } catch (Exception e) {
            this.accept(new ServerException(failMessage, e));
        }
        return false;
    }

    default boolean operate(ThrowingRunnable runnable, String failMessage) {
        try {
            runnable.run();
            return true;
        } catch (Exception e) {
            this.accept(new ServerException(failMessage, e));
        }
        return false;
    }

    default void acceptIf(boolean check, Exception exception) {
        if (check) this.accept(exception);
    }
}
