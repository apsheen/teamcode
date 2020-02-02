package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

@Autonomous(name = "BettaFoundation", group = "DriveTrain")
public class BettaFoundation extends LinearOpMode {
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
//        robot.drive("d", robot.TS, 20, 20, 0);

//        robot.drive("s", robot.TS, -20, -20, 0);

        robot.drive("d", .3, -40,
                -40, 0);

        sleep(1000);

        robot.runFoundationMover("down");

        sleep(1000);

        robot.drive("d", .3, 20,
                20, 0);
        robot.drive("d", .3, 20,
                20, 0);
//        robot.drive("d", .3, 20,
//                20, 0);

        robot.runFoundationMover("up");

        robot.drive("s", robot.TS, 45,
                45, 0);

        telemetry.addData("Status", "Running");
        telemetry.addData("Status", "Run Time: " + runtime.toString());

        telemetry.update();


    }
}