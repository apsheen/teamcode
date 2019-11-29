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
import org.opencv.imgproc.Moments;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvInternalCamera;
import org.openftc.easyopencv.OpenCvCameraRotation;
import org.openftc.easyopencv.OpenCvPipeline;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Autonomous
public class OpenCVAuton extends LinearOpMode {
    private ElapsedTime runtime = new ElapsedTime(); //game timer

    BettaAutoTemplate robot = new BettaAutoTemplate();

    OpenCvCamera phoneCam;
    SamplePipeline pipe = new SamplePipeline();

    @Override
    public void runOpMode() {
        robot.init_motors(hardwareMap);

        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        phoneCam = OpenCvCameraFactory.getInstance().createInternalCamera(OpenCvInternalCamera.CameraDirection.BACK, cameraMonitorViewId);

        phoneCam.openCameraDevice();

        phoneCam.setPipeline(pipe);

        phoneCam.startStreaming(320, 240, OpenCvCameraRotation.UPRIGHT);

        waitForStart();

        while (opModeIsActive()) {

            if (pipe.detect) {
                robot.encoderRun("drive", robot.TRANSLATE_SPEED, 0,
                                 0, 0);
            }
            else {
                robot.encoderRun("drive", robot.TRANSLATE_SPEED, 8,
                                 8, 0);
            }

//            phoneCam.pauseViewport();
//
//            phoneCam.resumeViewport();
            sleep(10000);
            break;
        }
        phoneCam.stopStreaming();
        phoneCam.closeCameraDevice();
    }


    class SamplePipeline extends OpenCvPipeline {
        // instance variables here
        Mat hsv = new Mat();
        Mat mask = new Mat();
        Mat res =  new Mat();

        boolean detect = false;

        int x;
        int y;
        int centroid;

        List<MatOfPoint> contours = new ArrayList<>();
        List<Moments> mu = new ArrayList<Moments>();

        @Override
        public Mat processFrame(Mat input) {
            contours.clear();

            Imgproc.cvtColor(input, hsv, Imgproc.COLOR_BGR2HSV);
            Core.inRange(hsv, new Scalar(20, 110, 170), new Scalar(255, 255, 255), mask);
            Imgproc.findContours(mask, contours, new Mat(), Imgproc.RETR_LIST,
                                 Imgproc.CHAIN_APPROX_SIMPLE);

            // get pixel of centroid of block
            // if black then it's skystone, if yellow then regular block
            for (int i = 0; i < contours.size(); i++) {
                mu.add(i, Imgproc.moments(contours.get(i), false));
                Moments p = mu.get(i);
                x = (int) (p.get_m10() / p.get_m00());
                y = (int) (p.get_m01() / p.get_m00());

                if ((input.get(x, y)[0] > 20 && input.get(x, y)[0] < 255) &&
                    (input.get(x, y)[1] > 110 && input.get(x, y)[1] < 255) &&
                    (input.get(x, y)[2] > 170 && input.get(x, y)[2] < 255)) {
                    detect = false;
                }
                else if ((input.get(x, y)[0] > 0 && input.get(x, y)[0] < 20) &&
                         (input.get(x, y)[1] > 0 && input.get(x, y)[1] < 60) &&
                         (input.get(x, y)[2] > 0 && input.get(x, y)[2] < 60)) {
                    detect = true;
                }

                Imgproc.circle(input, new Point(x, y), 4, new Scalar(255,49,0,255));
            }

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
}
