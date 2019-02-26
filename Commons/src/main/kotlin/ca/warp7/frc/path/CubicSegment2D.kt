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

    override fun px(t: Double) = x.p(t.checkBounds())
    override fun py(t: Double) = y.p(t.checkBounds())
    override fun vx(t: Double) = x.v(t.checkBounds())
    override fun vy(t: Double) = y.v(t.checkBounds())
    override fun ax(t: Double) = x.a(t.checkBounds())
    override fun ay(t: Double) = y.a(t.checkBounds())
    override fun jx(t: Double) = x.j(t.checkBounds())
    override fun jy(t: Double) = y.j(t.checkBounds())

    override val dx: Double = x1 - x0
    override val dy: Double = y1 - y0
}