package ca.warp7.frc

/**
 * Returns true once if and only if the value of newValue changes from false to true.
 */

class LatchedBoolean {
    private var lastValue = false
    fun update(newValue: Boolean): Boolean {
        var returnValue = false
        if (newValue && !lastValue) {
            returnValue = true
        }
        lastValue = newValue
        return returnValue
    }
}