package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

@TeleOp
public class BettaTeleOp extends LinearOpMode {
    private ElapsedTime runtime = new ElapsedTime(); //game timer

    private DcMotor leftFront;
    private DcMotor leftBack;
    private DcMotor rightFront;
    private DcMotor rightBack;

    private DcMotor liftL;
    private DcMotor liftR;
    private DcMotor manipM;

//    private Servo foundationServo;

    /*
    Teleop parameters
     */
    double v1;
    double v2;
    double v3;
    double v4;
    double liftLV;
    double liftRV;
    double manipV = 0;
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
        leftFront = hardwareMap.get(DcMotor.class, "leftFront");
        leftBack = hardwareMap.get(DcMotor.class, "leftBack");
        rightFront = hardwareMap.get(DcMotor.class, "rightFront");
        rightBack = hardwareMap.get(DcMotor.class, "rightBack");

        liftL = hardwareMap.get(DcMotor.class, "liftL");
        liftR = hardwareMap.get(DcMotor.class, "liftR");
        manipM = hardwareMap.get(DcMotor.class, "manipM");

        leftFront.setDirection(DcMotor.Direction.FORWARD);
        leftBack.setDirection(DcMotor.Direction.FORWARD);
        rightFront.setDirection(DcMotor.Direction.REVERSE);
        rightBack.setDirection(DcMotor.Direction.REVERSE);

        liftL.setDirection(DcMotor.Direction.REVERSE);
        liftR.setDirection(DcMotor.Direction.FORWARD);
        manipM.setDirection(DcMotor.Direction.FORWARD);

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
            leftFront.setPower(v1 / 2);
            leftBack.setPower(v2 / 2);
            rightFront.setPower(v3 / 2);
            rightBack.setPower(v4 / 2);
        }
        else {
            leftFront.setPower(v1);
            leftBack.setPower(v2);
            rightFront.setPower(v3);
            rightBack.setPower(v4);
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
            manipM.setPower(manipV);
        }
        else if (this.gamepad2.right_bumper) {
            if (ninja_mode) {
                manipV = -.125;
            }
            else {
                manipV = -.25;
            }
            manipM.setPower(manipV);
        } else {
            manipV = 0;
            manipM.setPower(manipV);
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