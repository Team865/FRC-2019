package ca.warp7.actionj.impl;

import ca.warp7.actionj.IAction;

public abstract class Singleton implements IAction, IAction.Delegate {

    private Delegate mParent;
    private SingletonResources mResources;
    private double mStartTime;
    private boolean mIsInterrupted;
    private int mDetachDepth;
    private String mName = "";

    static void linkChild(Delegate parent, IAction action) {
        if (action instanceof Singleton) safeLinkChild(parent, (Singleton) action);
    }

    static void safeLinkChild(Delegate parent, Singleton singleton) {
        singleton.mParent = parent;
    }

    static void incrementDetachDepth(Singleton action) {
        action.mDetachDepth++;
    }

    @Override
    public final void start() {
        start_();
        if (getResources().getVerboseLevel() > 1) {
            System.out.print(Thread.currentThread().getName() + " ");
            System.out.println("Started: " + this);
        }
        getResources().startTimer();
        mStartTime = mResources.getTime();
    }

    @Override
    public final boolean shouldFinish() {
        return mIsInterrupted || shouldFinish_();
    }

    @Override
    public final double getElapsed() {
        return getResources().getTime() - mStartTime;
    }

    @Override
    public Delegate getParent() {
        return mParent;
    }

    @Override
    public final void interrupt() {
        mIsInterrupted = true;
    }

    @Override
    public final int getDetachDepth() {
        return mDetachDepth;
    }

    @Override
    public final SingletonResources getResources() {
        if (mResources != null) return mResources;
        mResources = hasParent() ? getParent().getResources() : new Resources();
        mResources = mResources != null ? mResources : new Resources();
        return mResources;
    }

    @Override
    public final void setName(String name) {
        mName = name;
    }

    @Override
    public final String getName() {
        if (!mName.isEmpty()) return mName;
        return getClass().getSimpleName();
    }

    public abstract void start_();

    public abstract boolean shouldFinish_();
}
