package ca.warp7.frc.path

import ca.warp7.frc.geometry.Pose2D

fun QuinticSegment2D.parameterizedWithArgLength(): List<Pose2D> = TODO()

fun List<QuinticSegment2D>.parameterizedWithArcLength(): List<Pose2D> =
        map { it.parameterizedWithArgLength() }.flatten()