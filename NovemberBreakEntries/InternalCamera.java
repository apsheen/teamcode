package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraRotation;
import org.openftc.easyopencv.OpenCvInternalCamera;
import org.openftc.easyopencv.OpenCvPipeline;

import java.util.ArrayList;
import java.util.List;

@Autonomous
public class InternalCamera extends LinearOpMode {
    private ElapsedTime runtime = new ElapsedTime(); //game timer

    OpenCvCamera phoneCam;
    SamplePipeline pipe = new SamplePipeline();

    private DcMotor leftFront;
    private DcMotor leftBack;
    private DcMotor rightFront;
    private DcMotor rightBack;
    private DcMotor manipM;

    static final double TRANSLATE_SPEED = 1;
    static final double COUNTS_PER_MOTOR_REV = 1120;
    static final double WHEEL_DIAMETER_INCHES = 4.0;
    static final double COUNTS_PER_INCH = COUNTS_PER_MOTOR_REV / (WHEEL_DIAMETER_INCHES * Math.PI);

    @Override
    public void runOpMode() {
        init_motors();

        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        phoneCam = new OpenCvInternalCamera(OpenCvInternalCamera.CameraDirection.BACK, cameraMonitorViewId);

        //phoneCam = new OpenCvInternalCamera(OpenCvInternalCamera.CameraDirection.BACK);

        phoneCam.openCameraDevice();

        phoneCam.setPipeline(pipe);

        phoneCam.startStreaming(320, 240, OpenCvCameraRotation.UPRIGHT);

        waitForStart();

        while (opModeIsActive()) {

            sleep(10000);

            phoneCam.stopStreaming();
            phoneCam.closeCameraDevice();

//            phoneCam.pauseViewport();
//
//            phoneCam.resumeViewport();

            sleep(100);
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

        manipM = hardwareMap.get(DcMotor.class, "manipM");
        manipM.setDirection(DcMotor.Direction.FORWARD);
        manipM.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        manipM.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    public void encoderStrafe(double vel, double leftShift, double rightShift) {
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

            while (opModeIsActive() && leftFront.isBusy() && leftBack.isBusy() && rightFront.isBusy() && rightBack.isBusy()) {

            }

            setMotorPowers(0);

            leftFront.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            leftBack.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            rightFront.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            rightBack.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        }
    }

    public void encoderDrive(double vel, double leftShift, double rightShift) {
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

            while (opModeIsActive() && leftFront.isBusy() && leftBack.isBusy() && rightFront.isBusy() && rightBack.isBusy()) {

            }

            setMotorPowers(0);

            leftFront.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            leftBack.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            rightFront.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            rightBack.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        }
    }

    public void manipOpen() {
        int manipTarget;

        if (opModeIsActive()) {
            manipTarget = manipM.getCurrentPosition() + 10;

            manipM.setTargetPosition(manipTarget);

            manipM.setMode(DcMotor.RunMode.RUN_TO_POSITION);

            runtime.reset();
            manipM.setPower(0.1);

            while (opModeIsActive() && (runtime.seconds() < 1)) {

            }

            manipM.setPower(0);

            manipM.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        }
    }

    public void manipClose() {
        int manipTarget;

        if (opModeIsActive()) {
            manipTarget = manipM.getCurrentPosition() - 1000;

            manipM.setTargetPosition(manipTarget);

            manipM.setMode(DcMotor.RunMode.RUN_TO_POSITION);

            runtime.reset();
            manipM.setPower(-0.2);

            while (opModeIsActive() && (runtime.seconds() < 1)) {

            }
            manipM.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        }
    }

    public void setMotorPowers(double vel) {
        leftFront.setPower(vel);
        leftBack.setPower(vel);
        rightFront.setPower(vel);
        rightBack.setPower(vel);
    }


    class SamplePipeline extends OpenCvPipeline {
        // instance variables here
        Mat hsv = new Mat();
        Mat mask = new Mat();

        List<MatOfPoint> contoursList = new ArrayList<>();
        @Override
        public Mat processFrame(Mat input) {
            contoursList.clear();

            Imgproc.cvtColor(input, hsv, Imgproc.COLOR_BGR2HSV);
            Core.inRange(hsv, new Scalar(20, 110, 170), new Scalar(255, 255, 255), mask);
            Imgproc.findContours(mask, contoursList, new Mat(), Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);
            Imgproc.drawContours(input, contoursList, -1, new Scalar(0, 0, 255), 3, 8);



            Imgproc.rectangle(
                    mask,
                    new Point(
                            mask.cols()/4,
                            mask.rows()/4),
                    new Point(
                            mask.cols()*(3f/4f),
                            mask.rows()*(3f/4f)),
                    new Scalar(0, 255, 0), 4);

            return input;
        }
    }
}