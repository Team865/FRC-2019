package ca.warp7.frc;

public interface Functions {
    static double limit(double val, double lim) {
        lim = Math.abs(lim);
        return Math.max(-lim, Math.min(val, lim));
    }

    static double constrainMinimum(double n, double tolerance) {
        return Math.abs(n) > Math.abs(tolerance) ? n : 0;
    }
}
