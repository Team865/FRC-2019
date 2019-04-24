package ca.warp7.frc

class CycleCounter(private val cycles: Int = 1) {
    private var count: Int = cycles

    fun get(): Boolean {
        count++
        if (count >= cycles) {
            count = 0
            return true
        }
        return false
    }
}