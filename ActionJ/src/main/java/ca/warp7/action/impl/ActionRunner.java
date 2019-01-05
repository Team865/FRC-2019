package ca.warp7.action.impl;

import ca.warp7.action.IAction;

import java.util.Objects;

class ActionRunner extends Singleton {

    /**
     * The main action to run
     */
    private final IAction mAction;

    /**
     * Sets the interval of the loop
     */
    private final long mInterval;

    /**
     * Sets an explicit timeout to the auto phase for safety and testing
     */
    private final double mTimeout;

    /**
     * Sets whether to print out info during the run
     */
    private final int mVerboseLevel;

    /**
     * The timer used on this runner
     */
    private ITimer mTimer;

    /**
     * The thread that autos are run on. If this is null, then no autos are or should be running
     */
    private Thread mRunThread;

    ActionRunner(ITimer timer, double interval, double timeout, IAction action, boolean verbose) {
        Objects.requireNonNull(action);
        mTimer = timer;
        mAction = action;
        mInterval = (long) (interval * 1000);
        mTimeout = timeout;
        mVerboseLevel = verbose ? 1 : 0;
    }

    @Override
    public Delegate getParent() {
        // Return null so the resources are not shared in the hierarchy of actions
        return null;
    }

    /**
     * Starts the periodic Runnable for the auto program
     *
     * <p>The mechanism in which actions are running means that there cannot be blocking operations in
     * both {@link IAction#update()} and {@link IAction#stop()} or auto may not end on time</p>
     *
     * <p>The proper code mechanism should use implement {@link IAction}for a monitoring/locking purpose,
     * and the actual IO loops should be run instead in the IO looper. This would also make the actual
     * periodic delay not very relevant</p>
     */
    @Override
    public void start_() {
        // Make sure autos are not running right now before continuing
        if (mRunThread != null) {
            System.err.println("ERROR an Action is already running!!!");
            return;
        }
        // Check if a timer has already been assigned
        if (mTimer == null) mTimer = getResources().getActionTimer();
        // Get the cast resources of current action
        final Resources resources = (Resources) getResources();
        // Pass the timer to the resources
        resources.setActionTimer(mTimer);
        // Convert interval into seconds and pass to resources
        resources.setInterval(mInterval / 1000.0);
        // Set the compatible verbose level
        resources.setVerboseLevel(mVerboseLevel);
        // Use a variable to better name the thread
        final String actionName;
        // Operate on the action if it extends Singleton
        if (mAction instanceof Singleton) {
            final Singleton singleton = (Singleton) mAction;
            // Link the runner to the action
            safeLinkChild(this, singleton);
            // Increment the detachment state of the child
            incrementDetachDepth(singleton);
            // Fetch and store the resources pointer from the parent
            singleton.getResources();
            // Get the action name
            actionName = singleton.getName();
        }
        // Give the action its class name if it does not exist
        else actionName = mAction.getClass().getSimpleName();
        // Create the thread name based on the action name
        final String threadName = String.format("Action[%d:%s]", getDetachDepth() + 1, actionName);
        // Create a new run thread
        mRunThread = new Thread(() -> {
            if (mVerboseLevel > 0) System.out.printf("%s starting\n", threadName);
            // measure the start time and start the action
            final double startTime = mTimer.getTime();
            double time = startTime;
            mAction.start();
            // Count the loops
            int loopCount = 0;
            // Loop forever until an exit condition is met
            // Stop priority #1: Check if the stop method has been called to terminate this thread
            while (!Thread.currentThread().isInterrupted()) {
                time = mTimer.getTime() - startTime;
                // Stop priority #2: Check for explicit timeouts used in setAutoMode
                // Stop priority #3: Check if the action should finish
                // Note the main action may have recursive actions under it and all of those actions
                // should contribute to this check
                if (time >= mTimeout || mAction.shouldFinish()) break;
                // Update the action now after no exit conditions are met
                mAction.update();
                loopCount++;
                try {
                    // Delay for a certain amount of time so the update function is not called so often
                    Thread.sleep(mInterval);
                } catch (InterruptedException e) {
                    // Breaks out the loop instead of returning so that stop can be called
                    break;
                }
            }
            mAction.stop();
            // Print out info about the execution if verbose
            if (mVerboseLevel > 0) {
                System.out.printf("%s time relative to expected:  %.3fs\n", threadName, loopCount * mInterval - time);
                if (time < mTimeout) System.out.printf("%s ended early by %.3fs\n", threadName, mTimeout - time);
                else System.out.printf("%s ending after %.3fs\n", threadName, time);
            }
            // Assign null to the thread so this runner can be called again
            // without robot code restarting
            mRunThread = null;
        });
        // Start the thread
        mRunThread.setName(threadName);
        mRunThread.start();
    }

    @Override
    public boolean shouldFinish_() {
        return mRunThread == null;
    }

    @Override
    public void stop() {
        if (mRunThread != null) mRunThread.interrupt();
    }
}
