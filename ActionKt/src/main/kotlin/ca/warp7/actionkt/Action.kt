package ca.warp7.actionkt

/**
 * <p>
 * An Action defines any self contained action that can be executed by the robot.
 * An Action is the unit of basis for autonomous programs. Actions may contain anything,
 * which means we can run sub-actions in various ways, in combination with the start,
 * update, end, and shouldFinish methods.
 * </p>
 *
 * <p>
 * An entire scheduling API is developed with this interface as the basis, adding various
 * scheduling functionality to the interface
 * </p>
 */
interface Action {

    /**
     * <p>
     * Run code once when the action is started, usually for set up.
     * This method must be called first before shouldFinish is called.
     * </p>
     *
     * <p>
     * This method is the only non-default one in the {@link IAction}
     * interface, making it a functional interface that can be used to
     * create singleton actions
     * </p>
     */
    fun start() = Unit

    /**
     * <p>
     * Returns whether or not the code has finished execution.
     * </p>
     *
     * <p>
     * <b>IMPORTANT:</b> We must make sure the changes in start
     * actually get applied to subsystems because updateState
     * will not run on the first call of this method after start
     * </p>
     */
    val shouldFinish: Boolean get() = true

    /**
     * <p>
     * Periodically updates the action
     * </p>
     */

    fun update() = Unit

    /**
     * <p>
     * Run code once when the action finishes, usually for clean up
     * </p>
     *
     * @since 1.0 (modified 3.10)
     */
    fun stop() = Unit
}