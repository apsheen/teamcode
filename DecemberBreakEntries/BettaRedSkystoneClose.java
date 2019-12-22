package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import java.util.List;
import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer.CameraDirection;
import org.firstinspires.ftc.robotcore.external.tfod.TFObjectDetector;
import org.firstinspires.ftc.robotcore.external.tfod.Recognition;


@Autonomous(name = "BettaRedSkystoneClose", group = "DriveTrain")
public class BettaRedSkystoneClose extends LinearOpMode {
    private static final String TFOD_MODEL_ASSET = "Skystone.tflite";
    private static final String LABEL_FIRST_ELEMENT = "Stone";
    private static final String LABEL_SECOND_ELEMENT = "Skystone";

    private static final String VUFORIA_KEY =
    "ASX0h5P/////AAABmRSjJSDG9k6QgCPHTUuoh/soPfgx3VR1JxvBhAp7cgQBWPKmsMGKcYho59KRFxQ41Jgp6nFnYIcuLPkKk1Ru4Y5Ba2cNpdp6/rrip+nShU7X627GDeyugYqlDL6enp8NIwwvdALFeg724/nXsW7bqkQliRAX92DkAaxzqz7Wv/g6JMycZtQxPf8g1ufm+jy0CCBl78iUpLBhIRlhxaSBa/8u5Os7ZxlwER8TsVcX3zgkpOajyD8/dCxLqek5+8GJMRO2eiiJLnuSbQzcbpbEBRpGIpzYVUSy/bdP1ANKypAKlbapavLHycuodFjhhukeMw1NYkr4WP6vGRjWB+W/QUPIz8HKCq+zhzOTeYyZm9MJ";

    private VuforiaLocalizer vuforia;

    private TFObjectDetector tfod;

    double middlex;

    BettaAutoTemplate robot = new BettaAutoTemplate();

    boolean breakBool = false;

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

        /**
         Vision sector
         * Run loop until robot lines up to center of skystone
         */
        runVisionSector();

        /**
         Autonomous sector
         * Autonomous instructions after running into skystone
         */
        runAutonomousSector();

        if (tfod != null) {
            tfod.shutdown();
        }
    }

    public void runVisionSector() {
        while (opModeIsActive()) {
            // terminate vision loop
            if (breakBool) {
                break;
            }

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
                            if (middlex < 220) {
                                telemetry.addLine("robot should go left");

                                // go left
                                robot.setMotorPowers(0);
                                robot.runDrive("drive", robot.TRANSLATE_SPEED,
                                        1, 1, 0);
                            }
                            // if robot is left of skystone
                            else if (middlex > 420) {
                                telemetry.addLine("robot should go right");
                                robot.setMotorPowers(0);
                                robot.runDrive("drive", robot.TRANSLATE_SPEED,
                                        -1, -1, 0);
                            }
                            else {
                                telemetry.addLine("robot in center");
                                robot.setMotorPowers(0);
                                robot.runDrive("strafe", robot.TRANSLATE_SPEED,
                                        -35, -35, 0);
                                // set the break boolean to true
                                breakBool = true;
                                // break out of the for loop
                                break;
                            }
                        }
                    }
                    telemetry.update();
                }
                else {
                    robot.runDrive("drive", robot.TRANSLATE_SPEED,
                            -1, -1, 0);
                }
            }
        }
    }

    public void runAutonomousSector() {
        robot.runIntake("in");
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