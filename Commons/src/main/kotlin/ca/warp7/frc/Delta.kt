package ca.warp7.frc

class Delta {
    var value = 0.0

    fun update(newValue: Double): Double {
        val oldValue = value
        value = newValue
        return oldValue - newValue
    }
}