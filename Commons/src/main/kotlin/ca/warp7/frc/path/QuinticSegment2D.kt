package ca.warp7.frc.path

@Suppress("unused")
class QuinticSegment2D(
        x0: Double,
        dx0: Double,
        ddx0: Double,
        x1: Double,
        dx1: Double,
        ddx1: Double,
        y0: Double,
        dy0: Double,
        ddy0: Double,
        y1: Double,
        dy1: Double,
        ddy1: Double
) : Path2D {

    val x = QuinticSpline(x0, dx0, ddx0, x1, dx1, ddx1)
    val y = QuinticSpline(y0, dy0, ddy0, y1, dy1, ddy1)

    override fun x(t: Double) = x.p(t)
    override fun y(t: Double) = y.p(t)
    override fun vx(t: Double) = x.v(t)
    override fun vy(t: Double) = y.v(t)
    override fun ax(t: Double) = x.a(t)
    override fun ay(t: Double) = y.a(t)
    override fun jx(t: Double) = x.j(t)
    override fun jy(t: Double) = y.j(t)

    override val dx: Double = x1 - x0
    override val dy: Double = y1 - y0
}