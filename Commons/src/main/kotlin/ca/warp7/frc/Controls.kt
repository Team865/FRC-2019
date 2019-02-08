package ca.warp7.frc

object Controls {
    fun withDriver(block: RobotController.() -> Unit) = block(CommonRobot.robotDriver)
    fun withOperator(block: RobotController.() -> Unit) = block(CommonRobot.robotOperator)
    val robotDriver = CommonRobot.robotDriver
    val robotOperator = CommonRobot.robotOperator
}