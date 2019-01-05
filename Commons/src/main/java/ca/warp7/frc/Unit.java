package ca.warp7.frc;

public interface Unit {
    interface Degrees {
        static double toRadians(double degrees) {
            return degrees / 180.0 * Math.PI;
        }
    }
}
