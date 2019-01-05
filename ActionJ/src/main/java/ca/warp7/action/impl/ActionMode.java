package ca.warp7.action.impl;

import ca.warp7.action.IAction;

@SuppressWarnings("unused")
public abstract class ActionMode extends IAction.HeadClass implements IAction.Mode {

    public static IAction createRunner(ITimer timer, double interval, double timeout, IAction action, boolean verbose) {
        return new ActionRunner(timer, interval, timeout, action, verbose);
    }

    @Override
    public API head() {
        return new Queue();
    }
}
