package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

@TeleOp
public class JoyStickControlTwo extends LinearOpMode {
    private ElapsedTime runtime = new ElapsedTime(); //game timer

    private DcMotor leftFront;
    private DcMotor leftBack;
    private DcMotor rightFront;
    private DcMotor rightBack;

    private DcMotor liftL;
    private DcMotor liftR;
    private DcMotor manipM;

//    private Servo foundationServo;

    double v1, v2, v3, v4, liftLV, liftRV;
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
            runLift(ninja_mode);
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

//        manipM.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        telemetry.addData("Status", "Initialized");
        telemetry.update();
    }

    public void runDrive(boolean ninja_mode) {
        double driveV, strafeV, rotateV;

        driveV = -this.gamepad1.left_stick_y;
        strafeV = this.gamepad1.left_stick_x;
        rotateV = this.gamepad1.right_stick_x;

        v1 = driveV - strafeV + rotateV;
        v2 = driveV + strafeV + rotateV;
        v3 = driveV + strafeV - rotateV;
        v4 = driveV - strafeV - rotateV;

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

    public void runLift(boolean ninja_mode) {
        if (this.gamepad1.left_trigger != 0) {
            liftLV = this.gamepad1.left_trigger;
            liftRV = this.gamepad1.left_trigger;
            if (ninja_mode) {
                liftL.setPower(liftLV / 2);
                liftR.setPower(liftRV / 2);
            }
            else {
                liftL.setPower(liftLV);
                liftR.setPower(liftRV);
            }
        }
        else if (this.gamepad1.right_trigger != 0) {
            liftLV = -this.gamepad1.right_trigger;
            liftRV = -this.gamepad1.right_trigger;
            if (ninja_mode) {
                liftL.setPower(liftLV / 2);
                liftR.setPower(liftRV / 2);
            }
            else {
                liftL.setPower(liftLV);
                liftR.setPower(liftRV);
            }
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