package ca.warp7.frc;

import ca.warp7.action.IAction;
import ca.warp7.action.impl.ActionMode;
import edu.wpi.first.wpilibj.Notifier;
import edu.wpi.first.wpilibj.Timer;

import java.util.ArrayList;
import java.util.List;

import static ca.warp7.frc.RobotUtils.*;

public class RobotRuntime {

    private final Object mRuntimeLock = new Object();
    private final List<ControlInstance> mControls = new ArrayList<>();
    private final List<Subsystem> mSubsystems = new ArrayList<>();
    private final List<Input> mInputs = new ArrayList<>();

    private Notifier mLoopNotifier;
    private IAction mAutoRunner;
    private ControlLoop mControlLoop;

    private boolean mEnabled;
    private double mPreviousTime;
    private int mLoopsPerSecond;

    public void start(int loopsPerSecond) {
        if (mLoopNotifier == null) {
            initRuntimeUtils();
            mEnabled = false;
            mLoopsPerSecond = loopsPerSecond;
            mLoopNotifier = new Notifier(() -> {
                double time = Timer.getFPGATimestamp();
                double diff = time - mPreviousTime;
                mPreviousTime = time;
                synchronized (mRuntimeLock) {
                    for (ControlInstance instance : mControls) collectActiveControlInstance(instance);
                    for (Input input : mInputs) {
                        input.onMeasure(diff);
                        sendObjectDescription(input);
                    }
                    if (mEnabled) {
                        if (mControlLoop != null) mControlLoop.periodic();
                        for (Subsystem subsystem : mSubsystems) {
                            sendObjectDescription(subsystem);
                            subsystem.updateState();
                            subsystem.onOutput();
                        }
                    }
                }
                updateSystemStream();
            });
            mLoopNotifier.startPeriodic(1.0 / mLoopsPerSecond);
        }
    }

    public void disableOutputs() {
        System.out.println("Robot State: Disabled");
        if (mAutoRunner != null) mAutoRunner.stop();
        synchronized (mRuntimeLock) {
            mEnabled = false;
            mControlLoop = null;
            mSubsystems.forEach(Subsystem::onDisabled);
            mSubsystems.forEach(RobotUtils::sendObjectDescription);
        }
    }

    public void initAuto(IAction.Mode mode, double timeout) {
        System.out.println(String.format("Robot State: Autonomous [%s]", mode.getClass().getSimpleName()));
        IAction action = mode.getAction();
        if (mAutoRunner != null) mAutoRunner.stop();
        mAutoRunner = ActionMode.createRunner(Timer::getFPGATimestamp,
                1.0 / mLoopsPerSecond, timeout, action, true);
        synchronized (mRuntimeLock) {
            mEnabled = true;
            mControlLoop = null;
            mInputs.forEach(Input::onZeroSensors);
        }
        mAutoRunner.start();
    }

    public void initControls(ControlLoop controlLoop) {
        System.out.println(String.format("Robot State: Teleop [%s]", controlLoop.getClass().getSimpleName()));
        if (mAutoRunner != null) mAutoRunner.stop();
        synchronized (mRuntimeLock) {
            mEnabled = true;
            mInputs.forEach(Input::onZeroSensors);
            mControlLoop = controlLoop;
            mControlLoop.setup();
        }
    }

    public void registerInput(Input input) {
        synchronized (mRuntimeLock) {
            mInputs.add(input);
        }
    }

    void registerSubsystem(Subsystem subsystem) {
        synchronized (mRuntimeLock) {
            mSubsystems.add(subsystem);
        }
    }

    public RobotController getController(int port, boolean isActive) {
        int port0 = isActive ? port : -1;
        synchronized (mRuntimeLock) {
            for (ControlInstance instance : mControls)
                if (instance.getPort() == port0) return instance.getState();
            ControlInstance newInstance = new ControlInstance(port0);
            mControls.add(newInstance);
            return newInstance.getState();
        }
    }

    public static final RobotRuntime RT;

    static {
        RT = new RobotRuntime();
    }

    private RobotRuntime() {
    }
}
