package ca.warp7.frc;

public interface Input {
    /**
     * <p>Called periodically for the subsystem to get measurements from its input devices.
     * This method is called from the Input Looper. All sensor reading should be done
     * in this method.</p>
     *
     * <p>When using input/current states, the measured values here should change
     * the subsystem's current state</p>
     *
     * <p>Note that this method may still be called while the robot is disabled, so
     * extra care should be made that it performs no outputting</p>
     */
    default void onMeasure(double dt) {
    }

    /**
     * <p>Called at the start for the subsystem to zero its sensors.
     * In addition, this method may by called by autonomous actions</p>
     */
    default void onZeroSensors() {
    }
}
