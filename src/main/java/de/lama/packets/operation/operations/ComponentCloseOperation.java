package de.lama.packets.operation.operations;

import de.lama.packets.operation.AbstractThreadedOperation;
import de.lama.packets.operation.Operation;
import de.lama.packets.operation.ThreadedOperation;
import de.lama.packets.util.ExceptionHandler;

import java.util.Set;

public class ComponentCloseOperation extends AbstractThreadedOperation {

    private final Set<ThreadedOperation> operations;
    private final ExceptionHandler exceptionHandler;

    public ComponentCloseOperation(Set<ThreadedOperation> operations, ExceptionHandler exceptionHandler) {
        this.operations = operations;
        this.exceptionHandler = exceptionHandler;
    }

    @Override
    public Operation complete() {
        this.operations.forEach(operation -> this.exceptionHandler.operate(operation::stop, "Could not stop operation"));
        return this;
    }
}
