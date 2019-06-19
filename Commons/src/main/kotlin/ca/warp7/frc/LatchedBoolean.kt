package ca.warp7.frc

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