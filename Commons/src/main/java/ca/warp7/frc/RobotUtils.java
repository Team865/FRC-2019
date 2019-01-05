package ca.warp7.frc;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.XboxController;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static ca.warp7.frc.RobotController.State.*;
import static edu.wpi.first.wpilibj.GenericHID.Hand.kLeft;
import static edu.wpi.first.wpilibj.GenericHID.Hand.kRight;

class RobotUtils {

    private static final double kTriggerDeadBand = 0.5;
    private static final int kUpPOV = 0;
    private static final int kRightPOV = 90;
    private static final int kDownPOV = 180;
    private static final int kLeftPOV = 270;

    private static final ByteArrayOutputStream sOutContent = new ByteArrayOutputStream();
    private static final ByteArrayOutputStream sErrContent = new ByteArrayOutputStream();
    private static final PrintStream sOriginalOut = System.out;
    private static final PrintStream sOriginalErr = System.err;
    private static NetworkTable sSystemsTable;
    private static NetworkTable sControllersTable;

    static void initRuntimeUtils() {
        Thread.currentThread().setName("Robot");
        NetworkTableInstance instance = NetworkTableInstance.getDefault();
        sSystemsTable = instance.getTable("Systems");
        sControllersTable = instance.getTable("Controllers");
        System.setOut(new PrintStream(sOutContent));
        System.setErr(new PrintStream(sErrContent));
    }

    static void updateSystemStream() {
        sOriginalOut.println(sOutContent.toString());
        sOutContent.reset();
        String[] errors = sErrContent.toString().split(System.lineSeparator());
        for (String error : errors) sOriginalErr.println("ERROR " + error);
        sErrContent.reset();
    }

    static void sendObjectDescription(Object system) {
        sendObjectDescription(sSystemsTable, system, system.getClass().getSimpleName());
    }

    private static void sendObjectDescription(NetworkTable table, Object system, String subTable) {
        for (Method method : system.getClass().getMethods()) {
            String name = method.getName();
            if (name.startsWith("get")) {
                String entry = subTable + "/" + name.substring(3);
                try {
                    sendNetworkTableValue(table.getEntry(entry), method.invoke(system));
                } catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static void sendNetworkTableValue(NetworkTableEntry entry, Object value) {
        if (entry == null || value == null) return;
        if (value instanceof Number) {
            double n = ((Number) value).doubleValue();
            if (Math.abs(n) < 0.001) n = 0;
            entry.setNumber(n);
        } else if (value instanceof Boolean) entry.setBoolean((Boolean) value);
        else if (value instanceof String) entry.setString((String) value);
        else if (value.getClass().isEnum()) entry.setString(value.toString());
        else entry.setString(value.getClass().getSimpleName() + " Object");
    }

    private static RobotController.State u(RobotController.State old, boolean _new) {
        return _new ? old == Pressed || old == HeldDown ? HeldDown : Pressed :
                old == Released || old == KeptUp ? KeptUp : Released;
    }

    private static void collect(RobotController s, XboxController c) {
        int POV = c.getPOV();
        s.leftTriggerAxis = c.getTriggerAxis(kLeft);
        s.rightTriggerAxis = c.getTriggerAxis(kRight);
        s.leftXAxis = c.getX(kLeft);
        s.leftYAxis = c.getY(kLeft);
        s.rightXAxis = c.getX(kRight);
        s.rightYAxis = c.getY(kRight);
        s.AButton = u(s.AButton, c.getAButton());
        s.BButton = u(s.BButton, c.getBButton());
        s.XButton = u(s.XButton, c.getXButton());
        s.YButton = u(s.YButton, c.getYButton());
        s.leftBumper = u(s.leftBumper, c.getBumper(kLeft));
        s.rightBumper = u(s.rightBumper, c.getBumper(kRight));
        s.leftTrigger = u(s.leftTrigger, s.leftTriggerAxis > kTriggerDeadBand);
        s.rightTrigger = u(s.rightTrigger, s.rightTriggerAxis > kTriggerDeadBand);
        s.leftStickButton = u(s.leftStickButton, c.getStickButton(kLeft));
        s.rightStickButton = u(s.rightStickButton, c.getStickButton(kRight));
        s.startButton = u(s.startButton, c.getStartButton());
        s.backButton = u(s.backButton, c.getBackButton());
        s.upDPad = u(s.upDPad, POV == kUpPOV);
        s.rightDPad = u(s.rightDPad, POV == kRightPOV);
        s.downDPad = u(s.downDPad, POV == kDownPOV);
        s.leftDPad = u(s.leftDPad, POV == kLeftPOV);
    }

    static void collectActiveControlInstance(ControlInstance instance) {
        if (instance.isActive()) {
            collect(instance.getState(), instance.getController());
            sendObjectDescription(sControllersTable, instance.mState, "Controller " + instance.mPort);
        }
    }

    private static void reset(RobotController s) {
        s.AButton = KeptUp;
        s.BButton = KeptUp;
        s.XButton = KeptUp;
        s.YButton = KeptUp;
        s.leftBumper = KeptUp;
        s.rightBumper = KeptUp;
        s.leftTrigger = KeptUp;
        s.rightTrigger = KeptUp;
        s.leftStickButton = KeptUp;
        s.rightStickButton = KeptUp;
        s.startButton = KeptUp;
        s.backButton = KeptUp;
        s.upDPad = KeptUp;
        s.rightDPad = KeptUp;
        s.downDPad = KeptUp;
        s.leftDPad = KeptUp;
        s.leftTriggerAxis = 0;
        s.rightTriggerAxis = 0;
        s.leftXAxis = 0;
        s.leftYAxis = 0;
        s.rightXAxis = 0;
        s.rightYAxis = 0;
    }

    static class ControlInstance {
        private final RobotController mState;
        private XboxController mController;
        private int mPort;
        private boolean mActive;

        ControlInstance(int port) {
            this.mPort = port;
            this.mState = new RobotController();
            if (port >= 0 && port < 6) {
                mActive = true;
                this.mController = new XboxController(port);
                reset(mState);
            } else mActive = false;
        }

        RobotController getState() {
            return mState;
        }

        XboxController getController() {
            return mController;
        }

        int getPort() {
            return mPort;
        }

        boolean isActive() {
            return mActive;
        }
    }
}
