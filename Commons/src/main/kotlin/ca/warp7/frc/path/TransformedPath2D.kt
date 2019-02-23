package ca.warp7.frc.path

@Suppress("unused")
class TransformedPath2D(val path: Path2D, val transform: RigidTransform2D) : Path2D {
    override fun px(t: Double): Double {
        return path.px(t)
    }

    override fun py(t: Double): Double {
        return path.px(t)
    }

    override fun vx(t: Double): Double {
        return path.px(t)
    }

    override fun vy(t: Double): Double {
        return path.px(t)
    }

    override fun ax(t: Double): Double {
        return path.px(t)
    }

    override fun ay(t: Double): Double {
        return path.px(t)
    }

    override fun jx(t: Double): Double {
        return path.px(t)
    }

    override fun jy(t: Double): Double {
        return path.px(t)
    }

    override val dx: Double
        get() = 0.0
    override val dy: Double
        get() = 0.0
}