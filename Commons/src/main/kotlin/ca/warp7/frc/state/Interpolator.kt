package ca.warp7.frc.state

/**
 * Interpolator is an interface used by an Interpolating Tree as the Value type. Given two end points and an
 * interpolation parameter on [0, 1], it calculates a new Interpolator representing the interpolated value.
 *
 * @param <T> The Type of Interpolator
 * @see InterpolatingTreeMap
</T> */
interface Interpolator<T> {
    /**
     * Interpolates between this value and an other value according to a given parameter. If x is 0, the method should
     * return this value. If x is 1, the method should return the other value. If 0 < x < 1, the return value should be
     * interpolated proportionally between the two.
     *
     * @param other The value of the upper bound
     * @param x     The requested value. Should be between 0 and 1.
     * @return Interpolator<T> The estimated average between the surrounding data
    </T> */
    fun interpolate(other: T, x: Double): T
}