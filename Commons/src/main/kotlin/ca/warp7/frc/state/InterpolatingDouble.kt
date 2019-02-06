package ca.warp7.frc.state

/**
 * A Double that can be interpolated using the InterpolatingTreeMap.
 *
 * @see InterpolatingTreeMap
 */
data class InterpolatingDouble(private val value: Double) :
        Interpolator<InterpolatingDouble>,
        InverseInterpolator<InterpolatingDouble>, Comparable<InterpolatingDouble> {

    override fun interpolate(other: InterpolatingDouble, x: Double): InterpolatingDouble {
        val dyDx = other.value - value
        val searchY = dyDx * x + value
        return InterpolatingDouble(searchY)
    }

    override fun inverseInterpolate(upper: InterpolatingDouble, query: InterpolatingDouble): Double {
        val upperToLower = upper.value - value
        if (upperToLower <= 0) return 0.0
        val queryToLower = query.value - value
        return if (queryToLower <= 0) 0.0 else queryToLower / upperToLower
    }

    override fun compareTo(other: InterpolatingDouble): Int {
        return when {
            other.value < value -> 1
            other.value > value -> -1
            else -> 0
        }
    }
}