package ca.warp7.frckt

abstract class InputSystem : ShuffleSource() {

    fun initInputs() {
        Lifecycle.inputSystems.add(this)
    }

    /**
     *
     * Called periodically for the subsystem to get measurements from its input devices.
     * This method is called from the Input Looper. All sensor reading should be done
     * in this method.
     *
     *
     * When using input/current states, the measured values here should change
     * the subsystem's current state
     *
     *
     * Note that this method may still be called while the robot is disabled, so
     * extra care should be made that it performs no outputting
     */
    open fun onMeasure(dt: Double) {}

    /**
     *
     * Called at the start for the subsystem to zero its sensors.
     * In addition, this method may by called by autonomous actions
     */
    open fun onZeroSensors() {}
}
