package de.lama.packets.operation.operations;

import de.lama.packets.operation.AbstractThreadedOperation;
import de.lama.packets.operation.Operation;
import de.lama.packets.operation.ThreadedOperation;
import de.lama.packets.util.ExceptionHandler;

import java.io.Closeable;
import java.util.Set;

public class SocketCloseOperation extends AbstractThreadedOperation {

    private final Closeable closeable;
    private final ExceptionHandler exceptionHandler;
    private final ComponentCloseOperation operation;

    public SocketCloseOperation(Closeable closeable, Set<ThreadedOperation> operations, ExceptionHandler exceptionHandler) {
        this.closeable = closeable;
        this.exceptionHandler = exceptionHandler;
        this.operation = new ComponentCloseOperation(operations, exceptionHandler);
    }

    @Override
    public Operation complete() {
        this.operation.complete();
        this.exceptionHandler.operate(this.closeable::close, "Could not close");
        return this;
    }
}
