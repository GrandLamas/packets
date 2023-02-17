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

package de.lama.packets.util;

import de.lama.packets.util.exception.ThrowingRunnable;
import de.lama.packets.util.exception.ThrowingSupplier;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

public class CompletableFutureUtil {

    public static <T> CompletableFuture<T> supplyAsync(ThrowingRunnable runnable, T value) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                runnable.run();
                return value;
            } catch (Exception e) {
                throw new CompletionException(e);
            }
        });
    }

    public static <T> CompletableFuture<T> supplyAsync(ThrowingSupplier<T> runnable) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return runnable.get();
            } catch (Exception e) {
                throw new CompletionException(e);
            }
        });
    }
}