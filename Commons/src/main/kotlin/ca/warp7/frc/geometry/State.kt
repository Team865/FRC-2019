package ca.warp7.frc.geometry

interface State<T> {
    operator fun rangeTo(state: State<T>): Interpolator<T>
}