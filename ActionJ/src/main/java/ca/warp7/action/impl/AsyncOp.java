package ca.warp7.action.impl;

import ca.warp7.action.IAction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AsyncOp extends Singleton {
    private final List<State> mStates;
    private final AsyncStart mStart;
    private final AsyncStop mStop;
    private double mStaticEstimate;
    private double mInterval;

    AsyncOp(AsyncStart startMode, AsyncStop stopMode, IAction... actions) {
        mStart = startMode;
        mStop = stopMode;
        mStates = new ArrayList<>();
        for (IAction action : actions) {
            mStates.add(new State(action));
            linkChild(this, action);
        }
    }

    @Override
    public List<IAction> getQueue() {
        return mStates.size() == 1 ? Collections.singletonList(mStates.get(0).realAction) : null;
    }

    @Override
    public void start_() {
        if (mStart == AsyncStart.OnStaticInverse || mStop == AsyncStop.OnStaticEstimate) {
            for (State s : mStates) {
                s.updateRemaining();
                s.updateStaticRemaining();
                if (s.staticRemaining > mStaticEstimate) mStaticEstimate = s.staticRemaining;
            }
        }
        if (mStart == AsyncStart.OnStart) for (State s : mStates) s.start();
        mInterval = getResources().getInterval();
    }

    @Override
    public void update() {
        switch (mStart) {
            case OnStaticInverse:
                double elapsed = getElapsed();
                double staticRemaining = mStaticEstimate - elapsed - mInterval;
                for (State s : mStates) if (s.staticRemaining > staticRemaining) s.start();
                break;
            case OnDynamicInverse:
                mStates.forEach(State::updateRemaining);
                double dynamicEstimate = 0;
                for (State s : mStates) if (s.remaining > dynamicEstimate) dynamicEstimate = s.remaining;
                double dynamicRemaining = dynamicEstimate - mInterval;
                for (State s : mStates) if (s.remaining > dynamicRemaining) s.start();
                break;
        }
        mStates.forEach(State::update);
    }

    @Override
    public boolean shouldFinish_() {
        switch (mStop) {
            case OnAnyFinished:
                for (State s : mStates) if (s.shouldFinish()) return true;
                return false;
            case OnEachFinished:
                for (State s : mStates) {
                    if (!s.shouldFinish()) return false;
                    else s.stop();
                }
                return true;
            case OnAllFinished:
                for (State state : mStates) if (!state.shouldFinish()) return false;
                return true;
            case OnStaticEstimate:
                return getElapsed() > mStaticEstimate;
        }
        return true;
    }

    @Override
    public void stop() {
        for (State mState : mStates) mState.stop();
    }

    static class State {
        private Delegate delegate;
        private boolean isRunning;
        private boolean hasStarted;
        private boolean hasRemaining;
        private boolean shouldUpdate;

        IAction realAction;
        double remaining;
        double staticRemaining;

        State(IAction action) {
            realAction = action;
            delegate = action instanceof Delegate ? (Delegate) action : null;
            isRunning = false;
            hasStarted = false;
            remaining = 0;
            staticRemaining = 0;
            hasRemaining = delegate != null && delegate.hasRemainingTime();
        }

        void updateRemaining() {
            if (hasRemaining && shouldUpdate) remaining = delegate.getRemainingTime();
            shouldUpdate = false;
        }

        void updateStaticRemaining() {
            staticRemaining = remaining;
        }

        public void start() {
            if (!hasStarted) {
                isRunning = true;
                shouldUpdate = true;
                realAction.start();
                hasStarted = true;
            }
        }

        public boolean shouldFinish() {
            return isRunning && realAction.shouldFinish();
        }

        public void update() {
            if (isRunning) {
                realAction.update();
                shouldUpdate = true;
            }
        }

        public void stop() {
            if (isRunning) {
                realAction.stop();
                isRunning = false;
            }
        }
    }
}
