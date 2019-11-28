package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

@Autonomous(name = "GyroStrafeTest", group = "DriveTrain")
public class GyroStrafeTest extends LinearOpMode {
    private ElapsedTime runtime = new ElapsedTime(); //game timer

    BettaAutoTemplate robot = new BettaAutoTemplate();

    @Override
    public void runOpMode() {
        telemetry.addData(">", "Robot Heading = %d", robot.gyro.getHeading());

        robot.init_motors(hardwareMap);

        telemetry.addData("Status", "Initialized");

        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {
            robot.encoderRun("strafe", robot.TRANSLATE_SPEED, 10, 10,
                        0);
            robot.encoderRun("drive", robot.TRANSLATE_SPEED, 17, -17,
                        90);

            telemetry.addData("Status", "Running");
            telemetry.addData("Status", "Run Time: " + runtime.toString());

            telemetry.update();

            break;
        }
    }
}