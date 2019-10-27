package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.ElapsedTime;

@TeleOp
public class DriverControl extends LinearOpMode {
    private ElapsedTime runtime = new ElapsedTime(); //game timer

    private DcMotor leftFront;
    private DcMotor leftBack;
    private DcMotor rightFront;
    private DcMotor rightBack;

    private DcMotor liftL;
    private DcMotor liftR;
    private DcMotor manipM;

    private AnalogInput potentiometer;

    double v1, v2, v3, v4, liftLV, liftRV;
    double manipV = 0;

    @Override
    public void runOpMode()
    {
        init_motors();

        waitForStart();
        runtime.reset();

        while (opModeIsActive()) {
            runDrive();
            runLift();
            runManipulator();

            telemetry.addData("left front power", v1);
            telemetry.addData("left back power", v2);
            telemetry.addData("right front power", v3);
            telemetry.addData("right back power", v4);
            telemetry.addData("left lift power", liftLV);
            telemetry.addData("right lift power", liftRV);
            telemetry.addData("manipulator power", manipV);

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

        potentiometer = hardwareMap.analogInput.get("potentiometer");

        leftFront.setDirection(DcMotor.Direction.FORWARD);
        leftBack.setDirection(DcMotor.Direction.FORWARD);
        rightFront.setDirection(DcMotor.Direction.REVERSE);
        rightBack.setDirection(DcMotor.Direction.REVERSE);

        liftL.setDirection(DcMotor.Direction.REVERSE);
        liftR.setDirection(DcMotor.Direction.FORWARD);
        manipM.setDirection(DcMotor.Direction.FORWARD);

        telemetry.addData("Status", "Initialized");
        telemetry.update();
    }

    public void runDrive() {
        double driveV, strafeV, rotateV;

        driveV = -this.gamepad1.left_stick_y;
        strafeV = -this.gamepad1.left_stick_x;
        rotateV = this.gamepad1.right_stick_x;

        v1 = driveV + strafeV + rotateV;
        v2 = driveV - strafeV + rotateV;
        v3 = driveV + strafeV - rotateV;
        v4 = driveV - strafeV - rotateV;

        leftFront.setPower(v1);
        leftBack.setPower(v2);
        rightFront.setPower(v3);
        rightBack.setPower(v4);
    }

    public void runLift() {
        if (this.gamepad1.left_trigger != 0) {
            liftLV = this.gamepad1.left_trigger;
            liftRV = -this.gamepad1.left_trigger;
            liftL.setPower(liftLV);
            liftR.setPower(liftRV);
        }
        else {
            liftL.setPower(0);
            liftR.setPower(0);
        }
        if (this.gamepad1.right_trigger != 0) {
            liftLV = -this.gamepad1.right_trigger;
            liftRV = this.gamepad1.right_trigger;
            liftL.setPower(liftLV);
            liftR.setPower(liftRV);        }
        else {
            liftL.setPower(0);
            liftR.setPower(0);        }
    }

    public void runManipulator() {
        if (this.gamepad1.left_bumper) {
            manipV = 0.7;
            manipM.setPower(manipV);
        }
        else {
            manipV = 0;
            manipM.setPower(manipV);
        }
        if (this.gamepad1.right_bumper) {
            manipV = -0.7;
            manipM.setPower(manipV);
        }
        else {
            manipV = 0;
            manipM.setPower(manipV);
        }
    }
}