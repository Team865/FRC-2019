package ca.warp7.frc.path

@Suppress("unused")
class CompoundPath2D(private val segments: List<Path2D>) : Path2D {
    private val count = segments.size
    private val seg = 1.0 / segments.size
    override fun px(t: Double): Double {
        t.checkBounds()
        val i = (count * t).toInt()
        return segments[i].px((t - seg * i) / seg)
    }

    override fun py(t: Double): Double {
        t.checkBounds()
        val i = (count * t).toInt()
        return segments[i].py((t - seg * i) / seg)
    }

    override fun vx(t: Double): Double {
        t.checkBounds()
        val i = (count * t).toInt()
        return segments[i].vx((t - seg * i) / seg)
    }

    override fun vy(t: Double): Double {
        t.checkBounds()
        val i = (count * t).toInt()
        return segments[i].vy((t - seg * i) / seg)
    }

    override fun ax(t: Double): Double {
        t.checkBounds()
        val i = (count * t).toInt()
        return segments[i].ax((t - seg * i) / seg)
    }

    override fun ay(t: Double): Double {
        t.checkBounds()
        val i = (count * t).toInt()
        return segments[i].ay((t - seg * i) / seg)
    }

    override fun jx(t: Double): Double {
        t.checkBounds()
        val i = (count * t).toInt()
        return segments[i].jx((t - seg * i) / seg)
    }

    override fun jy(t: Double): Double {
        t.checkBounds()
        val i = (count * t).toInt()
        return segments[i].jy((t - seg * i) / seg)
    }

    override val dx: Double
        get() = segments.map { it.dx }.sum()
    override val dy: Double
        get() = segments.map { it.dy }.sum()
}