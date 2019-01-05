package ca.warp7.frc;

import ca.warp7.action.IAction;

public abstract class Subsystem {

    protected Subsystem() {
        RobotRuntime.RT.registerSubsystem(this);
    }

    /**
     * <p>Called when the robot is disabled</p>
     *
     * <p>This method should reset everything having to do with output so as to put
     * the subsystem in a disabled state</p>
     */
    public abstract void onDisabled();


    public void onIdle() {
        onDisabled();
    }

    /**
     * <p>Called periodically for the subsystem to send outputs to its output device.
     * This method is called from the State Change Looper.</p>
     *
     * <p>This method is guaranteed to not be called when the robot is disabled.
     * Any output limits should be applied here for safety reasons.</p>
     */
    public abstract void onOutput();

    private IAction mState;

    synchronized void updateState() {
        if (mState == null) onIdle();
        else if (mState.shouldFinish()) {
            mState.stop();
            mState = null;
        } else mState.update();
    }

    synchronized public void setState(IAction state) {
        if (mState == state) return;
        if (mState != null) mState.stop();
        if (state != null) {
            mState = state;
            mState.start();
        }
    }

    synchronized public IAction getState() {
        return mState;
    }

    synchronized public void setIdle() {
        mState = null;
    }
}
