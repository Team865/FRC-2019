# Team 865 FRC 2019 Robot Code

```text
_______       _____              
___    |________  /_____________ 
__  /| |_  ___/  __/_  ___/  __ \
_  ___ |(__  )/ /_ _  /   / /_/ /
/_/  |_/____/ \__/ /_/    \____/ 
```

### System Structure
- All code written in **Kotlin**, a programming language based on Java
- Subsystems are organized as **finite state machines**.
- States are **actions** that subsystems can perform. 
- Actions can have sub-actions, which allows for **complex routines**
- **Meta-subsystems** (RobotControl and Superstructure) control other subsystems
- **Action Management** and **Gradle’s Multi-Project Build** for modular and easy to read code

### Teleop Cycles
- **Limelight Vision Alignment**: Dual-mode angle **PID** (quick turn and drive forward) to the rocket and loading station for optimized game piece acquisition and placement
- **Limelight Stream**: Automatically switching between camera stream and vision mode 
- **Lift Velocity Control**: Squared inputs with a gravity-countering **feedforward** prevents carriage from falling and allows for precise adjustments
- **Lift Setpoint Control**: Position **PID** control loop with seven setpoints. Automatically accounts for **drift** using a Hall Effect Sensor. Precisely raises/lowers to any setpoint in under 1 second. Setpoint does not change when going to the bottom allowing faster cycles for the same level
- **Cargo Bi-Directional Passthrough**: Control shared between the driver and the operator. Motor speeds are tuned to stop cargo after going through the lift to prevent falling. Cargo passes from intake to outtake in under 1 second
- **Curvature Drive**: Driver controls throttle and radius of curvature with the addition of precise quick turning at a reduced speed
- Robot runs autonomous programs during **sandstorm control** until driver touches the controller

### Autonomous Programs
- Wheel encoders combining with the navX-MXP sensor provides accurate **odometry**
- **Complex actions** in series and parallel enables multi-state autonomous programs
- Drive trajectory planning for both straight lines and curves using a **motion planner**. Curves uses a **parametric spline function** to generate smooth position and curvature
- **Drivetrain kinematics** is modeled through calculations and empirical measurements
- A **PID** controller with an **integral zone** is used for turning the robot in place. This gets the robot to within 3 degrees to the target angle
- To follow a drive trajectory, the robot uses a **PDVA** controller with a **velocity intercept** constant to account for static friction and a proportional controller to correct for **drift in angle**

