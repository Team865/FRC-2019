package ca.warp7.frc.geometry

interface Interpolator<T> {
    operator fun get(n: Double): T
}