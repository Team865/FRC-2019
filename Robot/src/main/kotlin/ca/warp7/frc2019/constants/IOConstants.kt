package ca.warp7.frc2019.constants

object IOConstants {

    /*
    =====================================================
    The following motor controllers must have the same id
    as the the port they are plugged in to the PDP. Left/
    Right pairs should be made symmetrical on the PDP.
    =====================================================
     */

    const val kLiftMaster = 0
    const val kLiftFollower = 15

    const val kLeftMaster = 1
    const val kRightMaster = 14

    const val kLeftFollowerA = 2
    const val kRightFollowerA = 13

    const val kLeftFollowerB = 3
    const val kRightFollowerB = 12

    const val kOuttakeLeftMaster = 4
    const val kOuttakeRightMaster = 11

    const val kConveyorLeftMaster = 5
    const val kConveyorRightMaster = 10

    const val kIntakeMaster = 9

    /*
    ===============================================
    Solenoid ports[
    ===============================================
    */

    const val kPusherSolenoid = 2
    const val kGrabberSolenoid = 1


    /*
    ===============================================
    The following Digital I/O ports are plugged
    into the RIO
    ===============================================
     */

    const val kLiftHallEffect = 9
}