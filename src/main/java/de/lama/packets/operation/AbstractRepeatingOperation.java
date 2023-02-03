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

package de.lama.packets.operation;

import java.util.concurrent.Future;

public abstract class AbstractRepeatingOperation implements RepeatingOperation {

    private Future<?> task;

    protected abstract Future<?> createRepeatingTask();

    @Override
    public void start() {
        if (this.isRunning()) throw new IllegalStateException("Already running");
        this.task = this.createRepeatingTask();
    }

    @Override
    public void stop() {
        if (!this.isRunning()) throw new IllegalStateException("Operation not running");
        this.task.cancel(true);
        this.task = null;
    }

    @Override
    public boolean isRunning() {
        return this.task != null && !this.task.isCancelled();
    }

    @Override
    public int hashCode() {
        return this.task.hashCode();
    }
}
