package ca.warp7.frc.path

operator fun Path2D.get(t: Double): Path2DState {
    return Path2DState(t, px(t), py(t), vx(t), vy(t), ax(t), ay(t), jx(t), jy(t))
}

internal fun Double.checkBounds(): Double {
    if (this.isNaN() || this.isInfinite()) throw ArithmeticException("Path cannot use NaN or Infinity")
    if (this < 0 || this > 1) throw IndexOutOfBoundsException("Path cannot be interpolated beyond [0, 1]")
    return this
}