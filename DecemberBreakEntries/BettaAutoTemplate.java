package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotor;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.util.ElapsedTime;

//import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;


/**
 * Default template for 7641 BettaFish autonomous robot
 */

public class BettaAutoTemplate {
    private ElapsedTime runtime = new ElapsedTime(); //game timer

    /*
    Motors, sensors, and servos' declarations
     */
    private DcMotor fl;
    private DcMotor fr;
    private DcMotor bl;
    private DcMotor br;
//    public DcMotor liftL;
//    public DcMotor liftR;
    private DcMotor manip;
    private BNO055IMU imu;
    //    public Servo fMover;

    /*
    Encoder parameters
    COUNTS_PER_INCH: Calculates how many encoder ticks per inch
     */
    public final double TRANSLATE_SPEED = .1;
    static final double COUNTS_PER_MOTOR_REV = 560;
    static final double WHEEL_DIAMETER_INCHES = 77.0 / 25.4;
    static final double COUNTS_PER_INCH = COUNTS_PER_MOTOR_REV / (WHEEL_DIAMETER_INCHES * Math.PI);

    /*
    IMU parameters
     */
    Orientation lastAngles = new Orientation();
    double globalAngle;
    double correction;

    /*
    Class parameters
     */
    private int leftFrontTarget, leftBackTarget, rightFrontTarget, rightBackTarget, manipTarget;
    public int distanceFromBridge;
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
        fl = hwMap.get(DcMotor.class, "leftFront");
        fr = hwMap.get(DcMotor.class, "rightFront");
        bl = hwMap.get(DcMotor.class, "leftBack");
        br = hwMap.get(DcMotor.class, "rightBack");

        // set drive train motors' directions
        fl.setDirection(DcMotor.Direction.FORWARD);
        bl.setDirection(DcMotor.Direction.FORWARD);
        fr.setDirection(DcMotor.Direction.REVERSE);
        br.setDirection(DcMotor.Direction.REVERSE);

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

        // set up manipulator config
        manip = hwMap.get(DcMotor.class, "manipM");
        manip.setDirection(DcMotor.Direction.FORWARD);
        manip.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        manip.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        // set up gyro parameters and calibrate
        BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();
        parameters.mode = BNO055IMU.SensorMode.IMU;
        parameters.angleUnit = BNO055IMU.AngleUnit.DEGREES;
        parameters.accelUnit = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC;
        parameters.loggingEnabled = false;
        imu = hwMap.get(BNO055IMU.class, "imu");
        imu.initialize(parameters);
    }

    /**
     May get rid of getCurrentPosition() if not necessary
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

        while (fl.isBusy() && bl.isBusy() && fr.isBusy()
                && br.isBusy()) {
            correction = checkDirection(angle);
            setMotorPowers(vel - correction, vel + correction);
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

        while (runtime.seconds() < 1 && manip.isBusy()) {

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
        double correction, angle, gain = .10;

        angle = getAngle();

        if (angle == targetAngle)
            correction = 0;             // no adjustment.
        else
            correction = -angle;        // reverse sign of angle for correction.

        correction = correction * gain;

        return correction;
    }
}