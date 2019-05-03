package ca.warp7.frc.path

@Suppress("unused")
class CubicSegment2D(
        x0: Double,
        dx0: Double,
        x1: Double,
        dx1: Double,
        y0: Double,
        dy0: Double,
        y1: Double,
        dy1: Double
) : Path2D {

    val x = CubicSpline(x0, dx0, x1, dx1)
    val y = CubicSpline(y0, dy0, y1, dy1)

    override fun px(t: Double) = x.p(t)
    override fun py(t: Double) = y.p(t)
    override fun vx(t: Double) = x.v(t)
    override fun vy(t: Double) = y.v(t)
    override fun ax(t: Double) = x.a(t)
    override fun ay(t: Double) = y.a(t)
    override fun jx(t: Double) = x.j()
    override fun jy(t: Double) = y.j()

    override val dx: Double = x1 - x0
    override val dy: Double = y1 - y0
}