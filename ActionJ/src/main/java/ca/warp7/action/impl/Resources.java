package ca.warp7.action.impl;

import ca.warp7.action.IAction;

import java.util.HashMap;
import java.util.Map;

public class Resources implements IAction.SingletonResources {

    private Map<String, Object> mPool = new HashMap<>();
    private IAction.ITimer mTimer;
    private double mStartTime = 0;
    private double mInterval = 0;
    private int verboseLevel;

    @Override
    public void put(String name, Object value) {
        mPool.put(name, value);
    }

    @Override
    public Object get(String name, Object defaultValue) {
        return mPool.getOrDefault(name, defaultValue);
    }

    @Override
    public int getBroadcastCount(String trigger) {
        return getInt(broadcastName(trigger), 0);
    }

    @Override
    public String broadcastName(String trigger) {
        return "Broadcast/" + trigger;
    }

    @Override
    public IAction.ITimer getActionTimer() {
        return mTimer;
    }

    @Override
    public double getTime() {
        return mTimer != null ? mTimer.getTime() : 0;
    }

    @Override
    public double getTotalElapsed() {
        if (mStartTime == 0) return 0;
        return mTimer.getTime() - mStartTime;
    }

    @Override
    public void startTimer() {
        if (mStartTime == 0) mStartTime = mTimer.getTime();
    }

    @Override
    public double getInterval() {
        return mInterval;
    }

    @Override
    public int getVerboseLevel() {
        return verboseLevel;
    }

    void setActionTimer(IAction.ITimer timer) {
        mTimer = timer;
    }

    void setInterval(double interval) {
        mInterval = interval;
    }

    void setVerboseLevel(int verboseLevel) {
        this.verboseLevel = verboseLevel;
    }
}
