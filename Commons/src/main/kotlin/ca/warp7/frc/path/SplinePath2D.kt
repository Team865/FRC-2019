package ca.warp7.frc.path

@Suppress("unused")
class SplinePath2D(private val segments: List<Path2D>) : Path2D {
    private val count = segments.size
    override fun px(t: Double): Double {
        TODO("not implemented")
    }

    override fun py(t: Double): Double {
        TODO("not implemented")
    }

    override fun vx(t: Double): Double {
        TODO("not implemented")
    }

    override fun vy(t: Double): Double {
        TODO("not implemented")
    }

    override fun ax(t: Double): Double {
        TODO("not implemented")
    }

    override fun ay(t: Double): Double {
        TODO("not implemented")
    }

    override fun jx(t: Double): Double {
        TODO("not implemented")
    }

    override fun jy(t: Double): Double {
        TODO("not implemented")
    }

    override val dx: Double
        get() = segments.map { it.dx }.sum()
    override val dy: Double
        get() = segments.map { it.dy }.sum()
}