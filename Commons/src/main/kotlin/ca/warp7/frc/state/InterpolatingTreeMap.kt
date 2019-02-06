package ca.warp7.frc.state

import java.util.*

/**
 * Interpolating Tree Maps are used to get values at points that are not defined by making a guess from points that are
 * defined. This uses linear interpolation.
 *
 * @param <K> The type of the key (must implement InverseInterpolator)
 * @param <V> The type of the value (must implement Interpolator)
</V></K> */
@Suppress("unused")
class InterpolatingTreeMap<K, V>(private val maxElements: Int) : TreeMap<K, V>()
        where K : InverseInterpolator<K>, V : Interpolator<V> {

    /**
     * Inserts a key value pair, and trims the tree if a max size is specified
     *
     * @param key   Key for inserted data
     * @param value Value for inserted data
     * @return the value
     */
    override fun put(key: K, value: V): V? {
        if (maxElements in 1..size) {
            // "Prune" the tree if it is oversize
            val first = firstKey()
            remove(first)
        }
        super.put(key, value)
        return value
    }

    override fun putAll(from: Map<out K, V>) {
        from.forEach { k, v -> put(k, v) }
    }

    /**
     * @param key Lookup for a value (does not have to exist)
     * @return V or null; V if it is Interpolator or exists, null if it is at a bound and cannot average
     */
    fun getInterpolated(key: K): V? {
        val result = get(key)
        if (result == null) {
            /* Get surrounding keys for interpolation */
            val topBound = ceilingKey(key)
            val bottomBound = floorKey(key)

            /*
             * If attempting interpolation at ends of tree, return the nearest data point
             */
            if (topBound == null && bottomBound == null) {
                return null
            } else if (topBound == null) {
                return get(bottomBound)
            } else if (bottomBound == null) {
                return get(topBound)
            }

            /* Get surrounding values for interpolation */
            val topElem = get(topBound)
            val bottomElem = get(bottomBound)
            return bottomElem!!.interpolate(topElem!!, bottomBound.inverseInterpolate(topBound, key))
        } else {
            return result
        }
    }
}