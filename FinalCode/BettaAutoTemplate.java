package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import com.qualcomm.hardware.bosch.BNO055IMU;

import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;


/**
 * Default template for 7641 BettaFish autonomous robot
 */

public class BettaAutoTemplate {
    private ElapsedTime runtime = new ElapsedTime(); //game timer

    private ElapsedTime runtime2 = new ElapsedTime();
    /*
    Motors, sensors, and servos' declarations
     */
    private DcMotor fl;
    private DcMotor fr;
    private DcMotor bl;
    private DcMotor br;
    public Servo fMoverL;
    public Servo fMoverR;
    public DcMotor intakeL;
    public DcMotor intakeR;
//    public DcMotor lift;
    public DcMotor transfer;
    public Servo manip;

    private BNO055IMU imu;

    /*
    Encoder parameters
    COUNTS_PER_INCH: Calculates how many encoder ticks per inch
     */
    public final double TS = .35; // Translate speed
    static final double COUNTS_PER_MOTOR_REV = 560;
    static final double WHEEL_DIAMETER_INCHES = 77.0 / 25.4;
    static final double COUNTS_PER_INCH = COUNTS_PER_MOTOR_REV / (WHEEL_DIAMETER_INCHES * Math.PI);
    private int leftRev, rightRev;

    /*
    IMU parameters
     */
    Orientation lastAngles = new Orientation();
    double globalAngle;
    double correction;

    /*
    Class parameters
     */
    private int leftFrontTarget, leftBackTarget, rightFrontTarget, rightBackTarget;
    private HardwareMap hwMap;

    /** Constructors */
    public BettaAutoTemplate() {

    }

    /**
     * Sends motors, sensors, and servos to hardware map
     * @param ahwmap hardware map parameter
     */
    public void init_motors(HardwareMap ahwmap) {
        // copy hardware map reference
        hwMap = ahwmap;

        // initialize drive train motors
        fl = hwMap.get(DcMotor.class, "fl");
        fr = hwMap.get(DcMotor.class, "fr");
        bl = hwMap.get(DcMotor.class, "bl");
        br = hwMap.get(DcMotor.class, "br");

        // set drive train motors' directions
        fl.setDirection(DcMotor.Direction.REVERSE);
        bl.setDirection(DcMotor.Direction.REVERSE);
        fr.setDirection(DcMotor.Direction.FORWARD);
        br.setDirection(DcMotor.Direction.FORWARD);

        // reset encoder ticks of drive train
        fl.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        bl.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        fr.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        br.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        // drive train motors' mode: run using encoder
        fl.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        bl.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        fr.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        br.setMode(DcMotor.RunMode.RUN_USING_ENCODER);


        // initialize drive train motors
        intakeL = hwMap.get(DcMotor.class, "intakeL");
        intakeR = hwMap.get(DcMotor.class, "intakeR");

        // set intake motors' directions
        intakeL.setDirection(DcMotor.Direction.REVERSE);
        intakeR.setDirection(DcMotor.Direction.FORWARD);

        // reset encoder ticks of drive train
        intakeL.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        intakeR.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        // drive train motors' mode: run using encoder
        intakeL.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        intakeR.setMode(DcMotor.RunMode.RUN_USING_ENCODER);


        // initialize foundation mover servos
        fMoverL = hwMap.get(Servo.class, "fMoverL");
        fMoverR = hwMap.get(Servo.class, "fMoverR");

        // set foundation mover servos' directions
        fMoverL.setDirection(Servo.Direction.FORWARD);
        fMoverR.setDirection(Servo.Direction.REVERSE);

//        lift = hwMap.get(DcMotor.class, "lift");
//        lift.setDirection(DcMotor.Direction.FORWARD);

        manip = hwMap.get(Servo.class, "manip");
        manip.setDirection(Servo.Direction.FORWARD);

        transfer = hwMap.get(DcMotor.class, "transfer");
        transfer.setDirection(DcMotor.Direction.FORWARD);

        // set up gyro parameters and calibrate
        BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();
        parameters.mode = BNO055IMU.SensorMode.IMU;
        parameters.angleUnit = BNO055IMU.AngleUnit.DEGREES;
        parameters.accelUnit = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC;
        parameters.loggingEnabled = false;
        imu = hwMap.get(BNO055IMU.class, "imu");
        imu.initialize(parameters);
    }

    public void drive(String mT, double vel, double leftShift, double rightShift,
                           double angle) {
        leftRev = (int) (leftShift * COUNTS_PER_INCH);
        rightRev = (int) (rightShift * COUNTS_PER_INCH);

        // drive
        if (mT.equals("d")) {
            leftFrontTarget = fl.getCurrentPosition() + leftRev;
            leftBackTarget = bl.getCurrentPosition() + leftRev;
            rightFrontTarget = fr.getCurrentPosition() + rightRev;
            rightBackTarget = br.getCurrentPosition() + rightRev;

            // set target position and set mode run to position
            fl.setTargetPosition(leftFrontTarget);
            bl.setTargetPosition(leftBackTarget);
            fr.setTargetPosition(rightFrontTarget);
            br.setTargetPosition(rightBackTarget);

            fl.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            bl.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            fr.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            br.setMode(DcMotor.RunMode.RUN_TO_POSITION);

            runtime.reset();

            // run motors until reached target position
            while (fl.isBusy() && bl.isBusy() && fr.isBusy()
                    && br.isBusy()) {
                correction = checkDirection(angle);
                setMotorPowers(vel - correction, vel + correction);
                if (runtime.seconds() > 3) {
                    break;
                }
            }

            setMotorPowers(0);
        }
        else if (mT.equals("s")) {
            leftFrontTarget = fl.getCurrentPosition() - leftRev;
            leftBackTarget = bl.getCurrentPosition() + leftRev;
            rightFrontTarget = fr.getCurrentPosition() + rightRev;
            rightBackTarget = br.getCurrentPosition() - rightRev;

            // set target position and set mode run to position
            fl.setTargetPosition(leftFrontTarget);
            bl.setTargetPosition(leftBackTarget);
            fr.setTargetPosition(rightFrontTarget);
            br.setTargetPosition(rightBackTarget);

            fl.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            bl.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            fr.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            br.setMode(DcMotor.RunMode.RUN_TO_POSITION);

            runtime.reset();

            // run motors until reached target position
            while (fl.isBusy() && bl.isBusy() && fr.isBusy()
                    && br.isBusy()) {
                correction = checkDirection(angle);
                setMotorPowers(vel - correction, vel + correction);
                if (runtime.seconds() > 2) {
                    break;
                }
            }
            //epic coder montage
            fl.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            bl.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            fr.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            fl.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

            setMotorPowers(0);
        }
    }

    /**
     * Runs intake with encoders
     * @param movementType open or close
     */
    public void runIntake(String movementType) {
        if (movementType.equals("out")) {
            intakeL.setPower(1);
            intakeR.setPower(1);
        }
        else if (movementType.equals("in")) {
            intakeL.setPower(-1);
            intakeR.setPower(-1);
        }
        else if (movementType.equals("stop")) {
            intakeL.setPower(0);
            intakeR.setPower(0);
        }
    }

    /**
     * Runs foundation mover
     */
    public void runFoundationMover(String mT) {
        if (mT.equals("up")) {
            fMoverL.setPosition(.8);
            fMoverR.setPosition(.7);
        }
        else if (mT.equals("down")) {
            fMoverL.setPosition(.1);
            fMoverR.setPosition(0);
        }
    }

    public void runManipulator(String mT) {
        if (mT.equals("up")) {
            manip.setPosition(.95);
        }
        else if (mT.equals("down")) {
            manip.setPosition(.1);
        }
    }

    public void runTransfer(String mT) {
        runtime2.reset();

        if (mT.equals("out")) {
            while (runtime2.seconds() < 4) {
                transfer.setPower(-1);
            }
        }
        if (mT.equals("in")) {
            while (runtime2.seconds() < 4) {
                transfer.setPower(1);
            }
        }
    }

    /**
     * Overload function for all motor powers are set to one distinct velocity
     * Motor powers are set to one distinct velocity
     * @param vel all motors velocity
     */
    public void setMotorPowers(double vel) {
        fl.setPower(vel);
        bl.setPower(vel);
        fr.setPower(vel);
        br.setPower(vel);
    }

    /**
     * Overload function for left and right motor powers are set to two distinct velocities
     * @param leftV left motors velocity
     * @param rightV right motors velocity
     */
    public void setMotorPowers(double leftV, double rightV) {
        fl.setPower(leftV);
        bl.setPower(leftV);
        fr.setPower(rightV);
        br.setPower(rightV);
    }

    private double getAngle()
    {
        // We experimentally determined the Z axis is the axis we want to use for heading angle.
        // We have to process the angle because the imu works in euler angles so the Z axis is
        // returned as 0 to +180 or 0 to -180 rolling back to -179 or +179 when rotation passes
        // 180 degrees. We detect this transition and track the total cumulative angle of rotation.

        Orientation angles = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);

        double deltaAngle = angles.firstAngle - lastAngles.firstAngle;

        if (deltaAngle < -180)
            deltaAngle += 360;
        else if (deltaAngle > 180)
            deltaAngle -= 360;

        globalAngle += deltaAngle;

        lastAngles = angles;

        return globalAngle;
    }

    private double checkDirection(double targetAngle)
    {
        double correction, angle, gain = .05;

        angle = getAngle();

        if (angle == targetAngle)
            correction = 0;             // no adjustment.
        else
            correction = -angle;        // reverse sign of angle for correction.

        correction = correction * gain;

        return correction;
    }

    private final void sleep(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}