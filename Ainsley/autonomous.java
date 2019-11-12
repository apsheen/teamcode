package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.ElapsedTime;

@Autonomous(name="AutonRedClose", group="DriveTrain")
public class AutonRedClose extends LinearOpMode {
    private ElapsedTime runtime = new ElapsedTime(); //game timer

    //motor objects -- private
    private DcMotor leftFront;
    private DcMotor leftBack;
    private DcMotor rightFront;
    private DcMotor rightBack;

    static final double TRANSLATE_SPEED = 0.45;
    static final double COUNTS_PER_MOTOR_REV = 1440;
    static final double WHEEL_DIAMETER_INCHES = 4.0;
    static final double COUNTS_PER_INCH = COUNTS_PER_MOTOR_REV / (WHEEL_DIAMETER_INCHES * Math.PI);

    @Override
    public void runOpMode() {
        init_motors();

        telemetry.addData("Status", "Initialized");
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {

            autonInstructions();

            telemetry.addData("Status", "Running");
            telemetry.addData("Status", "Run Time: " + runtime.toString());

            telemetry.update();
        }
    }

    public void init_motors() {
        leftFront = hardwareMap.get(DcMotor.class, "leftFront");
        leftBack = hardwareMap.get(DcMotor.class, "leftBack");
        rightFront = hardwareMap.get(DcMotor.class, "rightFront");
        rightBack = hardwareMap.get(DcMotor.class, "rightBack");

        leftFront.setDirection(DcMotor.Direction.FORWARD);
        leftBack.setDirection(DcMotor.Direction.FORWARD);
        rightFront.setDirection(DcMotor.Direction.REVERSE);
        rightBack.setDirection(DcMotor.Direction.REVERSE);

        leftFront.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        leftBack.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightFront.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightBack.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        leftFront.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        leftBack.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rightFront.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rightBack.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    public void autonInstructions() {
        encoderStrafe(TRANSLATE_SPEED, -20, -20, 10.0); //shifting right
    }

    public void encoderStrafe(double vel, double leftShift, double rightShift, double dt) {
        int leftFrontTarget, leftBackTarget, rightFrontTarget, rightBackTarget;

        if (opModeIsActive()) {
            leftFrontTarget = leftFront.getCurrentPosition() - (int)(leftShift * COUNTS_PER_INCH);
            leftBackTarget = leftBack.getCurrentPosition() + (int)(leftShift * COUNTS_PER_INCH * 0.9);
            rightFrontTarget = rightFront.getCurrentPosition() + (int)(rightShift * COUNTS_PER_INCH);
            rightBackTarget = rightBack.getCurrentPosition() - (int)(rightShift * COUNTS_PER_INCH * 0.4);

            leftFront.setTargetPosition(leftFrontTarget);
            leftBack.setTargetPosition(leftBackTarget);
            rightFront.setTargetPosition(rightFrontTarget);
            rightBack.setTargetPosition(rightBackTarget);

            leftFront.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            leftBack.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            rightFront.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            rightBack.setMode(DcMotor.RunMode.RUN_TO_POSITION);

            runtime.reset();

            leftFront.setPower(vel);
            leftBack.setPower(vel * 0.9);
            rightFront.setPower(vel);
            rightBack.setPower(vel * 0.4);

            while (opModeIsActive() && (runtime.seconds() < dt)) {
                telemetry.addData("left front power", leftFront.getPower());
                telemetry.addData("left back power", leftBack.getPower());
                telemetry.addData("right front power", rightFront.getPower());
                telemetry.addData("right back power", rightBack.getPower());

                telemetry.update();
            }

            setMotorPowers(0);

            leftFront.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            leftBack.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            rightFront.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            rightBack.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        }
    }

    public void encoderDrive(double vel, double leftShift, double rightShift, double dt) {
        int leftFrontTarget, leftBackTarget, rightFrontTarget, rightBackTarget;

        if (opModeIsActive()) {
            leftFrontTarget = leftFront.getCurrentPosition() + (int)(leftShift * COUNTS_PER_INCH);
            leftBackTarget = leftBack.getCurrentPosition() + (int)(leftShift * COUNTS_PER_INCH);
            rightFrontTarget = rightFront.getCurrentPosition() + (int)(rightShift * COUNTS_PER_INCH);
            rightBackTarget = rightBack.getCurrentPosition() + (int)(rightShift * COUNTS_PER_INCH);

            leftFront.setTargetPosition(leftFrontTarget);
            leftBack.setTargetPosition(leftBackTarget);
            rightFront.setTargetPosition(rightFrontTarget);
            rightBack.setTargetPosition(rightBackTarget);

            leftFront.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            leftBack.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            rightFront.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            rightBack.setMode(DcMotor.RunMode.RUN_TO_POSITION);

            runtime.reset();

            setMotorPowers(vel);

            while (opModeIsActive() && (runtime.seconds() < dt)) {

            }

            setMotorPowers(0);

            leftFront.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            leftBack.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            rightFront.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            rightBack.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        }
    }

    public void setMotorPowers(double vel) {
        leftFront.setPower(vel);
        leftBack.setPower(vel);
        rightFront.setPower(vel);
        rightBack.setPower(vel);
    }
}
