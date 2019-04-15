package ca.warp7.frc.drive

data class ChassisState(val linear: Double, val angular: Double) {
    override fun toString(): String {
        return "(${"%.3f".format(linear)}, ${"%.3f".format(angular)})"
    }
}