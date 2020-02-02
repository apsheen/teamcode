package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;
import org.openftc.easyopencv.OpenCvInternalCamera;
import org.openftc.easyopencv.OpenCvPipeline;

import java.util.ArrayList;
import java.util.List;


/**
 *
 * nerverest ticks
 * 60 1680
 * 40 1120
 * 20 560
 *
 * monitor: 640 x 480
 */

@Autonomous(name= "BettaCVRedSkystone", group = "DriveTrain")
public class BettaCVRedSkystone extends LinearOpMode {
    private ElapsedTime runtime = new ElapsedTime();

    BettaAutoTemplate robot = new BettaAutoTemplate();

    private static int valMid = -1;
    private static int valLeft = -1;
    private static int valRight = -1;

    private static float rectHeight = .6f/8f;
    private static float rectWidth = 1.5f/8f;

    private static float offsetX = 0f/8f;
    private static float offsetY = 0f/8f;

    private static float[] midPos = {4f/8f+offsetX, 4f/8f+offsetY};
    private static float[] leftPos = {2f/8f+offsetX, 4f/8f+offsetY};
    private static float[] rightPos = {6f/8f+offsetX, 4f/8f+offsetY};

    private final int rows = 640;
    private final int cols = 480;

    OpenCvCamera phoneCam;

    @Override
    public void runOpMode() throws InterruptedException {
        robot.init_motors(hardwareMap);

        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        phoneCam = OpenCvCameraFactory.getInstance().createInternalCamera(OpenCvInternalCamera.CameraDirection.FRONT, cameraMonitorViewId);
        phoneCam.openCameraDevice();
        phoneCam.setPipeline(new StageSwitchingPipeline());
        phoneCam.startStreaming(rows, cols, OpenCvCameraRotation.SIDEWAYS_LEFT);

        telemetry.addData("Values", valLeft+"   "+valMid+"   "+valRight);
        telemetry.update();

        waitForStart();
        runtime.reset();
        telemetry.addData("Values", valLeft+"   "+valMid+"   "+valRight);

        telemetry.update();

        if (opModeIsActive()) {
            runVisionSector();
        }

        phoneCam.stopStreaming();
        phoneCam.closeCameraDevice();
    }


    public void runVisionSector() {
        telemetry.addData("Values", valLeft+"   "+valMid+"   "+valRight);
        telemetry.update();

        if (valLeft == 0) {
//            robot.drive("s", 1, -3,
//                    -3, 0);

            robot.drive("d", robot.TS, -10,
                    -10, 0);

            robot.drive("s", 1, -45,
                    -45, 0);

            robot.runIntake("in");
            sleep(100);
            robot.drive("d", robot.TS, 8,
                    8, 0);
            robot.runIntake("stop");

            robot.drive("s",1, 22,
                    22, 0);

            robot.drive("d", robot.TS, 44,
                    -44, 180);
            robot.drive("d", robot.TS, 41,
                    41, 0);

            robot.runIntake("out");
            sleep(2000);
            robot.runIntake("stop");

            robot.drive("d", robot.TS, -8,
                    -8, 0);
        }
        else if (valMid == 0) {
//            robot.drive("s", 1, -3,
//                    -3, 0);
            robot.drive("d", robot.TS, -13,
                    -13, 0);
            robot.drive("d", robot.TS, -6,
                    -6, 0);

            robot.drive("s", 1, -40,
                    -40, 0);

            robot.runIntake("in");
            robot.drive("d", robot.TS, 9,
                    9, 0);
            robot.runIntake("stop");

            robot.drive("s", 1, 20,
                    20, 0);

            robot.drive("d", robot.TS,
                    -42, 42, 180);
            robot.drive("d", robot.TS, 51,
                    51, 0);

            robot.runIntake("out");
            sleep(2000);
            robot.runIntake("stop");

            robot.drive("d", robot.TS, -8,
                    -8, 0);

        } else if (valRight == 0) {
            robot.drive("s",1, -3,
                    -3, 0);
            robot.drive("d", robot.TS, 4,
                    4, 0);

            robot.drive("s", 1, -17,
                    -17, 0);
            robot.drive("d", robot.TS, 42,
                    -42, 180);

            robot.drive("s", 1, 23,
                    23, 0);

            robot.runIntake("in");
            robot.drive("d", robot.TS, 10,
                    10, 0);
            robot.runIntake("stop");

            robot.drive("s", 1, -20,
                    -20, 0);

            robot.drive("d", robot.TS, 50,
                    50, 0);

            robot.runIntake("out");
            sleep(2000);
            robot.runIntake("stop");

            robot.drive("d", robot.TS, -8,
                    -8, 0);
        }
    }

    static class StageSwitchingPipeline extends OpenCvPipeline
    {
        Mat yCbCrChan2Mat = new Mat();
        Mat thresholdMat = new Mat();
        Mat all = new Mat();
        List<MatOfPoint> contoursList = new ArrayList<>();

        enum Stage
        {
            detection,
            THRESHOLD,
            RAW_IMAGE,
        }

        private Stage stageToRenderToViewport = Stage.detection;
        private Stage[] stages = Stage.values();

        @Override
        public void onViewportTapped()
        {
            int currentStageNum = stageToRenderToViewport.ordinal();

            int nextStageNum = currentStageNum + 1;

            if(nextStageNum >= stages.length)
            {
                nextStageNum = 0;
            }

            stageToRenderToViewport = stages[nextStageNum];
        }

        @Override
        public Mat processFrame(Mat input)
        {
            contoursList.clear();

            Imgproc.cvtColor(input, yCbCrChan2Mat, Imgproc.COLOR_RGB2YCrCb);//converts rgb to ycrcb
            Core.extractChannel(yCbCrChan2Mat, yCbCrChan2Mat, 2);//takes cb difference and stores

            Imgproc.threshold(yCbCrChan2Mat, thresholdMat, 102, 255, Imgproc.THRESH_BINARY_INV);

            Imgproc.findContours(thresholdMat, contoursList, new Mat(), Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);
            yCbCrChan2Mat.copyTo(all);//copies mat object
            //Imgproc.drawContours(all, contoursList, -1, new Scalar(255, 0, 0), 3, 8);//draws blue contours

            double[] pixMid = thresholdMat.get((int)(input.rows()* midPos[1]) + 70, (int)(input.cols()* midPos[0]));//gets value at circle
            valMid = (int)pixMid[0];

            double[] pixLeft = thresholdMat.get((int)(input.rows()* leftPos[1]) + 70, (int)(input.cols()* leftPos[0]));//gets value at circle
            valLeft = (int)pixLeft[0];

            double[] pixRight = thresholdMat.get((int)(input.rows()* rightPos[1]) + 70, (int)(input.cols()* rightPos[0]));//gets value at circle
            valRight = (int)pixRight[0];

            Point pointMid = new Point((int)(input.cols()* midPos[0]), (int)(input.rows()* midPos[1]) + 70);
            Point pointLeft = new Point((int)(input.cols()* leftPos[0]), (int)(input.rows()* leftPos[1]) + 70);
            Point pointRight = new Point((int)(input.cols()* rightPos[0]), (int)(input.rows()* rightPos[1]) + 70);

            Imgproc.circle(all, pointMid,5, new Scalar( 255, 0, 0 ),1 );//draws circle
            Imgproc.circle(all, pointLeft,5, new Scalar( 255, 0, 0 ),1 );//draws circle
            Imgproc.circle(all, pointRight,5, new Scalar( 255, 0, 0 ),1 );//draws circle

            Imgproc.rectangle(//1-3
                    all,
                    new Point(
                            input.cols()*(leftPos[0]-rectWidth/2),
                            input.rows()*(leftPos[1]-rectHeight/2)),
                    new Point(
                            input.cols()*(leftPos[0]+rectWidth/2),
                            input.rows()*(leftPos[1]+rectHeight/2)),
                    new Scalar(0, 255, 0), 3);
            Imgproc.rectangle(//3-5
                    all,
                    new Point(
                            input.cols()*(midPos[0]-rectWidth/2),
                            input.rows()*(midPos[1]-rectHeight/2)),
                    new Point(
                            input.cols()*(midPos[0]+rectWidth/2),
                            input.rows()*(midPos[1]+rectHeight/2)),
                    new Scalar(0, 255, 0), 3);
            Imgproc.rectangle(//5-7
                    all,
                    new Point(
                            input.cols()*(rightPos[0]-rectWidth/2),
                            input.rows()*(rightPos[1]-rectHeight/2)),
                    new Point(
                            input.cols()*(rightPos[0]+rectWidth/2),
                            input.rows()*(rightPos[1]+rectHeight/2)),
                    new Scalar(0, 255, 0), 3);

            switch (stageToRenderToViewport) {
                case THRESHOLD: {
                    return thresholdMat;
                }
                case detection: {
                    return all;
                }
                case RAW_IMAGE: {
                    return input;
                }
                default: {
                    return input;
                }
            }
        }
    }
}