package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

@Autonomous(name="BettaFish", group="DriveTrain")
public class AutonRedCloseSide extends LinearOpMode {
    private ElapsedTime runtime = new ElapsedTime(); //game timer

    //motor objects -- private
    private DcMotor leftFront;
    private DcMotor leftBack;
    private DcMotor rightFront;
    private DcMotor rightBack;

//    Mat frame = new Mat();
//    VideoCapture camera = new VideoCapture(0);

    static final double TRANSLATE_SPEED = 0.7;

    static final double COUNTS_PER_MOTOR_REV = 1440;
    static final double WHEEL_DIAMETER_INCHES = 4.0;
    static final double COUNTS_PER_INCH = COUNTS_PER_MOTOR_REV / (WHEEL_DIAMETER_INCHES * Math.PI);

//    private DcMotor liftLeft;
//    private DcMotor liftRight;

    @Override
    public void runOpMode() {
        initialize();

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

    public void initialize() {
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
        encoderStrafe(TRANSLATE_SPEED, -72, -72, 10.0); //shifting right
        encoderDrive(TRANSLATE_SPEED, 45, 45, 6.0);
        encoderDrive(TRANSLATE_SPEED, -45, -45, 6.0);
        encoderStrafe(TRANSLATE_SPEED, 72, 72, 10.0); //shifting left
    }

    public void encoderStrafe(double vel, double leftShift, double rightShift, double dt) {
        int leftFrontTarget, leftBackTarget, rightFrontTarget, rightBackTarget;

        if (opModeIsActive()) {
            leftFrontTarget = leftFront.getCurrentPosition() - (int)(leftShift * COUNTS_PER_INCH);
            leftBackTarget = leftBack.getCurrentPosition() + (int)(leftShift * COUNTS_PER_INCH);
            rightFrontTarget = rightFront.getCurrentPosition() + (int)(rightShift * COUNTS_PER_INCH);
            rightBackTarget = rightBack.getCurrentPosition() - (int)(rightShift * COUNTS_PER_INCH);

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