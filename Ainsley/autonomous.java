/**
 * linear opmode will run like in vex -- use if/if elses
 * opmode will run event based (ie uses a swtich/case setup -- called a state machine)
 */

package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

@Autonomous

public class autonomous extends OpMode {
    private ElapsedTime runtime = new ElapsedTime(); //game timer

    //motor objects -- why would you want these to be public?
    private DcMotor leftFront;
    private DcMotor leftBack;
    private DcMotor rightFront;
    private DcMotor rightBack;

    private DcMotor liftLeft;
    private DcMotor liftRight;

    @Override
    public void init() {
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
    }

    @Override
    public void start() {
        //could possibly also use telemetry for debugging purposes?
        telemetry.addData("Status", "Started");
        telemetry.update();

        super.start(); //not sure what this is exactly
        runtime.reset();

        //stuff
    }

    public void loop() {
        /**
         * can use if/else if statements like in vex
         * or can use switch/case statements which seems to be popular with other ftc teams
         */

        //could possibly be used for debug later
            telemetry.addData("Status", "Running");
            telemetry.addData("Status", "Run Time: " + runtime.toString());
            telemetry.update();
        }
    }
}
