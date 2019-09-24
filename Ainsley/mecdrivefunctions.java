package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.ElapsedTime;
import java.lang.Math;

@TeleOp
//will display this opmode in the driver station
public class mecdrivefunctions extends LinearOpMode {
    private ElapsedTime runtime = new ElapsedTime(); //game timer

    //motor objects -- private
    private DcMotor leftFront;
    private DcMotor leftBack;
    private DcMotor rightFront;
    private DcMotor rightBack;


    double v1, v2, v3, v4;

    //private DcMotor liftLeft;
    //private DcMotor liftRight;

    public void runMechanumAngleMethod() {
        double r = Math.hypot(this.gamepad1.left_stick_x, this.gamepad1.left_stick_y);
        double robotAngle = Math.atan2(this.gamepad1.left_stick_y, this.gamepad1.left_stick_x) - Math.PI / 4;
        double rightX = this.gamepad1.right_stick_y;

        v1 = 0.75 * r * Math.cos(robotAngle) + rightX;
        v2 = 0.75 * r * Math.sin(robotAngle) - rightX;
        v3 = 0.75 * r * Math.sin(robotAngle) + rightX;
        v4 = 0.75 * r * Math.cos(robotAngle) - rightX;

        leftFront.setPower(v1);
        rightFront.setPower(v2);
        leftBack.setPower(v3);
        rightBack.setPower(v4);
    }

    public void runMechanumVectorMethod() {
        double driveV, strafeV, rotateV;

        driveV = -this.gamepad1.left_stick_y;
        strafeV = -this.gamepad1.left_stick_x;
        rotateV = this.gamepad1.right_stick_x;

        v1 = driveV + strafeV + rotateV;
        v2 = driveV - strafeV + rotateV;
        v3 = driveV - strafeV - rotateV;
        v4 = driveV + strafeV - rotateV;

        leftFront.setPower(v1);
        leftBack.setPower(v2);
        rightFront.setPower(v3);
        rightBack.setPower(v4);
    }

    @Override
    public void runOpMode() {
        //initializing the motors
        leftFront = hardwareMap.get(DcMotor.class, "leftFront");
        leftBack = hardwareMap.get(DcMotor.class, "leftBack");
        rightFront = hardwareMap.get(DcMotor.class, "rightFront");
        rightBack = hardwareMap.get(DcMotor.class, "rightBack");

        //liftLeft = hardwareMap.get(DcMotor.class, "liftLeft");
        //liftRight = hardwareMap.get(DcMotor.class, "liftRight");

        //may need to change directions
        leftFront.setDirection(DcMotor.Direction.FORWARD);
        leftBack.setDirection(DcMotor.Direction.FORWARD);
        rightFront.setDirection(DcMotor.Direction.REVERSE);
        rightBack.setDirection(DcMotor.Direction.FORWARD);

        //liftLeft.setDirection(DcMotor.Direction.FORWARD);
        //liftRight.setDirection(DcMotor.Direction.FORWARD);

        //could possibly also use telemetry for debugging purposes?
        telemetry.addData("Status", "Initialized");
        telemetry.update();

        waitForStart();
        runtime.reset();

        while (opModeIsActive()) {
            //runMechanumAngleMethod();
            runMechanumVectorMethod();

            telemetry.addData("Status", "Running");
            telemetry.addData("Status", "Run Time: " + runtime.toString());
            telemetry.addData("left front power", v1);
            telemetry.addData("left right power", v2);
            telemetry.addData("left right power", v3);
            telemetry.addData("left right power", v4);
            telemetry.update();
        }
    }
}
