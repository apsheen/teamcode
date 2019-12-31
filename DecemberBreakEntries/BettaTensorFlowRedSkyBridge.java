package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import java.util.List;
import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer.CameraDirection;
import org.firstinspires.ftc.robotcore.external.tfod.TFObjectDetector;
import org.firstinspires.ftc.robotcore.external.tfod.Recognition;


@Autonomous(name = "BettaTensorFlowRedSkyBridge", group = "DriveTrain")
public class BettaTensorFlowRedSkyBridge extends LinearOpMode {
    private static final String TFOD_MODEL_ASSET = "Skystone.tflite";
    private static final String LABEL_FIRST_ELEMENT = "Stone";
    private static final String LABEL_SECOND_ELEMENT = "Skystone";

    private static final String VUFORIA_KEY =
    "ASX0h5P/////AAABmRSjJSDG9k6QgCPHTUuoh/soPfgx3VR1JxvBhAp7cgQBWPKmsMGKcYho59KRFxQ41Jgp6nFnYIcuLPkKk1Ru4Y5Ba2cNpdp6/rrip+nShU7X627GDeyugYqlDL6enp8NIwwvdALFeg724/nXsW7bqkQliRAX92DkAaxzqz7Wv/g6JMycZtQxPf8g1ufm+jy0CCBl78iUpLBhIRlhxaSBa/8u5Os7ZxlwER8TsVcX3zgkpOajyD8/dCxLqek5+8GJMRO2eiiJLnuSbQzcbpbEBRpGIpzYVUSy/bdP1ANKypAKlbapavLHycuodFjhhukeMw1NYkr4WP6vGRjWB+W/QUPIz8HKCq+zhzOTeYyZm9MJ";

    private VuforiaLocalizer vuforia;

    private TFObjectDetector tfod;

    private double middlex;
    private int i; // iter variable

    BettaAutoTemplate robot = new BettaAutoTemplate();

    @Override
    public void runOpMode() {
        // retrieve hardware from hardware map
        robot.init_motors(hardwareMap);

        // initialize vuforia
        initVuforia();

        // initialize tensorflow
        if (ClassFactory.getInstance().canCreateTFObjectDetector()) {
            initTfod();
        } else {
            telemetry.addData("Sorry!", "This device is not compatible with TFOD");
        }

        // activate tensorflow
        if (tfod != null) {
            tfod.activate();
        }

        // wait for start
        telemetry.addData(">", "Press Play to start");
        telemetry.update();
        waitForStart();

        for (i=0; i < 2; ++i) {
            /**
             Vision sector
             * Run into skystone
             */
            runVisionSector();

            /**
             Autonomous sector
             * Autonomous instructions after running into skystone
             */
            runAutonomousSector();
        }

        if (tfod != null) {
            tfod.shutdown();
        }
    }

    public void runVisionSector() {
        if (tfod != null) {
            // get detected objects
            List<Recognition> updatedRecognitions = tfod.getUpdatedRecognitions();

            // if there are detected objects
            if (updatedRecognitions != null) {
                telemetry.addData("# Object Detected", updatedRecognitions.size());

                // go through the recognitions
                for (Recognition recognition : updatedRecognitions) {
                    // if it detects the skystone
                    if (recognition.getLabel().equals("Skystone")) {
                        telemetry.addLine("Skystone");

                        // get middle of the skystone
                        middlex = (recognition.getLeft() + recognition.getRight()) / 2;

                        telemetry.addData("middlex", middlex);

                        // if robot is right of skystone
                        if (middlex < 270) {
                            telemetry.addLine("robot should go left");

                            // go forward and strafe into skystone
                            robot.setMotorPowers(0);
//                            robot.runDrive("ds", robot.TRANSLATE_SPEED, 12, 0, 0);
                            robot.runDrive("s", robot.TRANSLATE_SPEED, -4,
                                    -4, 0);
                            robot.runDrive("d", robot.TRANSLATE_SPEED, 20,
                                    20, 0);

                            // distance from bridge is farther
                            robot.dfb += 4;
                        }
                        // if robot is left of skystone
                        else if (middlex > 370) {
                            telemetry.addLine("robot should go right");

                            // go forward and strafe into skystone
                            robot.setMotorPowers(0);
//                            robot.runDrive("ds", robot.TRANSLATE_SPEED, 0, 12, 0);
                            robot.runDrive("s", robot.TRANSLATE_SPEED, 4,
                                    4, 0);
                            robot.runDrive("d", robot.TRANSLATE_SPEED, 20,
                                    20, 0);

                            // distance from bridge is shorter
                            robot.dfb -= 4;
                        } else {
                            telemetry.addLine("robot in center");

                            // drive into skystone
                            robot.setMotorPowers(0);
                            robot.runDrive("d", robot.TRANSLATE_SPEED, 20,
                                    20, 0);
                        }
                        break;
                    }
                }
            }
            telemetry.update();
        }
    }

    public void runAutonomousSector() {
        if (i == 0) {
            robot.runIntake("in");
            robot.runDrive("d", robot.TRANSLATE_SPEED, 6, 6, 0);
            robot.runIntake("stop");
            robot.runDrive("d", robot.TRANSLATE_SPEED, -11, -11, 0);
            robot.runDrive("d", robot.TRANSLATE_SPEED, 15, -15, 90);
            robot.runDrive("d", robot.TRANSLATE_SPEED, robot.dfb + 11,
                    robot.dfb + 11, 0);
            robot.runIntake("out");
            robot.runDrive("d", robot.TRANSLATE_SPEED, -59, -59, 0);
            robot.runDrive("d", robot.TRANSLATE_SPEED, -15, 15, -90);
            robot.dfb = 48;
        }
        else if (i == 1) {
            robot.runIntake("in");
            robot.runDrive("d", robot.TRANSLATE_SPEED, 6, 6, 0);
            robot.runIntake("stop");
            robot.runDrive("d", robot.TRANSLATE_SPEED, -11, -11, 0);
            robot.runDrive("d", robot.TRANSLATE_SPEED, 15, -15, 90);
            robot.runDrive("d", robot.TRANSLATE_SPEED, robot.dfb + 11,
                    robot.dfb + 11, 0);
            robot.runIntake("out");
            robot.runDrive("d", robot.TRANSLATE_SPEED, -11, -11, 0);
        }
    }

    /**
     * Initialize the Vuforia localization engine.
     */
    private void initVuforia() {
        /*
         * Configure Vuforia by creating a Parameter object, and passing it to the Vuforia engine.
         */
        VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters();

        parameters.vuforiaLicenseKey = VUFORIA_KEY;
        parameters.cameraDirection = CameraDirection.BACK;

        //  Instantiate the Vuforia engine
        vuforia = ClassFactory.getInstance().createVuforia(parameters);

        // Loading trackables is not necessary for the TensorFlow Object Detection engine.
    }

    /**
     * Initialize the TensorFlow Object Detection engine.
     */
    private void initTfod() {
        int tfodMonitorViewId = hardwareMap.appContext.getResources().getIdentifier(
                "tfodMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        TFObjectDetector.Parameters tfodParameters = new TFObjectDetector.Parameters(tfodMonitorViewId);
        tfodParameters.minimumConfidence = 0.75;
        tfod = ClassFactory.getInstance().createTFObjectDetector(tfodParameters, vuforia);
        tfod.loadModelFromAsset(TFOD_MODEL_ASSET, LABEL_FIRST_ELEMENT, LABEL_SECOND_ELEMENT);
    }
}