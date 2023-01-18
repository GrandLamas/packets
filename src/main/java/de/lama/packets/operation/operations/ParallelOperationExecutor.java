package de.lama.packets.operation.operations;

import de.lama.packets.operation.Operation;

import java.util.Arrays;
import java.util.Collection;

public record ParallelOperationExecutor(Collection<Operation> operations) implements Operation {

    public ParallelOperationExecutor(Operation... operations) {
        this(Arrays.asList(operations));
    }

    @Override
    public Operation queue() {
        this.operations.forEach(Operation::queue);
        return this;
    }

    @Override
    public Operation complete() {
        this.operations.forEach(Operation::complete);
        return this;
    }
}
