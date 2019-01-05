package ca.warp7.action.impl;

import ca.warp7.action.IAction;

import java.util.ArrayList;
import java.util.List;


abstract class QueueBase extends Singleton {

    private List<IAction> mRuntimeQueue = new ArrayList<>();
    private IAction mCurrentAction = null;

    void addToQueue(IAction... actions) {
//        Arrays.stream(actions).forEach(action -> System.out.println("Adding Queue: " + action + " to " + QueueBase.this));
        List<IAction> actionQueue = new ArrayList<>();
        for (IAction action : actions) {
            if (action instanceof Delegate) {
                List<IAction> elementQueue = ((Delegate) action).getQueue();
                if (elementQueue == null) actionQueue.add(action);
                else {
//                    System.out.print(Thread.currentThread().getName() + " ");
//                    System.out.println("Merging From: " + action);
//                    elementQueue.forEach(e -> System.out.println("Merging: " + e + " into " + QueueBase.this));
                    actionQueue.addAll(elementQueue);
                }
            } else actionQueue.add(action);
        }
        actionQueue.forEach(action -> linkChild(this, action));
        mRuntimeQueue.addAll(actionQueue);
    }

    @Override
    public void start_() {
    }

    @Override
    public void update() {
        if (mCurrentAction == null) {
            if (mRuntimeQueue.isEmpty()) return;
            mCurrentAction = mRuntimeQueue.remove(0);
//            System.out.print(Thread.currentThread().getName() + " ");
//            System.out.println("Queue Start: " + mCurrentAction);
            mCurrentAction.start();
        }
        mCurrentAction.update();
        if (mCurrentAction.shouldFinish()) {
//            System.out.print(Thread.currentThread().getName() + " ");
//            System.out.println("Queue Done: " + mCurrentAction);
            mCurrentAction.stop();
            mCurrentAction = null;
        }
    }

    @Override
    public void stop() {
        if (mCurrentAction != null) mCurrentAction.stop();
    }

    @Override
    public boolean shouldFinish_() {
        return mRuntimeQueue.isEmpty() && mCurrentAction == null;
    }

    @Override
    public List<IAction> getQueue() {
        return mRuntimeQueue;
    }
}
