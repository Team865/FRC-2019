package ca.warp7.action;

import java.util.Arrays;
import java.util.List;


/**
 * <p>
 * An {@link IAction} defines any self contained action that can be executed by the robot.
 * An Action is the unit of basis for autonomous programs. Actions may contain anything,
 * which means we can run sub-actions in various ways, in combination with the start,
 * update, end, and shouldFinish methods.
 * </p>
 *
 * <p>
 * An entire scheduling API is developed with this interface as the basis, adding various
 * scheduling functionality to the interface
 * </p>
 *
 * @author Team 865
 * @author Yu Liu
 * @version 3.16 (Revision 41 on 12/16/2018)
 * @apiNote {@link IAction} and its inner interfaces create an API framework for scheduling complex
 * action tasks in a variety of ways, especially useful for autonomous programming. See the
 * specific interfaces for documentation
 * @implNote {@link IAction} and its inner interfaces are implemented in the
 * <code>ca.warp7.action.impl</code> package
 * @see Mode
 * @see ITimer
 * @see Consumer
 * @see Predicate
 * @see SingletonResources
 * @see Delegate
 * @see AsyncStart
 * @see AsyncStop
 * @see Function
 * @see API
 * @see HeadClass
 * @since 1.0
 */
@SuppressWarnings({"WeakerAccess", "unused"})
@FunctionalInterface
public interface IAction {


    /**
     * A wrapper to create an action that should be used to define auto modes,
     * since a mode may be created for multiple times during runtime
     *
     * @since 1.0
     */
    @FunctionalInterface
    interface Mode {


        /**
         * Fetches the main action of the mode to be run
         *
         * @return the action
         */
        IAction getAction();
    }


    /**
     * An internal interface that keep track of time.
     * This makes the Action API independent of WPILib's timer api
     *
     * @since 2.0
     */
    @FunctionalInterface
    interface ITimer {


        /**
         * Gets the time since the Robot is started
         *
         * @return time in seconds
         * @since 2.0
         */
        double getTime();
    }


    /**
     * Default timer using the system
     */

    ITimer DefaultTimer = () -> System.nanoTime() / 1.0e09;


    /**
     * Represents an operation that accepts a {@link Delegate} and returns no
     * result. Unlike most other functional interfaces, {@code Consumer} is expected
     * to operate via side-effects.
     *
     * @since 2.0
     */
    @FunctionalInterface
    interface Consumer {


        /**
         * Accepts an action delegate performs an action with it
         *
         * @since 2.0
         */
        void accept(Delegate delegate);
    }


    /**
     * Represents a predicate (boolean-valued function) of a delegate
     *
     * @since 2.0
     */
    @FunctionalInterface
    interface Predicate {


        /**
         * Returns a decision based on the referred action
         *
         * @since 2.0
         */
        boolean test(Delegate delegate);
    }


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
     *
     * @since 1.0 (modified 3.10)
     */
    void start();


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
     *
     * @return boolean
     * @since 1.0
     */
    default boolean shouldFinish() {
        return true;
    }


    /**
     * <p>
     * Periodically updates the action
     * </p>
     *
     * @since 1.0 (modified 3.10)
     */
    default void update() {
    }


    /**
     * <p>
     * Run code once when the action finishes, usually for clean up
     * </p>
     *
     * @since 1.0 (modified 3.10)
     */
    default void stop() {
    }


    /**
     * <p>
     * Manages the resources of an action or an action tree, which includes
     * timers, variables, and broadcasts
     * </p>
     *
     * @since 2.0
     */
    interface SingletonResources {


        /**
         * Associates the specified value with the specified key in this map
         * (optional operation).  If the map previously contained a mapping for
         * the key, the old value is replaced by the specified value.
         *
         * @param name  name with which the specified value is to be associated
         * @param value value to be associated with the specified key
         * @since 2.0
         */
        void put(String name, Object value);


        /**
         * Returns the value to which the specified key is mapped, or
         * {@code defaultValue} if this map contains no mapping for the key.
         *
         * @param name         the key whose associated value is to be returned
         * @param defaultValue the default mapping of the key
         * @since 2.0
         */
        Object get(String name, Object defaultValue);


        /**
         * @since 2.0
         */
        int getBroadcastCount(String trigger);


        /**
         * @since 2.0
         */
        String broadcastName(String trigger);


        /**
         * @since 2.0
         */
        ITimer getActionTimer();


        /**
         * @since 2.0
         */
        double getTime();


        /**
         * @since 2.0
         */
        double getTotalElapsed();


        /**
         * @since 2.0
         */
        void startTimer();


        /**
         * @since 3.13
         */
        double getInterval();


        /**
         * Get the level of verbose logging
         *
         * @return the verbose level
         * @since 3.15
         */
        int getVerboseLevel();


        /**
         * @since 2.0
         */
        default void broadcast(String trigger) {
            String name = broadcastName(trigger);
            put(name, getInt(name, 0) + 1);
        }


        /**
         * @since 2.0
         */
        default double getDouble(String name, double defaultValue) {
            Object var = get(name, null);
            if (var instanceof Double) return (double) var;
            return defaultValue;
        }


        /**
         * @since 2.0
         */
        default int getInt(String name, int defaultValue) {
            Object var = get(name, null);
            if (var instanceof Integer) return (int) var;
            return defaultValue;
        }


        /**
         * @since 2.0
         */
        default String getString(String name, String defaultValue) {
            Object var = get(name, null);
            if (var instanceof String) return (String) var;
            return defaultValue;
        }
    }


    /**
     * <p>
     * A {@link} Delegate represents an actions running state, which includes tracking its time
     * and managing its resources
     * </p>
     *
     * @since 2.0
     */
    interface Delegate {


        /**
         * Gets the length of time (in seconds) since this action started
         *
         * @return the elapsed time
         * @since 2.0
         */
        double getElapsed();


        /**
         * Gets the thread level of the action tree
         *
         * @return the depth of threads
         * @since 2.0
         */
        int getDetachDepth();


        /**
         * Gets the parent of the action
         *
         * @return the parent delegate object
         * @since 2.0
         */
        Delegate getParent();


        /**
         * Sends a stop signal immediately, to be applied when the shouldFinish method is called
         *
         * @since 2.0
         */
        void interrupt();


        /**
         * Gets the resources object shared with the action, or create one if
         * none can currently be found
         *
         * @return the resources object associated with the delegate
         * @since 2.0
         */
        SingletonResources getResources();


        /**
         * Sets the name of the action
         *
         * @since 3.4
         */
        void setName(String name);


        /**
         * Gets the name of the action, if any has been set with setName
         *
         * @return name of the action
         * @since 3.4
         */
        String getName();


        /**
         * Gets a string that represents the action
         *
         * @return The string containing the name, class, and parent
         * @since 3.4 (Modified 3.13)
         */
        default String getActionSummary() {
            return String.format("Thread: %s |Name: %s |This: %s |Parent: %s",
                    Thread.currentThread().getName(), getName(), this, getParent());
        }


        /**
         * @since 3.6
         */
        default List<IAction> getQueue() {
            return null;
        }


        /**
         * @since 2.0
         */
        default boolean hasRemainingTime() {
            return false;
        }


        /**
         * @since 2.0
         */
        default double getRemainingTime() {
            return 0;
        }


        /**
         * @since 2.0
         */
        default boolean hasProgressState() {
            return false;
        }


        /**
         * @since 2.0
         */
        default double getPercentProgress() {
            return 0;
        }


        /**
         * @since 2.0
         */
        default double getNumericalProgress() {
            return 0;
        }


        /**
         * @since 2.0
         */
        default boolean hasParent() {
            return getParent() != null;
        }
    }


    /**
     * <p>
     * Specifies the start mode of an async action
     * </p>
     *
     * @since 3.12
     */
    enum AsyncStart {


        /**
         * <p>
         * Starts all actions when the async starts
         * </p>
         *
         * @since 3.12
         */
        OnStart,


        /**
         * <p>
         * Plans out the async actions so they finish at the same time,
         * and starts actions according to the plan
         * </p>
         *
         * @since 3.12
         */
        OnStaticInverse,


        /**
         * <p>
         * Dynamically adjust the starting time of actions so they finish at the
         * same time
         * </p>
         *
         * @since 3.12
         */
        OnDynamicInverse
    }


    /**
     * <p>
     * Specifies the stop mode of an async action
     * </p>
     *
     * @since 3.12
     */
    enum AsyncStop {


        /**
         * <p>
         * Each sub-action stops when they want to finish
         * The parent action stops when all actions are finished
         * </p>
         *
         * @since 3.12
         */
        OnEachFinished,


        /**
         * <p>
         * Each sub-action stops when the parent is finished
         * The parent action stops when any sub-action wants to finish
         * </p>
         *
         * @since 3.12
         */
        OnAnyFinished,


        /**
         * <p>
         * Each sub-action stops when the parent is finished
         * The parent action stops when all sub-actions are finished
         * </p>
         *
         * @since 3.12
         */
        OnAllFinished,


        /**
         * <p>
         * Each sub-action stops when the parent is finished
         * The parent action stops when a statically estimated time is met
         * </p>
         *
         * @since 3.12
         */
        OnStaticEstimate
    }


    /**
     * <p>
     * Provides a set of convenience creators for functional interfaces
     * that simplify the API
     * </p>
     *
     * @since 2.0
     */
    abstract class Function {


        /**
         * @since 2.0
         */
        protected static Predicate triggeredOnce(String name) {
            return d -> d.getResources().getBroadcastCount(name) == 1;
        }


        /**
         * @since 2.0
         */
        protected static Predicate triggeredRepeat(String name) {
            return d -> d.getResources().getBroadcastCount(name) > 1;
        }


        /**
         * @since 2.0
         */
        protected static Predicate triggeredSome(String name, int times) {
            return d -> d.getResources().getBroadcastCount(name) == times;
        }


        /**
         * @since 2.0
         */
        protected static Predicate elapsed(double timeInSeconds) {
            return d -> !d.hasParent() || d.getParent().getElapsed() > timeInSeconds;
        }


        /**
         * @since 2.0
         */
        protected static Consumer broadcastAll(String... triggers) {
            return d -> Arrays.stream(triggers).forEach(trigger -> d.getResources().broadcast(trigger));
        }


        /**
         * @since 2.0
         */
        protected static Predicate atProgress(double progress) {
            return d -> d.hasProgressState() && d.getNumericalProgress() > progress;
        }


        /**
         * @since 2.0
         */
        protected static Predicate atPercent(int percent) {
            int progress = Math.min(0, Math.max(100, percent));
            return d -> d.hasProgressState() && d.getPercentProgress() > progress;
        }


        /**
         * Consumes by interrupting the parent action of the executor of this consumer
         *
         * @since 3.7
         */
        protected static Consumer interrupt() {
            return d -> d.getParent().interrupt();
        }
    }


    /**
     * <p>
     * {@link API} defines the general syntax for expressing complex actions,
     * and defines the following properties:
     * </p>
     *
     * <ul>
     * <li><b>Chain-able:</b> All methods of the API object returns the API object itself</li>
     * <li><b>Hierarchical:</b> All API objects are actions themselves </li>
     * <li><b>List-based: </b> Most methods of the API accepts a vararg list of a actions as arguments</li>
     * </ul>
     *
     * @since 2.0
     */
    interface API extends IAction {


        /**
         * <p>
         * Get a "head" of this API as distinct from a chain
         * </p>
         *
         * @return the API copy object that currently doesn't have anything in it
         * @since 3.7
         */
        API head();


        /**
         * Runs an async action according to a start and stop mode with a list of actions
         *
         * @since 3.12
         */
        API asyncOp(AsyncStart startMode, AsyncStop stopMode, IAction... actions);


        /**
         * <p>
         * Waits the queue (do nothing) until a predicate is met
         * </p>
         *
         * @param predicate the predicate to test for
         * @return The API state after the method operation has been queued to the previous state
         * @since 2.0
         */
        API await(Predicate predicate);


        /**
         * <p>
         * Execute a function in reference to an action
         * </p>
         *
         * @param consumer the action delegate to consume
         * @return The API state after the method operation has been queued to the previous state
         * @since 2.0
         */
        API exec(Consumer consumer);


        /**
         * <p>
         * Iterate a function periodically in reference to an action
         * </p>
         *
         * @param consumer the action delegate to consume
         * @return The API state after the method operation has been queued to the previous state
         * @since 2.0
         */
        API iterate(Consumer consumer);


        /**
         * <p>
         * Run one of two actions depending on a condition
         * </p>
         *
         * @param predicate  the predicate to test for
         * @param ifAction   the action to run if the predicate is true
         * @param elseAction the action to run if the predicate is false
         * @return The API state after the method operation has been queued to the previous state
         * @since 2.0
         */
        API runIf(Predicate predicate, IAction ifAction, IAction elseAction);


        /**
         * <p>
         * Runs some actions in sequential order (i.e. the next action starts when the first
         * one finishes)
         * </p>
         *
         * @param actions A list of actions to run
         * @return The API state after the method operation has been queued to the previous state
         * @since 2.0
         */
        API queue(IAction... actions);


        /**
         * <p>
         * Starts a list of action in parallel, and finish when each of the actions
         * are finished and stops
         * </p>
         *
         * @param actions A list of actions to run
         * @return The API state after the method operation has been queued to the previous state
         * @since 2.0
         */
        default API async(IAction... actions) {
            return asyncOp(AsyncStart.OnStart, AsyncStop.OnEachFinished, actions);
        }


        /**
         * <p>
         * Starts a list of action in parallel, and finish when all of the actions
         * are finished and stops
         * </p>
         *
         * @param actions A list of actions to run
         * @return The API state after the method operation has been queued to the previous state
         * @since 2.0
         */
        default API asyncAll(IAction... actions) {
            return asyncOp(AsyncStart.OnStart, AsyncStop.OnAllFinished, actions);
        }


        /**
         * <p>
         * Starts a list of action in parallel, and finish when any of the actions
         * are finished and stops
         * </p>
         *
         * @param actions A list of actions to run
         * @return The API state after the method operation has been queued to the previous state
         * @since 2.0
         */
        default API asyncAny(IAction... actions) {
            return asyncOp(AsyncStart.OnStart, AsyncStop.OnAnyFinished, actions);
        }


        /**
         * <p>
         * Schedules a list of parallel actions according to the timing of a master.
         * This means slaves end when the master ends
         * </p>
         *
         * @param master the master action to sync to
         * @param slaves the slaves to run
         * @return The API state after the method operation has been queued to the previous state
         * @implNote Copies the array of slaves into a higher index and inserts interruptWhenDone
         * at the first index
         * @since 3.8
         */
        default API asyncMaster(IAction master, IAction... slaves) {
            return asyncAny(master, async(slaves));
        }


        /**
         * <p>
         * Starts a list of action in parallel, and finish when a condition has been met
         * </p>
         *
         * @param actions A list of actions to run
         * @return The API state after the method operation has been queued to the previous state
         * @since 2.0
         */
        default API asyncUntil(Predicate predicate, IAction... actions) {
            return asyncAny(await(predicate), async(actions));
        }


        /**
         * <p>
         * Broadcasts string triggers that can be received anywhere in the action tree
         * </p>
         *
         * @param triggers the triggers to broadcast
         * @return The API state after the method operation has been queued to the previous state
         * @since 2.0
         */
        default API broadcast(String... triggers) {
            return exec(Function.broadcastAll(triggers));
        }


        /**
         * <p>
         * Broadcasts string triggers that can be received anywhere in the action tree,
         * when a certain condition is met
         * </p>
         *
         * @param predicate the predicate to test for
         * @param triggers  the triggers to broadcast
         * @return The API state after the method operation has been queued to the previous state
         * @since 2.0
         */
        default API broadcastWhen(Predicate predicate, String... triggers) {
            return await(predicate).broadcast(triggers);
        }


        /**
         * <p>
         * Execute a function in reference to an action
         * </p>
         *
         * @param runnable the action runnable to consume
         * @return The API state after the method operation has been queued to the previous state
         * @since 3.15
         */
        default API execRunnable(Runnable runnable) {
            return exec(d -> runnable.run());
        }


        /**
         * <p>
         * Iterate a function periodically in reference to an action
         * </p>
         *
         * @param runnable the action runnable to consume
         * @return The API state after the method operation has been queued to the previous state
         * @since 3.15
         */
        default API iterateRunnable(Runnable runnable) {
            return iterate(d -> runnable.run());
        }


        /**
         * <p>
         * Interrupts the containing parent action
         * </p>
         *
         * @return The API state after the method operation has been queued to the previous state
         * @since 3.8
         */
        default API interruptParent() {
            return exec(Function.interrupt());
        }


        /**
         * <p>
         * Interrupts the parent action after a certain action
         * </p>
         *
         * @param action The action to follow
         * @return The API state after the method operation has been queued to the previous state
         * @since 3.9
         */
        default API interruptWhenDone(IAction action) {
            return queue(action).interruptParent();
        }


        /**
         * <p>
         * Runs an action only if the condition is true
         * </p>
         *
         * @param predicate the predicate to test for
         * @param ifAction  the action to run if the predicate is true
         * @return The API state after the method operation has been queued to the previous state
         * @since 2.0
         */
        default API onlyIf(Predicate predicate, IAction ifAction) {
            return runIf(predicate, ifAction, null);
        }


        /**
         * <p>
         * Wait (do nothing) for a specified number of seconds
         * </p>
         *
         * @return The API state after the method operation has been queued to the previous state
         * @since 2.0
         */
        default API waitFor(double seconds) {
            return await(Function.elapsed(seconds));
        }


        /**
         * <p>
         * Queues some action the moment a condition becomes true
         * </p>
         *
         * @param predicate the predicate to test for
         * @param actions   A list of actions to run when the condition is true
         * @return The API state after the method operation has been queued to the previous state
         * @since 2.0
         */
        default API when(Predicate predicate, IAction... actions) {
            return await(predicate).queue(actions);
        }
    }


    /**
     * <p>
     * Helper methods that allows creation of the API based on the API functions as a queue head.
     * This class does not implement the queue method to separate the implementation
     * </p>
     *
     * @since 2.0
     */
    abstract class HeadClass extends Function implements API {

        /**
         * {@inheritDoc}
         */
        @Override
        public void start() {
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public API asyncOp(AsyncStart startMode, AsyncStop stopMode, IAction... actions) {
            return head().asyncOp(startMode, stopMode, actions);
        }


        /**
         * {@inheritDoc}
         */
        @Override
        public API await(Predicate predicate) {
            return head().await(predicate);
        }


        /**
         * {@inheritDoc}
         */
        @Override
        public API exec(Consumer consumer) {
            return head().exec(consumer);
        }


        /**
         * {@inheritDoc}
         */
        @Override
        public API iterate(Consumer consumer) {
            return head().iterate(consumer);
        }


        /**
         * {@inheritDoc}
         */
        @Override
        public API queue(IAction... actions) {
            return head().queue(actions);
        }


        /**
         * {@inheritDoc}
         */
        @Override
        public API runIf(Predicate predicate, IAction ifAction, IAction elseAction) {
            return head().runIf(predicate, ifAction, elseAction);
        }
    }
}
