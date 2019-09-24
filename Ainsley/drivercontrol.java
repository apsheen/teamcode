package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

@TeleOp
//will display this opmode in the driver station

public class drivercontrol extends LinearOpMode {
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
            double leftPower, rightPower = 0;

            //left stick to go forward, right stick to turn.
            double drive = -gamepad1.left_stick_y;
            double turn  =  gamepad1.right_stick_x;
            leftPower    = Range.clip(drive + turn, -1.0, 1.0) ;
            rightPower   = Range.clip(drive - turn, -1.0, 1.0) ;

            //one stick to control each wheel
            leftPower  = -gamepad1.left_stick_y ;
            rightPower = -gamepad1.right_stick_y ;

            double angle;

            /**
             * i think the d-pad for 90 deg motion is a good idea for now, just to see how to do it
             * and tank drive on the gamepads:
             *
             * code would be
             * if(gamepad1.dpad_left == true)
             * {
             *     //left sliding code
             * }
             *
             * else if(gamepad1.dpad_right == true)
             * {
             *     //right sliding code
             * }
             */

            /**
             * 4:00 9-15-19:
             * right -- rotating/angles
             * left -- 90 deg changes
             */

            /**if((gamepad1.left_stick_y != 0) && (gamepad1.right_stick_y == 0) && (gamepad1.right_stick_x == 0)) {

            }

            else if ((gamepad1.left_stick_x != 0) && (gamepad1.right_stick_y == 0) && (gamepad1.right_stick_x == 0)) {


            }
             **/

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
