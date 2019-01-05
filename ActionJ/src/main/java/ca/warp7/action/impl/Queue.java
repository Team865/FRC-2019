package ca.warp7.action.impl;

import ca.warp7.action.IAction;

public class Queue extends QueueBase implements IAction.API {

    @Override
    public API head() {
        return new Queue();
    }

    @Override
    public API asyncOp(AsyncStart startMode, AsyncStop stopMode, IAction... actions) {
        addToQueue(new AsyncOp(startMode, stopMode, actions));
        return this;
    }

    @Override
    public API await(Predicate predicate) {
        addToQueue(new Await(predicate));
        return this;
    }

    @Override
    public API exec(Consumer consumer) {
        addToQueue(new Execution(consumer));
        return this;
    }

    @Override
    public API iterate(Consumer consumer) {
        addToQueue(new Iteration(consumer));
        return this;
    }

    @Override
    public API queue(IAction... actions) {
        addToQueue(actions);
        return this;
    }

    @Override
    public API runIf(Predicate predicate, IAction ifAction, IAction elseAction) {
        addToQueue(new Condition(predicate, ifAction, elseAction));
        return this;
    }
}
