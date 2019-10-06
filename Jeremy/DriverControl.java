package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

@TeleOp
//will display this opmode in the driver station
public class DriverControl extends LinearOpMode {
    private ElapsedTime runtime = new ElapsedTime(); //game timer

    //motor objects -- private
    private DcMotor leftFront;
    private DcMotor leftBack;
    private DcMotor rightFront;
    private DcMotor rightBack;

    double v1, v2, v3, v4;
    double liftLeftV, liftRightV;

    private DcMotor liftLeft;
    private DcMotor liftRight;

    public void runMechanumVectorMethod() {
        double driveV, strafeV, rotateV;

        driveV = this.gamepad1.left_stick_y;
        strafeV = -this.gamepad1.left_stick_x;
        rotateV = this.gamepad1.right_stick_x;

        v1 = driveV + strafeV + rotateV;
        v2 = driveV - strafeV + rotateV;
        v3 = driveV - strafeV - rotateV;
        v4 = driveV + strafeV - rotateV;

        leftFront.setPower(v1 * v1);
        leftBack.setPower(v2 * v2);
        rightFront.setPower(v3 * v3);
        rightBack.setPower(v4 * v4);
    }

    public void runLift() {
        if (this.gamepad1.left_trigger != 0) {
            liftLeftV = this.gamepad1.left_trigger;
            liftRightV = -this.gamepad1.left_trigger;

            liftLeft.setPower(liftLeftV);
            liftRight.setPower(liftRightV);
        }
        if (this.gamepad1.left_stick_button) {
            liftLeft.setPower(0);
            liftRight.setPower(0);
        }
    }

    @Override
    public void runOpMode() {
        leftFront = hardwareMap.get(DcMotor.class, "leftFront");
        leftBack = hardwareMap.get(DcMotor.class, "leftBack");
        rightFront = hardwareMap.get(DcMotor.class, "rightFront");
        rightBack = hardwareMap.get(DcMotor.class, "rightBack");

        //liftLeft = hardwareMap.get(DcMotor.class, "liftLeft");
        //liftRight = hardwareMap.get(DcMotor.class, "liftRight");

        leftFront.setDirection(DcMotor.Direction.FORWARD);
        leftBack.setDirection(DcMotor.Direction.FORWARD);
        rightFront.setDirection(DcMotor.Direction.REVERSE);
        rightBack.setDirection(DcMotor.Direction.REVERSE);

        //liftLeft.setDirection(DcMotor.Direction.FORWARD);
        //liftRight.setDirection(DcMotor.Direction.FORWARD);

        telemetry.addData("Status", "Initialized");
        telemetry.update();

        waitForStart();
        runtime.reset();

        while (opModeIsActive()) {
            runMechanumVectorMethod();
//            runLift();

            telemetry.addData("Status", "Running");
            telemetry.addData("Status", "Run Time: " + runtime.toString());

            telemetry.addData("left front power", v1 * v1);
            telemetry.addData("left back power", v2 * v2);
            telemetry.addData("right front power", v3 * v3);
            telemetry.addData("right back power", v4 * v4);
            telemetry.addData("lift left power", liftLeftV);
            telemetry.addData("lift right power", liftRightV);

            telemetry.update();
        }
    }
}