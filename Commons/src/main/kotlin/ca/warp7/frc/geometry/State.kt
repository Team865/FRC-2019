package ca.warp7.frc.geometry

/**
 * Defines a parametric transformation state
 */
interface State<T : State<T>> : StateView<T, T> {

    /**
     * Get the inverse transformation of this state
     */
    operator fun unaryMinus(): T

    /**
     * Get the inverse transformation of this state
     */
    val inverse: T

    /**
     * Create a copy of this state
     */
    operator fun unaryPlus(): T

    /**
     * Create a copy of this state
     */
    val copy: T

    /**
     * Checks if the state is its identity
     */
    val isIdentity: Boolean

    /**
     * Check if another state is close to the current state
     */
    fun epsilonEquals(state: T, epsilon: Double): Boolean

    /**
     * Check if another state is equal to the current state
     */
    fun epsilonEquals(state: T): Boolean

    /**
     * Transform this state by another state
     */
    fun transform(by: T): T

    /**
     * Get the result transformation of this and another state
     */
    operator fun plus(by: T): T

    /**
     * Get the transformation applied to another state to get this state
     */
    operator fun minus(by: T): T

    /**
     * Scale the transformation by a scalar value
     */
    fun scaled(by: Double): T

    /**
     * Scale the transformation by a scalar value
     */
    operator fun times(by: Double): T

    /**
     * Scale the transformation by a reciprocal scalar value
     */
    operator fun div(by: Double): T

    /**
     * Get the distance to another state
     */
    fun distanceTo(state: T): Double
}