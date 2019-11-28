package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.GyroSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

/**
 * Default template for 7641 BettaFish autonomous robot
 */

public class BettaAutoTemplate {
    private ElapsedTime runtime = new ElapsedTime(); //game timer

    /*
    Motors, sensors, and servos' declarations
     */
    public DcMotor fl;
    public DcMotor fr;
    public DcMotor bl;
    public DcMotor br;
    public DcMotor liftL;
    public DcMotor liftR;
    public DcMotor manip;
    public GyroSensor gyro;
//    public Servo fMover;

    /*
    Encoder parameters
    COUNTS_PER_INCH: Calculates how many encoder ticks per inch
     */
    static final double TRANSLATE_SPEED = 1;
    static final double COUNTS_PER_MOTOR_REV = 1120;
    static final double WHEEL_DIAMETER_INCHES = 4.0;
    static final double COUNTS_PER_INCH = COUNTS_PER_MOTOR_REV / (WHEEL_DIAMETER_INCHES * Math.PI);

    /*
    Gyro parameters
    HEADING_THRESHOLD: how much offset we want from target angle
    P_TURN_COEFF: proportional gain coeffecient
     */
    static final double HEADING_THRESHOLD = 1;
    static final double P_TURN_COEFF = 0.1;

    /*
    Class parameters
     */
    public int leftFrontTarget, leftBackTarget, rightFrontTarget, rightBackTarget, manipTarget;
    public int distanceFromBridge;
    HardwareMap hwMap;

    /** Constructors */
    public BettaAutoTemplate() {

    }

    /**
     * Sends motors, sensors, and servos to hardware map
     * @param ahwmap hardware map parameter
     */
    public void init_motors(HardwareMap ahwmap) {
        hwMap = ahwmap;

        fl = hwMap.get(DcMotor.class, "leftFront");
        fr = hwMap.get(DcMotor.class, "rightFront");
        bl = hwMap.get(DcMotor.class, "leftBack");
        br = hwMap.get(DcMotor.class, "rightBack");

        fl.setDirection(DcMotor.Direction.FORWARD);
        bl.setDirection(DcMotor.Direction.FORWARD);
        fr.setDirection(DcMotor.Direction.REVERSE);
        br.setDirection(DcMotor.Direction.REVERSE);

        fl.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        bl.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        fr.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        br.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        fl.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        bl.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        fr.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        br.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        manip = hwMap.get(DcMotor.class, "manipM");
        manip.setDirection(DcMotor.Direction.FORWARD);
        manip.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        manip.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        gyro = hwMap.gyroSensor.get("gyro");
        gyro.calibrate();
        gyro.resetZAxisIntegrator();
    }

    /**
     May condense encoderRun and manipRun into one function
     May get rid of getCurrentPosition() if not necessary
     May replace && with ||
     */
    public void encoderRun(String movementType, double vel, double leftShift, double rightShift,
                           double angle) {
        if (movementType.equals("strafe")) {
            leftFrontTarget = fl.getCurrentPosition() - (int) (leftShift * COUNTS_PER_INCH);
            leftBackTarget = bl.getCurrentPosition() + (int) (leftShift * COUNTS_PER_INCH);
            rightFrontTarget = fr.getCurrentPosition() + (int) (rightShift * COUNTS_PER_INCH);
            rightBackTarget = br.getCurrentPosition() - (int) (rightShift * COUNTS_PER_INCH);
        }
        else if (movementType.equals("drive")) {
            leftFrontTarget = fl.getCurrentPosition() + (int) (leftShift * COUNTS_PER_INCH);
            leftBackTarget = bl.getCurrentPosition() + (int) (leftShift * COUNTS_PER_INCH);
            rightFrontTarget = fr.getCurrentPosition() + (int) (rightShift * COUNTS_PER_INCH);
            rightBackTarget = br.getCurrentPosition() + (int) (rightShift * COUNTS_PER_INCH);
        }

        fl.setTargetPosition(leftFrontTarget);
        bl.setTargetPosition(leftBackTarget);
        fr.setTargetPosition(rightFrontTarget);
        br.setTargetPosition(rightBackTarget);

        fl.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        bl.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        fr.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        br.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        runtime.reset();
        setMotorPowers(vel);

        while (fl.isBusy() && bl.isBusy() && fr.isBusy()
                && br.isBusy() && !onHeading(vel, angle, P_TURN_COEFF)) {

        }

        setMotorPowers(0);

        fl.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        bl.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        fr.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        br.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    /**
     * Runs manipulator with encoders
     * @param movementType open or close
     */
    public void manipRun(String movementType) {
        double manipV = 0;

        if (movementType.equals("open")) {
            manipTarget = manip.getCurrentPosition() + 10;
            manipV = 0.1;
        }
        else if (movementType.equals("close")) {
            manipTarget = manip.getCurrentPosition() - 10;
            manipV = -0.1;
        }

        manip.setTargetPosition(manipTarget);

        manip.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        runtime.reset();
        manip.setPower(manipV);

        while ((runtime.seconds() < 1)) {

        }

        manip.setPower(0);

        manip.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    /**
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
     * Override function for left and right motor powers are set to two distinct velocities
     * @param leftV left motors velocity
     * @param rightV right motors velocity
     */
    public void setMotorPowers(double leftV, double rightV) {
        fl.setPower(leftV);
        bl.setPower(leftV);
        fr.setPower(rightV);
        br.setPower(rightV);
    }

    /**
     *
     * @param speed robot current speed
     * @param angle robot target angle
     * @param PCoeff proportional gain coefficient
     * @return returns whether robot is at the right angle or not
     */
    public boolean onHeading(double speed, double angle, double PCoeff) {
        double error;
        double steer;
        boolean onTarget = false;
        double vel;

        error = getError(angle);

        if (Math.abs(error) <= HEADING_THRESHOLD) {
            steer = 0.0;
            vel = 0.0;
            onTarget = true;
        }
        else {
            steer = getSteer(error, PCoeff);
            vel = speed * steer;
        }

        setMotorPowers(vel, -vel);

        return onTarget;
    }

    /**
     *
     * @param targetAngle desired robot angle to turn in
     * @return returns robot angle error
     */
    public double getError(double targetAngle) {
        double robotError;

        robotError = targetAngle - gyro.getHeading();

        while (robotError > 180) {
            robotError -= 360;
        }
        while (robotError <= -180){
            robotError += 360;
        }

        return robotError;
    }

    /**
     *
     * @param error
     * @param PCoeff
     * @return returns desires steer force
     */
    public double getSteer(double error, double PCoeff) {
        return Range.clip(error * PCoeff, -1, 1);
    }
}