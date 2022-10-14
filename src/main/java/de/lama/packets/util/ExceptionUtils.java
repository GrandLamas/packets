package de.lama.packets.util;

import de.lama.packets.server.exception.ServerException;

public final class ExceptionUtils {

    private ExceptionUtils() {}

    public static <R> R operate(ExceptionHandler handler, ThrowingSupplier<R> runnable, String failMessage) {
        try {
            return runnable.get();
        } catch (Exception e) {
            handler.accept(new ServerException(failMessage, e));
        }
        return null;
    }

    public static <L, R> R operate(ExceptionHandler handler, ThrowingFunction<L, R> runnable, L type, String failMessage) {
        try {
            return runnable.apply(type);
        } catch (Exception e) {
            handler.accept(new ServerException(failMessage, e));
        }
        return null;
    }

    public static <R> boolean operate(ExceptionHandler handler, ThrowingConsumer<R> runnable, R r, String failMessage) {
        try {
            runnable.accept(r);
            return true;
        } catch (Exception e) {
            handler.accept(new ServerException(failMessage, e));
        }
        return false;
    }

    public static boolean operate(ExceptionHandler handler, ThrowingRunnable runnable, String failMessage) {
        try {
            runnable.run();
            return true;
        } catch (Exception e) {
            handler.accept(new ServerException(failMessage, e));
        }
        return false;
    }

}
