package de.lama.packets.operation.operations;

import de.lama.packets.operation.AbstractRepeatingOperation;
import de.lama.packets.operation.Operation;
import de.lama.packets.operation.RepeatingOperation;
import de.lama.packets.util.ExceptionHandler;

import java.util.Set;

public class ComponentCloseOperation extends AbstractRepeatingOperation {

    private final Set<RepeatingOperation> operations;
    private final ExceptionHandler exceptionHandler;

    public ComponentCloseOperation(Set<RepeatingOperation> operations, ExceptionHandler exceptionHandler) {
        this.operations = operations;
        this.exceptionHandler = exceptionHandler;
    }

    @Override
    public Operation complete() {
        this.operations.forEach(operation -> this.exceptionHandler.operate(operation::stop, "Could not stop operation"));
        return this;
    }
}
