package ca.warp7.frc;

/**
 * Defines a periodic procedure getting input from the controllers
 */
public interface ControlLoop {
    void setup();

    void periodic();
}
