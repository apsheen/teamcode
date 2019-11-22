package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

@TeleOp
//will display this opmode in the driver station
public class ManipulatorEncoderTest extends LinearOpMode {
    private ElapsedTime runtime = new ElapsedTime(); //game timer

    //motor objects -- private
    private DcMotor manipM;

    double manipV;

    int target;

    public void runManip() {
        if (this.gamepad2.left_bumper) {
            manipV = .2;
            manipM.setPower(manipV);
        }
        else if (this.gamepad2.right_bumper) {
            manipV = -.2;
            manipM.setPower(manipV);
        } else {
            manipV = 0;
            manipM.setPower(manipV);
        }
    }

    @Override
    public void runOpMode() {
        manipM = hardwareMap.get(DcMotor.class, "manipM");
        manipM.setDirection(DcMotor.Direction.FORWARD);
        manipM.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        manipM.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        telemetry.addData("Status", "Initialized");
        telemetry.update();

        waitForStart();
        runtime.reset();

        while (opModeIsActive()) {
            runManip();

            telemetry.addData("Status", "Running");
            telemetry.addData("Status", "Run Time: " + runtime.toString());

            telemetry.addData("manipV", manipV);
            telemetry.addData("encoder tick", manipM.getCurrentPosition());


            telemetry.update();
        }
    }
}