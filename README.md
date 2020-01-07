# WildStang 2020 Robot Software

Further documentation is in the [`design_docs`](design_docs/) folder.

## Getting Started
### Setting up required software
- These software setup instructions should work on any operating system. If you experience discrepancies ask @fruzyna or #software.
- Install [Java Development Kit (JDK) 11](https://www.oracle.com/technetwork/java/javase/downloads/jdk11-downloads-5066655.html).
  - Make sure to accept the license before choosing the appropriate version.
  - You may need to create an Oracle account to download.
- Install [Visual Studio Code](https://code.visualstudio.com/).
- Install [Git](https://git-scm.com/download/).
  - You will likely only need to do this step if you use Windows.
- Install the [Java Extension Pack](https://marketplace.visualstudio.com/items?itemName=vscjava.vscode-java-pack) for VS Code.
  - This can also be installed by searching for it in the Extensions sidebar (4 boxes button or Ctrl + Shift + X) as for the following.
- Install the [WPILib Extension](https://marketplace.visualstudio.com/items?itemName=wpilibsuite.vscode-wpilib) for VS Code.
- [Official FIRST documention](https://docs.wpilib.org/en/latest/)

### Downloading the code
- This will give command line instructions, if you prefer you can use a GUI like [GitHub Desktop](https://desktop.github.com/).
- Open the VS Code terminal (Terminal > New Terminal).
- Type `git clone https://github.com/wildstang/2019_robot_software.git` to clone the repository.
- Enter your GitHub credentials if prompted.
- Open the cloned repository's folder in VS Code (File > Open Folder...).

### Building and deploying to the robot
To open the command palette use:
- F1
- Ctrl + Shift + P
- Cmd + Shift + P
- Select the WPILib Command Palette 'W' button in the top right

To build the code, open the command palette, search and select "WPILib: Build robot code".

To deploy the code, open the command palette, search and select "WPILib: Deploy robot code, *or* press Shift + F5.

To debug the code, open the command palette, search and select "WPILib: Debug robot code".

### Next Steps
You'll want to have a look below and at the detailed docs in the
[`design_docs`](design_docs/) folder for more information on where to go next.

## Robots
This combined codebase has logic for multiple robots in it. 

Each robot has a package under `org.wildstang` e.g. `org.wildstang.year2016`, `org.wildstang.year2017`.

| Robot              | Package                                     |
| ------------------ | ------------------------------------------- |
| 2016 Robot Code    | year2016                                    |
| 2017 Robot Code    | year2017                                    |
| 2018 Robot Code    | *Robot code was developed in C++ this year* |
| 2019 Robot Code    | year2019                                    |
| 2020 Robot Code    | year2020                                    |
| Practice Drivebase | devbase1                                    |