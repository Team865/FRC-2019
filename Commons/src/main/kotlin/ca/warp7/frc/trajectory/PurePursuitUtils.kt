package ca.warp7.frc.trajectory

import ca.warp7.frc.geometry.*


fun getDirection(pose: Pose2D, point: ArcPose2D): Double {
    val poseToPoint = point.translation - pose.translation
    val robot = pose.rotation.translation
    return if (robot cross poseToPoint < 0.0) -1.0 else 1.0 // if robot < pose turn left
}

fun findCenter(pose: Pose2D, point: ArcPose2D): Translation2D {
    val poseToPointHalfway = pose.translation.interpolate(point.translation, 0.5)
    val normal = pose.translation.inverse.transform(poseToPointHalfway).direction.normal
    val perpendicularBisector = Pose2D(poseToPointHalfway, normal)
    val normalFromPose = Pose2D(pose.translation, pose.rotation.normal)
    return if (normalFromPose.isColinear(perpendicularBisector.run { Pose2D(translation, rotation.normal) })) {
        // Special case: center is poseToPointHalfway.
        poseToPointHalfway
    } else normalFromPose.intersection(perpendicularBisector)
}

fun findRadius(pose: Pose2D, point: ArcPose2D): Double {
    return (point.translation - findCenter(pose, point)).mag * getDirection(pose, point)
}