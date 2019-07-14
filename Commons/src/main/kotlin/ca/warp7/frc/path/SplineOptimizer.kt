package ca.warp7.frc.path

import ca.warp7.frc.geometry.*

/**
 * @author Team 254
 */

fun QuinticSegment2D.sumDCurvature2(): Double =
        (0 until 100).map { get(it / 100.0) }.map { it.dCurvature2 }.sum()

fun List<QuinticSegment2D>.sumDCurvature2(): Double = this.sumByDouble { it.sumDCurvature2() }

fun List<QuinticSegment2D>.optimized(): List<QuinticSegment2D> = toMutableList().apply { optimize() }

fun MutableList<QuinticSegment2D>.optimize() {
    var count = 0
    var prev = sumDCurvature2()
    while (count < 100) {
        runOptimizationIteration()
        val current = sumDCurvature2()
        if (prev - current < 0.001) return
        prev = current
        count++
    }
}

private data class ControlPoint(var ddx: Double, var ddy: Double)

fun QuinticSegment2D.getStartPose(): Pose2D {
    return Pose2D(Translation2D(x0, y0), Rotation2D(dx0, dy0).norm)
}

fun QuinticSegment2D.getEndPose(): Pose2D {
    return Pose2D(Translation2D(x1, y1), Rotation2D(dx1, dy1).norm)
}

fun QuinticSegment2D.updateD2End(ddx: Double, ddy: Double): QuinticSegment2D {
    return QuinticSegment2D(
            x0, x1, dx0, dx1, ddx0, ddx1 + ddx, y0, y1, dy0, dy1, ddy0, ddy1 + ddy
    )
}

fun QuinticSegment2D.updateD2Start(ddx: Double, ddy: Double): QuinticSegment2D {
    return QuinticSegment2D(
            x0, x1, dx0, dx1, ddx0 + ddx, ddx1, y0, y1, dy0, dy1, ddy0 + ddy, ddy1
    )
}

private const val kEpsilon = 1E-5
private const val kStepSize = 1.0

fun MutableList<QuinticSegment2D>.runOptimizationIteration() {
    //can't optimize anything with less than 2 splines
    if (size <= 1) {
        return
    }

    val controlPoints = Array(size - 1) { ControlPoint(0.0, 0.0) }
    var magnitude = 0.0

    for (i in 0 until size - 1) {
        //don't try to optimize co-linear points
        if (this[i].getStartPose().isColinear(this[i + 1].getStartPose()) ||
                this[i].getEndPose().isColinear(this[i + 1].getEndPose())) continue

        val original = sumDCurvature2()
        val now = this[i]
        val next = this[i + 1]

        //calculate partial derivatives of sumDCurvature2
        this[i] = now.copy(ddx1 = now.ddx1 + kEpsilon)
        this[i + 1] = next.copy(ddx0 = next.ddx0 + kEpsilon)
        controlPoints[i].ddx = (sumDCurvature2() - original) / kEpsilon

        this[i] = now.copy(ddy1 = now.ddy1 + kEpsilon)
        this[i + 1] = next.copy(ddy0 = next.ddy0 + kEpsilon)
        controlPoints[i].ddy = (sumDCurvature2() - original) / kEpsilon

        this[i] = now
        this[i + 1] = next
        magnitude += controlPoints[i].ddx * controlPoints[i].ddx + controlPoints[i].ddy * controlPoints[i].ddy
    }

    magnitude = Math.sqrt(magnitude)

    //minimize along the direction of the gradient
    //first calculate 3 points along the direction of the gradient
    //middle point is at the current location
    val p2 = Translation2D(0.0, sumDCurvature2())

    for (i in 0 until size - 1) { //first point is offset from the middle location by -stepSize
        if (this[i].getStartPose().isColinear(this[i + 1].getStartPose())
                || this[i].getEndPose().isColinear(this[i + 1].getEndPose())) continue

        //normalize to step size
        controlPoints[i].ddx *= kStepSize / magnitude
        controlPoints[i].ddy *= kStepSize / magnitude

        //move opposite the gradient by step size amount
        //recompute the spline's coefficients to account for new second derivatives
        this[i] = this[i].updateD2End(-controlPoints[i].ddx, -controlPoints[i].ddy)
        this[i + 1] = this[i + 1].updateD2Start(-controlPoints[i].ddx, -controlPoints[i].ddy)
    }

    val p1 = Translation2D(-kStepSize, sumDCurvature2())

    for (i in 0 until size - 1) { //last point is offset from the middle location by +stepSize
        if (this[i].getStartPose().isColinear(this[i + 1].getStartPose())
                || this[i].getEndPose().isColinear(this[i + 1].getEndPose())) continue

        //move along the gradient by 2 times the step size amount (to return to original location and move by 1
        // step)
        //recompute the spline's coefficients to account for new second derivatives

        this[i] = this[i].updateD2End(2 * controlPoints[i].ddx, 2 * controlPoints[i].ddy)
        this[i + 1] = this[i + 1].updateD2Start(2 * controlPoints[i].ddx, 2 * controlPoints[i].ddy)
    }

    val p3 = Translation2D(kStepSize, sumDCurvature2())

    val stepSize = fitParabola(p1, p2, p3) //approximate step size to minimize sumDCurvature2 along the gradient

    for (i in 0 until size - 1) {
        if (this[i].getStartPose().isColinear(this[i + 1].getStartPose())
                || this[i].getEndPose().isColinear(this[i + 1].getEndPose())) continue

        //move by the step size calculated by the parabola fit (+1 to offset for the final transformation to find
        // p3)
        controlPoints[i].ddx *= 1 + stepSize / kStepSize
        controlPoints[i].ddy *= 1 + stepSize / kStepSize

        //recompute the spline's coefficients to account for new second derivatives

        this[i] = this[i].updateD2End(controlPoints[i].ddx, controlPoints[i].ddy)
        this[i + 1] = this[i + 1].updateD2Start(controlPoints[i].ddx, controlPoints[i].ddy)
    }
}