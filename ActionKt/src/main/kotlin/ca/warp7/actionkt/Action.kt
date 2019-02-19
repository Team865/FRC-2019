package ca.warp7.actionkt

/**
 * An Action defines any self contained action that can be executed by the robot.
 * An Action is the unit of basis for autonomous programs. Actions may contain anything,
 * which means we can run sub-actions in various ways, in combination with the start,
 * update, end, and shouldFinish methods.

 * An entire scheduling API is developed with this interface as the basis, adding various
 * scheduling functionality to the interface
 */
interface Action {

    /**
     * Run code once when the action is started, usually for set up.
     * This method must be called first before shouldFinish is called.
     *
     * This method is the only non-default one in the {@link IAction}
     * interface, making it a functional interface that can be used to
     * create singleton actions
     */
    fun start() = Unit

    /**
     * Returns whether or not the code has finished execution.
     *
     * <b>IMPORTANT:</b> We must make sure the changes in start
     * actually get applied to subsystems because updateState
     * will not run on the first call of this method after start
     */
    val shouldFinish: Boolean get() = true

    /**
     * Periodically updates the action
     */

    fun update() = Unit

    /**
     * Run code once when the action finishes, usually for clean up
     */
    fun stop() = Unit
}