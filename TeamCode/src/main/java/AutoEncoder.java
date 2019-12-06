import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

public class AutoEncoder {
    private HardwareStrafe robot;
    private LinearOpMode linearOpMode;
    private ElapsedTime runtime;

    public AutoEncoder(HardwareStrafe robot, LinearOpMode linearOpMode, ElapsedTime runtime) {
        this.robot = robot;
        this.linearOpMode = linearOpMode;
        this.runtime = runtime;
    }

    static final double COUNTS_PER_MOTOR_REV = 383.6;    // eg: TETRIX Motor Encoder
    static final double DRIVE_GEAR_REDUCTION = 27.4;     // This is < 1.0 if geared UP
    static final double WHEEL_DIAMETER_INCHES = 3.93701;     // For figuring circumference
    static final double COUNTS_PER_INCH = (COUNTS_PER_MOTOR_REV * DRIVE_GEAR_REDUCTION) /
            (WHEEL_DIAMETER_INCHES * 3.1415);

    public void encoderDrive(double speed, double Inches) {
        int newLeftTarget;
        int newRightTarget;


        // Ensure that the opmode is still active
        if (linearOpMode.opModeIsActive()) {

            // Determine new target position, and pass to motor controller
            newLeftTarget = robot.Drive0.getCurrentPosition() + (int) (Inches * COUNTS_PER_INCH);
            newRightTarget = robot.Drive1.getCurrentPosition() + (int) (Inches * COUNTS_PER_INCH);
            robot.Drive0.setTargetPosition(newLeftTarget);
            robot.Drive1.setTargetPosition(newRightTarget);

            // Turn On RUN_TO_POSITION
            robot.Drive0.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            robot.Drive1.setMode(DcMotor.RunMode.RUN_TO_POSITION);

            // reset the timeout time and start motion.
            robot.Drive0.setPower(Math.abs(speed));
            robot.Drive1.setPower(Math.abs(speed));
            robot.Drive2.setPower(Math.abs(speed));
            robot.Drive3.setPower(Math.abs(speed));

            while (linearOpMode.opModeIsActive() &&
                    (robot.Drive0.isBusy() || robot.Drive1.isBusy())) {

                // Display it for the driver.
                linearOpMode.telemetry.addData("Path1",  "Running to %7d :%7d", newLeftTarget,  newRightTarget);
                linearOpMode.telemetry.addData("Path2",  "Running at %7d :%7d",
                        robot.Drive0.getCurrentPosition(),
                        robot.Drive1.getCurrentPosition());
                linearOpMode.telemetry.update();
            }

            // Stop all motion;
            robot.Drive0.setPower(0);
            robot.Drive1.setPower(0);
            robot.Drive2.setPower(0);
            robot.Drive3.setPower(0);


            // Turn off RUN_TO_POSITION
            robot.Drive0.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            robot.Drive1.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        }
    }
}