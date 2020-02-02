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
    private final double ninjaSpeedScale = .3;

    NinjaOperator gamepad1X = new NinjaOperator();
    NinjaOperator gamepad2X = new NinjaOperator();

    @Override
    public void runOpMode() {
        init_motors();

        waitForStart();
        runtime.reset();

        while (opModeIsActive()) {
            // ninja mode activation
            gamepad1X.update(this.gamepad1.x);
            gamepad2X.update(this.gamepad2.x);

            if (this.gamepad1X.onPress() || this.gamepad2X.onPress()) {
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
        fl = hardwareMap.get(DcMotor.class, "fl");
        bl = hardwareMap.get(DcMotor.class, "bl");
        fr = hardwareMap.get(DcMotor.class, "fr");
        br = hardwareMap.get(DcMotor.class, "br");

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
        fMoverR.setDirection(Servo.Direction.REVERSE);

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
        driveV = this.gamepad1.left_stick_y;
        strafeV = this.gamepad1.left_stick_x;
        rotateV = this.gamepad1.right_stick_x;

        v1 = driveV - strafeV + rotateV;
        v2 = driveV + strafeV + rotateV;
        v3 = driveV + strafeV - rotateV;
        v4 = driveV - strafeV - rotateV;

        if (mode) {
            v1 *= ninjaSpeedScale;
            v2 *= ninjaSpeedScale;
            v3 *= ninjaSpeedScale;
            v4 *= ninjaSpeedScale;
        }

        fl.setPower(v1);
        bl.setPower(v2);
        fr.setPower(v3);
        br.setPower(v4);

    }

    // gamepad1 trigger
    public void runTransfer() {
        if (this.gamepad1.left_trigger != 0) {
            transfer.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

            transfer.setPower(-this.gamepad1.left_trigger);
        }
        else if (this.gamepad1.right_trigger != 0) {
            transfer.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

            transfer.setPower(this.gamepad1.right_trigger);
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
        if (this.gamepad1.left_bumper) {
            manip.setPosition(.95);
            telemetry.addData("manip pos", .95);
        }
        else if (this.gamepad1.right_bumper) {
            manip.setPosition(.1);
            telemetry.addData("manip pos", .1);
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
        intakeV = -this.gamepad2.left_stick_y;

        intakeL.setPower(intakeV);
        intakeR.setPower(intakeV);
    }

    // gamepad2 trigger
    public void runLift() {
        if (this.gamepad2.left_trigger != 0) {
            liftV = this.gamepad2.left_trigger;

            // run w/o encoder
            lift.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

            lift.setPower(liftV);
        }
        else if (this.gamepad2.right_trigger != 0) {
            liftV = -this.gamepad2.right_trigger;

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
        if (this.gamepad2.right_bumper) {
            fPos = 0;
        }
        else if (this.gamepad2.left_bumper) {
            fPos = .7;
        }
        fMoverL.setPosition(fPos + .1);
        fMoverR.setPosition(fPos);
    }

    /*
    End gamepad2 sector
     */
}