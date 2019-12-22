package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

@Autonomous(name = "ParkingAuton", group = "DriveTrain")
@Disabled
public class ParkingAuton extends LinearOpMode {
    private ElapsedTime runtime = new ElapsedTime(); //game timer

    BettaAutoTemplate robot;

    @Override
    public void runOpMode() {
        // initialize motors
        robot.init_motors(hardwareMap);

        telemetry.addData("Status", "Initialized");
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {

            // go forward 20 inches
            robot.runDrive("drive", robot.TRANSLATE_SPEED, 20, 20, 0);

            telemetry.addData("Status", "Running");
            telemetry.addData("Status", "Run Time: " + runtime.toString());

            telemetry.update();

            break;
        }
    }
}