package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.GyroSensor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

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
    public DcMotor manip;
    public GyroSensor gyro;
//    public Servo fMover;

    /*
    Teleop parameters
     */
    double v1;
    double v2;
    double v3;
    double v4;
    double liftLV;
    double liftRV;
    double manipV;
//    double position;

    boolean ninja_mode;

    @Override
    public void runOpMode() {
        init_motors();

        waitForStart();
        runtime.reset();

        while (opModeIsActive()) {
            if (this.gamepad1.x || this.gamepad2.x) {
                if (ninja_mode) {
                    ninja_mode = false;
                }
                else {
                    ninja_mode = true;
                }
            }
            runDrive(ninja_mode);
            runLift();
            runManipulator(ninja_mode);
//            runFoundationMover();

            telemetry.addData("left front power", v1);
            telemetry.addData("left back power", v2);
            telemetry.addData("right front power", v3);
            telemetry.addData("right back power", v4);
            telemetry.addData("left lift power", liftLV);
            telemetry.addData("right lift power", liftRV);
            telemetry.addData("manipulator power", manipV);
//            telemetry.addData("foundation mover position", position);

            telemetry.update();
        }
    }

    /**
     * Sends motors, sensors, and servos to hardware map
     */
    public void init_motors() {
        fl = hardwareMap.get(DcMotor.class, "leftFront");
        bl = hardwareMap.get(DcMotor.class, "leftBack");
        fr = hardwareMap.get(DcMotor.class, "rightFront");
        br = hardwareMap.get(DcMotor.class, "rightBack");

        liftL = hardwareMap.get(DcMotor.class, "liftL");
        liftR = hardwareMap.get(DcMotor.class, "liftR");
        manip = hardwareMap.get(DcMotor.class, "manipM");

        fl.setDirection(DcMotor.Direction.FORWARD);
        bl.setDirection(DcMotor.Direction.FORWARD);
        fr.setDirection(DcMotor.Direction.REVERSE);
        br.setDirection(DcMotor.Direction.REVERSE);

        liftL.setDirection(DcMotor.Direction.REVERSE);
        liftR.setDirection(DcMotor.Direction.FORWARD);
        manip.setDirection(DcMotor.Direction.FORWARD);

//        foundationServo = hardwareMap.get(Servo.class, "foundationMover");
//        foundationServo.setDirection(Servo.Direction.FORWARD);

        telemetry.addData("Status", "Initialized");
        telemetry.update();
    }

    public void runDrive(boolean ninja_mode) {
        double driveV, strafeV, rotateV;

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

    public void runLift() {
        if (this.gamepad1.left_trigger != 0) {
            liftLV = this.gamepad1.left_trigger;
            liftRV = this.gamepad1.left_trigger;

            liftL.setPower(liftLV);
            liftR.setPower(liftRV);
        }
        else if (this.gamepad1.right_trigger != 0) {
            liftLV = -this.gamepad1.right_trigger;
            liftRV = -this.gamepad1.right_trigger;

            liftL.setPower(liftLV);
            liftR.setPower(liftRV);
        } else {
            liftL.setPower(0);
            liftR.setPower(0);
        }
    }

    public void runManipulator(boolean ninja_mode) {
        if (this.gamepad2.left_bumper) {
            if (ninja_mode) {
                manipV = .125;
            }
            else {
                manipV = .25;
            }
            manip.setPower(manipV);
        }
        else if (this.gamepad2.right_bumper) {
            if (ninja_mode) {
                manipV = -.125;
            }
            else {
                manipV = -.25;
            }
            manip.setPower(manipV);
        } else {
            manipV = 0;
            manip.setPower(manipV);
        }
    }
//    public void runFoundationMover() {
//        if (this.gamepad2.a) {
//            position = 0;
//            foundationServo.setPosition(position);
//        } //foundation mover code
//        else if (this.gamepad2.y) {
//            position = 1;
//            foundationServo.setPosition(position);
//        }
//    }
}