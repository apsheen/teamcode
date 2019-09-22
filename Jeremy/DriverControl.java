package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;
import java.util.Math;

@TeleOp
//will display this opmode in the driver station

public class DriverControl extends LinearOpMode {
    private ElapsedTime runtime = new ElapsedTime(); //game timer

    Robot mRobot = new Robot();

    @Override
    public void runOpMode() {
        mRobot.init();
        debugProgram();

        waitForStart();
        runtime.reset();

        runProgramLoop();
    }

    public void setDirectionMotors() {
        leftFront.setDirection(DcMotor.Direction.FORWARD);
        leftBack.setDirection(DcMotor.Direction.FORWARD);
        rightFront.setDirection(DcMotor.Direction.FORWARD);
        rightBack.setDirection(DcMotor.Direction.FORWARD);

        liftLeft.setDirection(DcMotor.Direction.FORWARD);
        liftRight.setDirection(DcMotor.Direction.FORWARD);
    }

    public void debugProgram() {
        telemetry.addData("Status", "Initialized");
        telemetry.update();
    }

    public void runProgramLoop() {
        while (opModeIsActive()) {
            runMechanumAngleMethod();
//            runMechanumVectorMethod();
            telemetry.addData("Status", "Running");
            telemetry.addData("Status", "Run Time: " + runtime.toString());
            telemetry.update();
        }
    }

    public void setMotorPowers() {
        leftFront.setPower(v1);
        rightFront.setPower(v2);
        leftBack.setPower(v3);
        rightBack.setPower(v4);
    }

    public void runMechanumAngleMethod() {
        // getting the distance from the origin of the joystick
        double r = Math.hypot(this.gamepad1.left_stick_x, this.gamepad1.left_stick_y);
        // angle of the joystick position from the origing
        double robotAngle = Math.atan2(this.gamepad1.left_stick_y, this.gamepad1.left_stick_x) - Math.PI / 4;
        //
        double rightX = this.gamepad1.right_stick_x;
        // scale the magnitude by the x and y values of the left angle stick
        final double v1 = r * Math.cos(robotAngle) + rightX;
        final double v2 = r * Math.sin(robotAngle) - rightX;
        final double v3 = r * Math.sin(robotAngle) + rightX;
        final double v4 = r * Math.cos(robotAngle) - rightX;

        setMotorPowers();
    }

    public void runMechanumVectorMethod() {
        double driveV, strafeV, rotateV;
        driveV = -this.gamepad1.left_stick_x;
        strafeV = this.gamepad1.left_stick_x;
        rotateV = this.gamepad1.right_stick_x;

        final double v1 = driveV + strafeV + rotateV;
        final double v2 = driveV - strafeV + rotateV;
        final double v3 = driveV - strafeV - rotateV;
        final double v4 = driveV + strafeV - rotateV;

        setMotorPowers();
    }
}
