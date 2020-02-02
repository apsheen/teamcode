package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

@Autonomous(name = "BettaBridgePark", group = "DriveTrain")
public class BettaBridgePark extends LinearOpMode {
    private ElapsedTime runtime = new ElapsedTime(); //game timer

    BettaAutoTemplate robot = new BettaAutoTemplate();

    @Override
    public void runOpMode() {
        // initialize motors
        robot.init_motors(hardwareMap);

        telemetry.addData("Status", "Initialized");
        telemetry.update();

        waitForStart();

        // go forward 20 inches
        robot.drive("d", robot.TS, 20, 20, 0);

        telemetry.addData("Status", "Running");
        telemetry.addData("Status", "Run Time: " + runtime.toString());

        telemetry.update();


    }
}