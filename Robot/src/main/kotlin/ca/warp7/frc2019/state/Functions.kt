package ca.warp7.frc2019.state

import ca.warp7.frc2019.state.drive.CheesyDrive

fun cheesyDrive(block: CheesyDrive.() -> Unit) = block.invoke(CheesyDrive)