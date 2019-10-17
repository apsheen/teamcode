package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraRotation;
import org.openftc.easyopencv.OpenCvInternalCamera;
import org.openftc.easyopencv.OpenCvPipeline;

@TeleOp
public class InternalCamera extends LinearOpMode {
    private ElapsedTime runtime = new ElapsedTime(); //game timer

    OpenCvCamera phoneCam;

    private DcMotor leftFront;
    private DcMotor leftBack;
    private DcMotor rightFront;
    private DcMotor rightBack;

    private DcMotor liftM;

    double v1, v2, v3, v4, liftV;

    @Override
    public void runOpMode()
    {
        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());

        phoneCam = new OpenCvInternalCamera(OpenCvInternalCamera.CameraDirection.FRONT, cameraMonitorViewId);
        phoneCam.openCameraDevice();
        phoneCam.setPipeline(new SamplePipeline());
        phoneCam.startStreaming(320, 240, OpenCvCameraRotation.UPRIGHT);

        init_motors();

        waitForStart();
        runtime.reset();

        phoneCam.pauseViewport();

        while (opModeIsActive()) {
            runMechanumVectorMethod();
            runLift();

            telemetry.addData("Frame Count", phoneCam.getFrameCount());
            telemetry.addData("FPS", String.format("%.2f", phoneCam.getFps()));
            telemetry.addData("Total frame time ms", phoneCam.getTotalFrameTimeMs());
            telemetry.addData("Pipeline time ms", phoneCam.getPipelineTimeMs());
            telemetry.addData("Overhead time ms", phoneCam.getOverheadTimeMs());
            telemetry.addData("Theoretical max FPS", phoneCam.getCurrentPipelineMaxFps());

            telemetry.addData("left front power", v1);
            telemetry.addData("left back power", v2);
            telemetry.addData("right front power", v3);
            telemetry.addData("right back power", v4);
            telemetry.addData("lift power", liftV);

            telemetry.update();

            if(gamepad1.a)
            {
                phoneCam.stopStreaming();
                phoneCam.closeCameraDevice();
            }
            else if(gamepad1.y)
            {
                phoneCam.resumeViewport();
            }

            sleep(100);
        }
    }

    class SamplePipeline extends OpenCvPipeline
    {
        @Override
        public Mat processFrame(Mat input)
        {
            Imgproc.rectangle(
                    input,
                    new Point(
                            input.cols()/4,
                            input.rows()/4),
                    new Point(
                            input.cols()*(3f/4f),
                            input.rows()*(3f/4f)),
                    new Scalar(0, 255, 0), 4);
            return input;
        }
    }

    public void init_motors() {
        leftFront = hardwareMap.get(DcMotor.class, "leftFront");
        leftBack = hardwareMap.get(DcMotor.class, "leftBack");
        rightFront = hardwareMap.get(DcMotor.class, "rightFront");
        rightBack = hardwareMap.get(DcMotor.class, "rightBack");

        liftM = hardwareMap.get(DcMotor.class, "liftM");

        leftFront.setDirection(DcMotor.Direction.FORWARD);
        leftBack.setDirection(DcMotor.Direction.FORWARD);
        rightFront.setDirection(DcMotor.Direction.REVERSE);
        rightBack.setDirection(DcMotor.Direction.REVERSE);

        liftM.setDirection(DcMotor.Direction.FORWARD);

        telemetry.addData("Status", "Initialized");
        telemetry.update();
    }

    public void runMechanumVectorMethod() {
        double driveV, strafeV, rotateV;

        driveV = this.gamepad1.left_stick_y;
        strafeV = this.gamepad1.left_stick_x;
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

    public void runLift() {
        if (this.gamepad1.left_trigger != 0) {
            liftV = this.gamepad1.left_trigger;

            liftM.setPower(liftV);
        }
        if (this.gamepad1.left_stick_button) {
            liftM.setPower(0);
        }
    }
}