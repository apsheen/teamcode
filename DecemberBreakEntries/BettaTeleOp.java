package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

/**
 * gamepad1 joysticks: drive train ✔
 * gamepad2 joysticks: intake  ✔

 * gamepad1 trigger: transfer ✔
 * gamepad2 trigger: lift ✔

 * gamepad1 bumpers: manipulator ✔
 * gamepad2 bumpers: foundation mover ✔
 */

class NinjaOperator {
    public boolean curr;
    public boolean prev;

    public NinjaOperator(){
        curr = false;
        prev = false;
    }

    public boolean isPressed(){
        return curr;
    }

    public boolean onRelease(){
        return (!curr && prev);
    }

    public boolean onPress(){
        return (curr && !prev);
    }

    public void update(boolean a){
        prev = curr;
        curr = a;
    }
}

@TeleOp
public class BettaTeleOp extends LinearOpMode {
    private ElapsedTime runtime = new ElapsedTime(); //game timer

    /*
    Motors, sensors, and servos' declarations
     */
    public DcMotor fl, fr, bl, br;
    public DcMotor lift;
    public Servo manip;
    public Servo fMoverL, fMoverR;
    public DcMotor intakeL, intakeR;
    public DcMotor transfer;

    /*
    Teleop parameters
     */
    double v1, v2, v3, v4;  // drive train velocity
    double driveV, strafeV, rotateV;  // drive, strafe, and rotate vectors
    double liftV;  // lift velocity
    double intakeV;  // intake velocity
    double fPos;  // foundation mover position

    int liftTargPos; // lift target position
    int transferTargPos;

    boolean ninjaMode;

    NinjaOperator gamepad1X;
    NinjaOperator gamepad2X;

    @Override
    public void runOpMode() {
        init_motors();

        waitForStart();
        runtime.reset();

        while (opModeIsActive()) {
            // ninja mode activation
            gamepad1X.update(gamepad1.x);
            gamepad2X.update(gamepad2.x);

            if (gamepad1X.onPress() || gamepad2X.onPress()) {
                ninjaMode = !ninjaMode;
            }

            // run all mechanisms
            runDrive(ninjaMode);  //run drive train
            runLift();  //run lift
            runManipulator();  //run manipulator
            runFoundationMover();  //run foundation mover
            runIntake();  //run intake
            runTransfer();

            // debugging information
            telemetry.addData("ninja mode", ninjaMode);
            telemetry.addData("drive train power", v1);
            telemetry.addData("intake power", intakeV);
            telemetry.addData("lift power", liftV);
            telemetry.addData("f pos", fPos);

            telemetry.update();
        }
    }

    /**
     * Sends motors, sensors, and servos to hardware map
     */
    public void init_motors() {
        // drive train
        fl = hardwareMap.get(DcMotor.class, "leftFront");
        bl = hardwareMap.get(DcMotor.class, "leftBack");
        fr = hardwareMap.get(DcMotor.class, "rightFront");
        br = hardwareMap.get(DcMotor.class, "rightBack");

        fl.setDirection(DcMotor.Direction.REVERSE);
        bl.setDirection(DcMotor.Direction.REVERSE);
        fr.setDirection(DcMotor.Direction.FORWARD);
        br.setDirection(DcMotor.Direction.FORWARD);

        // lift and manip
        lift = hardwareMap.get(DcMotor.class, "lift");
        manip = hardwareMap.get(Servo.class, "manip");

        lift.setDirection(DcMotor.Direction.FORWARD);
        manip.setDirection(Servo.Direction.FORWARD);

        // intake
        intakeL = hardwareMap.get(DcMotor.class, "intakeL");
        intakeR = hardwareMap.get(DcMotor.class, "intakeR");

        intakeL.setDirection(DcMotor.Direction.REVERSE);
        intakeR.setDirection(DcMotor.Direction.FORWARD);

        // foundation mover
        fMoverL = hardwareMap.get(Servo.class, "fMoverL");
        fMoverR = hardwareMap.get(Servo.class, "fMoverR");

        fMoverL.setDirection(Servo.Direction.FORWARD);
        fMoverR.setDirection(Servo.Direction.FORWARD);

        // transfer
        transfer = hardwareMap.get(DcMotor.class, "transfer");

        transfer.setDirection(DcMotor.Direction.FORWARD);

        // status
        telemetry.addData("Status", "Initialized");
        telemetry.update();
    }

    /*
     Gamepad1 sector
     */

    // gamepad1 joysticks
    public void runDrive(boolean mode) {
        // values from joystick
        driveV = gamepad1.left_stick_y;
        strafeV = gamepad1.left_stick_x;
        rotateV = gamepad1.right_stick_x;

        v1 = driveV - strafeV + rotateV;
        v2 = driveV + strafeV + rotateV;
        v3 = driveV + strafeV - rotateV;
        v4 = driveV - strafeV - rotateV;

        if (mode) {
            v1 = Math.pow(v1, 3);
            v2 = Math.pow(v2, 3);
            v3 = Math.pow(v3, 3);
            v4 = Math.pow(v4, 3);
        }

        fl.setPower(v1);
        bl.setPower(v2);
        fr.setPower(v3);
        br.setPower(v4);

    }

    // gamepad1 trigger
    public void runTransfer() {
        if (gamepad1.left_trigger != 0) {
            transfer.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

            transfer.setPower(-gamepad1.left_trigger);
        }
        else if (gamepad1.right_trigger != 0) {
            transfer.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

            transfer.setPower(gamepad1.right_trigger);
        } else {
            transferTargPos = transfer.getCurrentPosition();

            // run to its current position
            transfer.setTargetPosition(transferTargPos); // set at same position

            // run w/ encoder
            transfer.setMode(DcMotor.RunMode.RUN_TO_POSITION);

            // make sure transfer stays in its current position
            if (transfer.getCurrentPosition() < transferTargPos) {
                transfer.setPower(.5);
            }
            else if (transfer.getCurrentPosition() > transferTargPos) {
                transfer.setPower(-.5);
            }
        }

    }

    // gamepad1 bumpers
    public void runManipulator() {
        if (gamepad1.left_bumper) {
            manip.setPosition(.85);
            telemetry.addData("manip pos", .85);
        }
        else if (gamepad1.right_bumper) {
            manip.setPosition(.25);
            telemetry.addData("manip pos", 25);
        }
    }

    /*
    End gamepad1 sector
     */


    /*
    Gamepad2 sector
     */

    // gamepad2 right stick
    public void runIntake() {
        intakeV = -gamepad2.left_stick_y;

        intakeL.setPower(intakeV);
        intakeR.setPower(intakeV);
    }

    // gamepad2 trigger
    public void runLift() {
        if (gamepad2.left_trigger != 0) {
            liftV = gamepad2.left_trigger;

            // run w/o encoder
            lift.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

            lift.setPower(liftV);
        }
        else if (gamepad2.right_trigger != 0) {
            liftV = -gamepad2.right_trigger;

            // run w/o encoder
            lift.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

            lift.setPower(liftV);
        } else {
            liftTargPos = lift.getCurrentPosition();

            // run to its current position
            lift.setTargetPosition(liftTargPos); // set at same position

            // run w/ encoder
            lift.setMode(DcMotor.RunMode.RUN_TO_POSITION);

            // make sure lift stays in its current position
            if (lift.getCurrentPosition() < liftTargPos) {
                lift.setPower(.5);
            }
            else if (lift.getCurrentPosition() > liftTargPos) {
                lift.setPower(-.5);
            }
        }
    }

    // gamepad2 bumpers
    public void runFoundationMover() {
        if (gamepad2.right_bumper) {
            fPos = 0;
        }
        else if (gamepad2.left_bumper) {
            fPos = 1;
        }
        fMoverL.setPosition(fPos);
        fMoverR.setPosition(fPos);
    }

    /*
    End gamepad2 sector
     */
}