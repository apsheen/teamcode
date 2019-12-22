package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.GyroSensor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

/**

 gamepad1 joysticks: drive train ✔
 gamepad2 joysticks: intake  ✔

 gamepad1 trigger: transfer
 gamepad2 trigger: lift ✔

 gamepad1 bumpers: manipulator ✔
 gamepad2 bumpers: foundation mover ✔


 change manip name in phone
 joystick bumper destroyed


 */



@TeleOp
public class BettaTeleOp extends LinearOpMode {
    private ElapsedTime runtime = new ElapsedTime(); //game timer

    /*
    Motors, sensors, and servos' declarations
     */
    public DcMotor fl;
    public DcMotor fr;
    public DcMotor bl;
    public DcMotor br;
    public DcMotor liftL;
    public DcMotor liftR;
    public Servo manip;
    public GyroSensor gyro;
    public Servo fMoverL;
    public Servo fMoverR;
    public DcMotor intakeL;
    public DcMotor intakeR;

    /*
    Teleop parameters
     */
    double v1, v2, v3, v4;
    double liftLV, liftRV;
    double manipPos;
    double fPos;
    double driveV, strafeV, rotateV;
    double intakeV;

    boolean ninja_mode;

    @Override
    public void runOpMode() {
        init_motors();

        waitForStart();
        runtime.reset();

        while (opModeIsActive()) {
            // ninja mode activation
            if (this.gamepad1.x || this.gamepad2.x) {
                if (ninja_mode) {
                    ninja_mode = false;
                }
                else {
                    ninja_mode = true;
                }
            }

            runDrive(ninja_mode);  //run drive train
            runLift();  //run lift
            runManipulator();  //run manipulator
            runFoundationMover();  //run foundatio mover
            runIntake();  //run intake

            // debugging information
            telemetry.addData("lf power", v1);
            telemetry.addData("lb power", v2);
            telemetry.addData("rf power", v3);
            telemetry.addData("rb power", v4);
            telemetry.addData("l-lift power", liftLV);
            telemetry.addData("r-lift power", liftRV);
            telemetry.addData("manip pos", manip);
            telemetry.addData("f pos", fPos);

            telemetry.update();
        }
    }

    /**
     * Sends motors, sensors, and servos to hardware map
     */
    public void init_motors() {
        // drive train
        fl = hardwareMap.get(DcMotor.class, "leftFront");
        bl = hardwareMap.get(DcMotor.class, "leftBack");
        fr = hardwareMap.get(DcMotor.class, "rightFront");
        br = hardwareMap.get(DcMotor.class, "rightBack");

        fl.setDirection(DcMotor.Direction.FORWARD);
        bl.setDirection(DcMotor.Direction.FORWARD);
        fr.setDirection(DcMotor.Direction.REVERSE);
        br.setDirection(DcMotor.Direction.REVERSE);

        // lift and manip
        liftL = hardwareMap.get(DcMotor.class, "liftL");
        liftR = hardwareMap.get(DcMotor.class, "liftR");
        manip = hardwareMap.get(Servo.class, "manip");

        liftL.setDirection(DcMotor.Direction.REVERSE);
        liftR.setDirection(DcMotor.Direction.FORWARD);
        manip.setDirection(Servo.Direction.FORWARD);

        // intake
        intakeL = hardwareMap.get(DcMotor.class, "intakeL");
        intakeR = hardwareMap.get(DcMotor.class, "intakeR");

        intakeL.setDirection(DcMotor.Direction.FORWARD);
        intakeR.setDirection(DcMotor.Direction.REVERSE);

        // foundation mover
        fMoverL = hardwareMap.get(Servo.class, "fMoverL");
        fMoverR = hardwareMap.get(Servo.class, "fMoverR");

        fMoverL.setDirection(Servo.Direction.FORWARD);
        fMoverR.setDirection(Servo.Direction.FORWARD);

        // transfer


        // status
        telemetry.addData("Status", "Initialized");
        telemetry.update();
    }

    /**
     Gamepad1 sector
     */

    // gamepad1 joysticks
    public void runDrive(boolean ninja_mode) {
        // values from joystick
        driveV = -this.gamepad1.left_stick_y;
        strafeV = this.gamepad1.left_stick_x;
        rotateV = this.gamepad1.right_stick_x;

        // clips motor power if < max 1 and > min -1
        v1 = Range.clip(driveV - strafeV + rotateV, -1, 1);
        v2 = Range.clip(driveV + strafeV + rotateV, -1, 1);
        v3 = Range.clip(driveV + strafeV - rotateV, -1, 1);
        v4 = Range.clip(driveV - strafeV - rotateV, -1, 1);

        if (ninja_mode) {
            fl.setPower(v1 / 2);
            bl.setPower(v2 / 2);
            fr.setPower(v3 / 2);
            br.setPower(v4 / 2);
        }
        else {
            fl.setPower(v1);
            bl.setPower(v2);
            fr.setPower(v3);
            br.setPower(v4);
        }
    }

    // gamepad1 trigger
    public void runTransfer() {

    }

    // gamepad1 bumpers
    public void runManipulator() {
        if (this.gamepad1.left_bumper) {
            manipPos = 1;
        }
        else if (this.gamepad1.right_bumper) {
            manipPos = 0;
        }
        manip.setPosition(manipPos);
    }

    /**
    Gamepad2 sector
     */

    // gamepad2 right stick
    public void runIntake() {
        intakeV = -this.gamepad2.right_stick_y;

        intakeL.setPower(intakeV);
        intakeR.setPower(intakeV);
    }

    // gamepad2 trigger
    public void runLift() {
        if (this.gamepad2.left_trigger != 0) {
            liftLV = this.gamepad2.left_trigger;
            liftRV = this.gamepad2.left_trigger;

            liftL.setPower(liftLV);
            liftR.setPower(liftRV);
        }
        else if (this.gamepad2.right_trigger != 0) {
            liftLV = -this.gamepad2.right_trigger;
            liftRV = -this.gamepad2.right_trigger;

            liftL.setPower(liftLV);
            liftR.setPower(liftRV);
        } else {
            liftL.setPower(0);
            liftR.setPower(0);
        }
    }

    // gamepad2 bumpers
    public void runFoundationMover() {
        if (this.gamepad2.right_bumper) {
            fPos = 0;
        }
        else if (this.gamepad2.left_bumper) {
            fPos = 1;
        }
        fMoverL.setPosition(fPos);
        fMoverR.setPosition(fPos);
    }
}