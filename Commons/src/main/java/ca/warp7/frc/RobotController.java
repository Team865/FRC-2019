package ca.warp7.frc;

@SuppressWarnings({"unused"})
public class RobotController {

    public enum State {
        Pressed, Released, HeldDown, KeptUp
    }

    State AButton;
    State BButton;
    State XButton;
    State YButton;
    State leftBumper;
    State rightBumper;
    State leftTrigger;
    State rightTrigger;
    State leftStickButton;
    State rightStickButton;
    State startButton;
    State backButton;
    State upDPad;
    State rightDPad;
    State downDPad;
    State leftDPad;

    double leftTriggerAxis;
    double rightTriggerAxis;
    double leftXAxis;
    double leftYAxis;
    double rightXAxis;
    double rightYAxis;

    public State getAButton() {
        return AButton;
    }

    public State getBButton() {
        return BButton;
    }

    public State getXButton() {
        return XButton;
    }

    public State getYButton() {
        return YButton;
    }

    public State getLeftBumper() {
        return leftBumper;
    }

    public State getRightBumper() {
        return rightBumper;
    }

    public State getLeftTrigger() {
        return leftTrigger;
    }

    public State getRightTrigger() {
        return rightTrigger;
    }

    public State getLeftStickButton() {
        return leftStickButton;
    }

    public State getRightStickButton() {
        return rightStickButton;
    }

    public State getStartButton() {
        return startButton;
    }

    public State getBackButton() {
        return backButton;
    }

    public State getUpDPad() {
        return upDPad;
    }

    public State getRightDPad() {
        return rightDPad;
    }

    public State getDownDPad() {
        return downDPad;
    }

    public State getLeftDPad() {
        return leftDPad;
    }

    public double getLeftTriggerAxis() {
        return leftTriggerAxis;
    }

    public double getRightTriggerAxis() {
        return rightTriggerAxis;
    }

    public double getLeftXAxis() {
        return leftXAxis;
    }

    public double getLeftYAxis() {
        return leftYAxis;
    }

    public double getRightXAxis() {
        return rightXAxis;
    }

    public double getRightYAxis() {
        return rightYAxis;
    }

}