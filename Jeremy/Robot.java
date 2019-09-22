package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

public class Robot extends LinearOpMode {
    private ElapsedTime runtime = new ElapsedTime(); //game timer

    //motor objects -- private
    public DcMotor leftFront;
    public DcMotor leftBack;
    public DcMotor rightFront;
    public DcMotor rightBack;

    public DcMotor liftLeft;
    public DcMotor liftRight;

    public Robot() {

    }

    public void init() {
        initMotors();
        resetMotors();
    }

    public void initMotors() {
        leftFront = hardwareMap.get(DcMotor.class, "leftFront");
        leftBack = hardwareMap.get(DcMotor.class, "leftBack");
        rightFront = hardwareMap.get(DcMotor.class, "rightFront");
        rightBack = hardwareMap.get(DcMotor.class, "rightBack");

        liftLeft = hardwareMap.get(DcMotor.class, "liftLeft");
        liftRight = hardwareMap.get(DcMotor.class, "liftRight");
    }

    public void resetMotors() {
        leftFront.setDirection(DcMotor.Direction.FORWARD);
        leftBack.setDirection(DcMotor.Direction.FORWARD);
        rightFront.setDirection(DcMotor.Direction.FORWARD);
        rightBack.setDirection(DcMotor.Direction.FORWARD);

        liftLeft.setDirection(DcMotor.Direction.FORWARD);
        liftRight.setDirection(DcMotor.Direction.FORWARD);

        leftFront.setPower(0);
        rightFront.setPower(0);
        leftBack.setPower(0);
        rightBack.setPower(0);
    }

}
