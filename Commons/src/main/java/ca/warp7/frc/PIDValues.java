package ca.warp7.frc;

import com.stormbots.MiniPID;

@SuppressWarnings("WeakerAccess")
public class PIDValues {

    private double P;
    private double I;
    private double D;
    private double F;

    public PIDValues(double p, double i, double d, double f) {
        P = p;
        I = i;
        D = d;
        F = f;
    }

    public PIDValues(double p, double i, double d) {
        this(p, i, d, 0);
    }

    public void reset() {
        P = 0;
        I = 0;
        D = 0;
        F = 0;
    }

    public void copyTo(PIDValues other) {
        other.P = P;
        other.I = I;
        other.D = D;
        other.F = F;
    }

    public void copyTo(MiniPID miniPID) {
        miniPID.setPID(P, I, D, F);
    }

    @Override
    public String toString() {
        return String.format("P: %s,I: %s,D: %s,F: %s", P, I, D, F);
    }
}
