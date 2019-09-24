package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

@Autonomous(name="BettaFish", group="DriveTrain")
public class Auton extends LinearOpMode {
    private ElapsedTime runtime = new ElapsedTime(); //game timer

    //motor objects -- private
    private DcMotor leftFront;
    private DcMotor leftBack;
    private DcMotor rightFront;
    private DcMotor rightBack;

    static final double TRANSLATE_SPEED = 0.6;
    static final double ROTATE_SPEED = 0.5;

    public double v1, v2, v3, v4;

    //private DcMotor liftLeft;
    //private DcMotor liftRight;

    @Override
    public void runOpMode() {
        initialize();

        waitForStart();

        telemetry.addData("Status", "Initialized");
        telemetry.update();

        autonInstructions();

        while (opModeIsActive()) {
            runMechanumVectorMethod();

            telemetry.addData("Status", "Running");
            telemetry.addData("Status", "Run Time: " + runtime.toString());

            telemetry.addData("left front power", v1);
            telemetry.addData("left back power", v2);
            telemetry.addData("right front power", v3);
            telemetry.addData("right back power", v4);
            telemetry.update();
        }
    }

    public void initialize() {
        leftFront = hardwareMap.get(DcMotor.class, "leftFront");
        leftBack = hardwareMap.get(DcMotor.class, "leftBack");
        rightFront = hardwareMap.get(DcMotor.class, "rightFront");
        rightBack = hardwareMap.get(DcMotor.class, "rightBack");

        leftFront.setDirection(DcMotor.Direction.FORWARD);
        leftBack.setDirection(DcMotor.Direction.FORWARD);
        rightFront.setDirection(DcMotor.Direction.REVERSE);
        rightBack.setDirection(DcMotor.Direction.FORWARD);

        leftFront.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        leftBack.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightFront.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightBack.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        leftFront.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        leftBack.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rightFront.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rightBack.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    public void encoderMove(double vel, double leftShift, double rightShift, double dt) {
        int leftFrontTarget, leftBackTarget, rightFrontTarget, rightBackTarget;

        if (opModeIsActive()) {
            leftFrontTarget = leftFront.getCurrentPosition() + (int)(leftShift);
            leftBackTarget = leftBack.getCurrentPosition() + (int)(leftShift);
            rightFrontTarget = rightFront.getCurrentPosition() + (int)(rightShift);
            rightBackTarget = rightBack.getCurrentPosition() + (int)(rightShift);

            leftFront.setTargetPosition(leftFrontTarget);
            leftBack.setTargetPosition(leftBackTarget);
            rightFront.setTargetPosition(rightFrontTarget);
            rightBack.setTargetPosition(rightBackTarget);

            leftFront.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            leftBack.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            rightFront.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            rightBack.setMode(DcMotor.RunMode.RUN_TO_POSITION);

            runtime.reset();
            setMotorPowers(Math.abs(vel));

            while (opModeIsActive() && (runtime.seconds() < dt)) {

            }

            setMotorPowers(0);

            leftFront.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            leftBack.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            rightFront.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            rightBack.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        }
    }

    public void autonInstructions() {
        encoderMove(TRANSLATE_SPEED, 20000.0, 20000.0, 5.0);
        encoderMove(ROTATE_SPEED, 20000.0, -20000.0, 5.0);
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

    public void setMotorPowers(double vel) {
        leftFront.setPower(vel);
        leftBack.setPower(vel);
        rightFront.setPower(vel);
        rightBack.setPower(vel);
    }
}