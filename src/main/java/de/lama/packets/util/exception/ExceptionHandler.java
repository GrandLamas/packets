/*
 * MIT License
 *
 * Copyright (c) 2023 Cuuky
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

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
