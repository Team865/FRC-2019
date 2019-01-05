package ca.warp7.frc;

public class DtMeasurement {
    private double dt;
    private double value;

    public DtMeasurement(double dt, double value) {
        this.dt = dt;
        this.value = value;
    }

    public DtMeasurement() {
    }

    public double getRatio() {
        return dt == 0 ? 0 : value / dt;
    }

    public DtMeasurement getAddedInPlace(DtMeasurement other) {
        dt += other.dt;
        value += other.value;
        return this;
    }
}
