package ca.warp7.frc.path

operator fun Path2D.get(t: Double): Path2DState {
    return Path2DState(t, x(t), y(t), vx(t), vy(t), ax(t), ay(t), jx(t), jy(t))
}
