# Team 865 FRC 2019 Robot Code

```text
 ██╗    ██╗ █████╗ ██████╗ ██████╗ ███████╗
██║    ██║██╔══██╗██╔══██╗██╔══██╗╚════██║
██║ █╗ ██║███████║██████╔╝██████╔╝    ██╔╝
██║███╗██║██╔══██║██╔══██╗██╔═══╝    ██╔╝ 
╚███╔███╔╝██║  ██║██║  ██║██║        ██║  
 ╚══╝╚══╝ ╚═╝  ╚═╝╚═╝  ╚═╝╚═╝        ╚═╝  
```

This repository contains all the relevant robot software. 
It does not include scouting software, which are found in other repositories

The `Robot` module contains the main robot code. It is the entry point 

`ActionJ`, `ActionKt`, and `Commons` are libraries used by the `Robot` module. 
The action libraries help the robot to schedule tasks. 
The Commons library helps organize subsystems and retrieve controller signals. 
Each module has the main source code in `src/main` and test code in `src/test`
The modules are organized by Gradle's multi-project build system

The `Test` module contain individual tests for the robot and depends on the 
`Robot` module

The `Robot` module also has a deploy folder that gets copied to RoboRIO