package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.GyroSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;


/**
 * Default class for 7641 BettaFish autonomous robot
 */

public class robotAuto {
    private ElapsedTime runtime = new ElapsedTime(); //game timer

    /*
    Motors, sensors, and servos' declarations
     */
    public DcMotor leftFront;
    public DcMotor leftBack;
    public DcMotor rightFront;
    public DcMotor rightBack;
    public DcMotor liftL;
    public DcMotor liftR;
    public DcMotor manipM;
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

    public int leftFrontTarget, leftBackTarget, rightFrontTarget, rightBackTarget, manipTarget;
    public int distanceFromBridge;

    /* Constructor */
    public robotAuto() {

    }

    public void init_motors(HardwareMap hwMap) {
        leftFront = hwMap.get(DcMotor.class, "leftFront");
        leftBack = hwMap.get(DcMotor.class, "leftBack");
        rightFront = hwMap.get(DcMotor.class, "rightFront");
        rightBack = hwMap.get(DcMotor.class, "rightBack");

        leftFront.setDirection(DcMotor.Direction.FORWARD);
        leftBack.setDirection(DcMotor.Direction.FORWARD);
        rightFront.setDirection(DcMotor.Direction.REVERSE);
        rightBack.setDirection(DcMotor.Direction.REVERSE);

        leftFront.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        leftBack.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightFront.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightBack.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        leftFront.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        leftBack.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rightFront.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rightBack.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        manipM = hwMap.get(DcMotor.class, "manipM");
        manipM.setDirection(DcMotor.Direction.FORWARD);
        manipM.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        manipM.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        gyro = hwMap.gyroSensor.get("gyro");
        gyro.calibrate();
        gyro.resetZAxisIntegrator();
    }

    /*
    May condense encoderRun and manipRun into one function
     */
    public void encoderRun(String movementType, double vel, double leftShift, double rightShift,
                           double angle, boolean opModeRunning) {
        if (opModeRunning) {
            if (movementType == "strafe") {
                leftFrontTarget = leftFront.getCurrentPosition() - (int) (leftShift * COUNTS_PER_INCH);
                leftBackTarget = leftBack.getCurrentPosition() + (int) (leftShift * COUNTS_PER_INCH);
                rightFrontTarget = rightFront.getCurrentPosition() + (int) (rightShift * COUNTS_PER_INCH);
                rightBackTarget = rightBack.getCurrentPosition() - (int) (rightShift * COUNTS_PER_INCH);
            }
            else if (movementType == "drive") {
                leftFrontTarget = leftFront.getCurrentPosition() + (int) (leftShift * COUNTS_PER_INCH);
                leftBackTarget = leftBack.getCurrentPosition() + (int) (leftShift * COUNTS_PER_INCH);
                rightFrontTarget = rightFront.getCurrentPosition() + (int) (rightShift * COUNTS_PER_INCH);
                rightBackTarget = rightBack.getCurrentPosition() + (int) (rightShift * COUNTS_PER_INCH);
            }

            leftFront.setTargetPosition(leftFrontTarget);
            leftBack.setTargetPosition(leftBackTarget);
            rightFront.setTargetPosition(rightFrontTarget);
            rightBack.setTargetPosition(rightBackTarget);

            leftFront.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            leftBack.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            rightFront.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            rightBack.setMode(DcMotor.RunMode.RUN_TO_POSITION);

            runtime.reset();
            setMotorPowers(vel);

            while (leftFront.isBusy() && leftBack.isBusy() && rightFront.isBusy()
                   && rightBack.isBusy() && !onHeading(vel, angle, P_TURN_COEFF)) {

            }

            setMotorPowers(0);

            leftFront.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            leftBack.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            rightFront.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            rightBack.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        }
    }

    public void manipRun(String movementType, boolean opModeRunning) {
        double manipV = 0;

        if (opModeRunning) {
            if (movementType == "open") {
                manipTarget = manipM.getCurrentPosition() + 10;
                manipV = 0.1;
            }
            else if (movementType == "close") {
                manipTarget = manipM.getCurrentPosition() - 10;
                manipV = -0.1;
            }

            manipM.setTargetPosition(manipTarget);

            manipM.setMode(DcMotor.RunMode.RUN_TO_POSITION);

            runtime.reset();
            manipM.setPower(manipV);

            while ((runtime.seconds() < 1)) {

            }

            manipM.setPower(0);

            manipM.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        }
    }

    public void setMotorPowers(double vel) {
        leftFront.setPower(vel);
        leftBack.setPower(vel);
        rightFront.setPower(vel);
        rightBack.setPower(vel);
    }

    public void setLeftRightVelocities(double leftV, double rightV) {
        leftFront.setPower(leftV);
        leftBack.setPower(leftV);
        rightFront.setPower(rightV);
        rightBack.setPower(rightV);
    }

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

        setLeftRightVelocities(vel, -vel);

        return onTarget;
    }

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

    /*
    Returns desired steering force
     */
    public double getSteer(double error, double PCoeff) {
        return Range.clip(error * PCoeff, -1, 1);
    }
}