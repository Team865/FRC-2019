package ca.warp7.frc.geometry

import ca.warp7.frc.f
import ca.warp7.frc.interpolate

data class CurvatureState<T : State<T>>(
        override val state: T,
        val curvature: Double,
        val dk_ds: Double
) : StateView<T, CurvatureState<T>> {

    override fun rangeTo(state: CurvatureState<T>): Interpolator<CurvatureState<T>> =
            object : Interpolator<CurvatureState<T>> {
                override fun get(x: Double): CurvatureState<T> = interpolate(state, x)
            }

    override fun interpolate(other: CurvatureState<T>, x: Double): CurvatureState<T> = when {
        x <= 0 -> CurvatureState(state.copy, curvature, dk_ds)
        x >= 1 -> CurvatureState(other.state.copy, other.curvature, other.dk_ds)
        else -> CurvatureState(
                state.interpolate(other.state, x),
                interpolate(curvature, other.curvature, x),
                interpolate(dk_ds, other.dk_ds, x)
        )
    }

    override fun toString(): String {
        return "Curvature($state, k=${curvature.f}, dk_ds=${dk_ds.f})"
    }
}