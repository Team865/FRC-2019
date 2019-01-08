# Team 865 FRC 2019 Robot Code

This repository will contain all the relevant robot software. It does not include scouting software, which are found in other repositories

The `Robot` module contains the main robot code.

`ActionJ`, `ActionKt`, `Commons`, and `MiniPID` are libraries used by the `Robot` module. 
The action libraries help the robot to schedule tasks. 
The Commons library helps organize subsystems and retrieve controller signals. 
The MiniPID library has an implementation of PID taken from [here](https://github.com/tekdemo/MiniPID-Java)

Each module has the main source code in `src/main` and test code in `src/test`

`Constants` contains Robot constants. It is in a separate module because robot characterization also requires those constants

The modules are organized by Gradle's multi-project build system

The `Robot` module also has a deploy folder that gets copied to RoboRIO