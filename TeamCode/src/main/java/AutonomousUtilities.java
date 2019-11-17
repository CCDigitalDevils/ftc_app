import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

public class AutonomousUtilities {
    private HardwareStrafe robot;
    private LinearOpMode linearOpMode;
    private ElapsedTime runtime;
    public AutonomousUtilities(){};
    public AutonomousUtilities (HardwareStrafe robot, LinearOpMode linearOpMode, ElapsedTime runtime){
        this.robot = robot;
        this.linearOpMode = linearOpMode;
        this.runtime = runtime;
    }
    public void strafe (double speed, double angle) {
        angle = angle + 45;
        double lFrR = Math.sin(Math.toRadians(angle));
        double rFlR = Math.cos(Math.toRadians(angle));
        lFrR = Range.clip(lFrR, 0, 1);
        rFlR = Range.clip(rFlR, 0, 1);
        if (linearOpMode.opModeIsActive()) {
            robot.Drive0.setPower(lFrR);
            robot.Drive1.setPower(rFlR);
            robot.Drive2.setPower(rFlR);
            robot.Drive3.setPower(lFrR);
        }
    }

        public void stopMotors () {
            robot.Drive0.setPower(0);
            robot.Drive1.setPower(0);
            robot.Drive2.setPower(0);
            robot.Drive3.setPower(0);
        }

        public void strafeTime ( double speed, double angle, double time){
            runtime.reset();
            while (linearOpMode.opModeIsActive() && (runtime.seconds() < time)) {
                strafe(speed, angle);
                linearOpMode.telemetry.addData("Path", "Leg: %2.5f S Elapsed", runtime.seconds());
                linearOpMode.telemetry.update();
            }
            stopMotors();
        }

    }