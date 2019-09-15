package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

@TeleOp
@Autonomous

public class autonomous extends LinearOpMode {
    private ElapsedTime runtime = new ElapsedTime(); //game timer

    //motor objects -- private
    private DcMotor leftFront;
    private DcMotor leftBack;
    private DcMotor rightFront;
    private DcMotor rightBack;

    private DcMotor liftLeft;
    private DcMotor liftRight;

    @Override
    public void runOpMode() {
        //initializing the motors
        leftFront = hardwareMap.get(DcMotor.class, "leftFront");
        leftBack = hardwareMap.get(DcMotor.class, "leftBack");
        rightFront = hardwareMap.get(DcMotor.class, "rightFront");
        rightBack = hardwareMap.get(DcMotor.class, "rightBack");

        liftLeft = hardwareMap.get(DcMotor.class, "liftLeft");
        liftRight = hardwareMap.get(DcMotor.class, "liftRight");

        //may need to change directions
        leftFront.setDirection(DcMotor.Direction.FORWARD);
        leftBack.setDirection(DcMotor.Direction.FORWARD);
        rightFront.setDirection(DcMotor.Direction.FORWARD);
        rightBack.setDirection(DcMotor.Direction.FORWARD);

        liftLeft.setDirection(DcMotor.Direction.FORWARD);
        liftRight.setDirection(DcMotor.Direction.FORWARD);

        //could possibly also use telemetry for debugging purposes?
        telemetry.addData("Status", "Initialized");
        telemetry.update();

        // Wait for the game to start (driver presses PLAY)
        waitForStart();
        runtime.reset(); //start game timer

        // run until the end of the match (driver presses STOP)
        while (opModeIsActive()) {
            //two wheel drive
            double leftPower = 0; //0 is placeholder
            double rightPower = 0; //0 is placeholder

            //yes negative power is a thing for going backwards sorry for the false info
            leftFront.setPower(leftPower);
            leftBack.setPower(leftPower);
            rightFront.setPower(rightPower);
            rightBack.setPower(rightPower);

            //could possibly be used for debug later
            telemetry.addData("Status", "Running");
            telemetry.addData("Status", "Run Time: " + runtime.toString());
            telemetry.update();

        }
    }
}
