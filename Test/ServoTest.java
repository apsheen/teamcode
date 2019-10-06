package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

@TeleOp
//will display this opmode in the driver station
public class ServoTest extends LinearOpMode {
    private ElapsedTime runtime = new ElapsedTime(); //game timer

    //motor objects -- private
    private Servo manipServo;

    double manipV;

    public void runManip() {
        if (this.gamepad1.right_trigger != 0) {
            manipV = 0;
            manipServo.setPosition(manipV);
        }
        else if (this.gamepad1.right_trigger == 0) {
            manipV = 1.0;
            manipServo.setPosition(manipV);
        }
    }

    @Override
    public void runOpMode() {
        manipServo = hardwareMap.get(Servo.class, "claw");
        manipServo.setDirection(Servo.Direction.FORWARD);

        telemetry.addData("Status", "Initialized");
        telemetry.update();

        waitForStart();
        runtime.reset();

        while (opModeIsActive()) {
            runManip();

            telemetry.addData("Status", "Running");
            telemetry.addData("Status", "Run Time: " + runtime.toString());

            telemetry.addData("manipV", manipV);

            telemetry.update();
        }
    }
}