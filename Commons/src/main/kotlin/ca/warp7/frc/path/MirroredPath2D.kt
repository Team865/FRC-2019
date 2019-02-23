package ca.warp7.frc.path

@Suppress("unused")
class MirroredPath2D(val path: Path2D, private val transform: Reflection2D) : Path2D {
    override fun px(t: Double) = if (transform.x) -path.px(t) else path.px(t)
    override fun py(t: Double) = if (transform.y) -path.py(t) else path.py(t)
    override fun vx(t: Double) = if (transform.x) -path.vx(t) else path.vx(t)
    override fun vy(t: Double) = if (transform.y) -path.vy(t) else path.vy(t)
    override fun ax(t: Double) = if (transform.x) -path.ax(t) else path.ax(t)
    override fun ay(t: Double) = if (transform.y) -path.ay(t) else path.ay(t)
    override fun jx(t: Double) = if (transform.x) -path.jx(t) else path.jx(t)
    override fun jy(t: Double) = if (transform.y) -path.jy(t) else path.jy(t)
    override val dx: Double get() = if (transform.x) -path.dx else path.dx
    override val dy: Double get() = if (transform.y) -path.dy else path.dy
}