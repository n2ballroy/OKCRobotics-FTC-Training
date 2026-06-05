package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.TouchSensor;

/**
 * TEACHING NOTE:
 * This OpMode demonstrates DC Motor control with Encoders, 
 * Edge Detection (Debouncing), and State Management.
 * 
 * KEY CONCEPTS:
 * 1. DC Motors & Encoders: Moving to specific "tick" counts instead of 0-1 range.
 * 2. Motor Run Modes: Using RUN_TO_POSITION to let the motor handle its own movement.
 * 3. Debouncing: Preventing a single press from being counted multiple times.
 * 4. State Machine: Using a counter and a Switch-Case to perform different actions.
 *
 * Key Structures:
 * 1. while loop
 * 2. global variables
 * 3. local variables
 */

@TeleOp(name = "Student Training OpMode", group = "Training")
public class StudentTrainingOpMode extends LinearOpMode {

    /* -------------------------------------------------------------------------
     * SETTINGS (Constants)
     * ------------------------------------------------------------------------- */
    // TEACHING NOTE: Unlike Servos (0 to 1), Motors use "Ticks". 
    // These numbers depend on your motor's gearbox.
    public static final int MOTOR_MIN_TICKS = 0;
    public static final int MOTOR_MAX_TICKS = 100;
    public static final int MOTOR_MID_TICKS = MOTOR_MAX_TICKS/2;
    
    public static final double MOTOR_POWER = 0.1; // Speed from 0.0 to 1.0

    public static final String MOTOR_NAME = "testMotor";
    public static final String TOUCH_NAME = "testTouch";

    /* -------------------------------------------------------------------------
     * HARDWARE & STATE VARIABLES
     * ------------------------------------------------------------------------- */
    private DcMotorEx trainingMotor;
    private TouchSensor trainingTouch;
    
    // Variables for Debouncing (Edge Detection)
    private boolean prevGamepadA = false;
    private boolean prevTouchState = false;
    private boolean prevGamepadB = false;

    // Variables for Logic State
    private boolean isMotorAtMax = false; // Used for the Toggle
    private int touchCount = 0;           // Counts how many times touch was pressed

    @Override
    public void runOpMode() {

        /* ---------------------------------------------------------------------
         * INITIALIZATION
         * --------------------------------------------------------------------- */
        trainingMotor = hardwareMap.get(DcMotorEx.class, MOTOR_NAME);
        trainingTouch = hardwareMap.get(TouchSensor.class, TOUCH_NAME);

        // TEACHING NOTE: Resetting the encoder is like "zeroing" a scale.
        // It makes the current physical position of the motor equal to 0.
        trainingMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        
        // We set the target to 0 and switch to RUN_TO_POSITION mode.
        trainingMotor.setTargetPosition(0);
        trainingMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        
        // In RUN_TO_POSITION, setPower sets the "Maximum Speed".
        trainingMotor.setPower(MOTOR_POWER);


        // TEACHING NOTE: Advanced Hardware Setting
        // You can reverse the direction of a motor if it's mounted "backwards".
        // trainingMotor.setDirection(DcMotor.Direction.REVERSE);

        telemetry.addData("Status", "Initialized. Encoders Reset.");
        telemetry.update();

        waitForStart();

        /* ---------------------------------------------------------------------
         * MAIN LOOP
         * --------------------------------------------------------------------- */
        while (opModeIsActive()) {

            // --- 1. DEBOUNCED TOGGLE (Gamepad A) ---
            if (gamepad1.a && !prevGamepadA) {
                if (isMotorAtMax) {
                    moveToPosition(MOTOR_MIN_TICKS);
                    isMotorAtMax = false;
                } else {
                    moveToPosition(MOTOR_MAX_TICKS);
                    isMotorAtMax = true;
                }
            }
            prevGamepadA = gamepad1.a;


            // --- 2. DEBOUNCED COUNTER (Touch Sensor) ---
            boolean currentTouch = isTouchPressed();
            if (currentTouch && !prevTouchState) {
                touchCount++;
            }
            prevTouchState = currentTouch;


            // --- 3. SWITCH-CASE ACTION (Gamepad B) ---
            if (gamepad1.b && !prevGamepadB) {
                
                switch (touchCount) {
                    case 1:
                        moveToPosition(MOTOR_MID_TICKS);
                        isMotorAtMax = false;
                        break;

                    case 2:
                        // Move to MAX, then back to MIN
                        performSweep(MOTOR_MIN_TICKS, MOTOR_MAX_TICKS, 10);
                        isMotorAtMax = false; 
                        break;

                    case 3:
                        // Move to MAX and stay there
                        moveToPosition(MOTOR_MAX_TICKS);
                        isMotorAtMax = true;
                        break;

                    default:
                        moveToPosition(MOTOR_MIN_TICKS);
                        isMotorAtMax = false;
                        break;
                }

                touchCount = 0; // Reset counter
            }
            prevGamepadB = gamepad1.b;

            /* -----------------------------------------------------------------
             * TELEMETRY
             * ----------------------------------------------------------------- */
            // TEACHING NOTE: Always show the TARGET (where we want to go) 
            // vs the ACTUAL position (where the motor is right now).
            telemetry.addData("Commanded Target", trainingMotor.getTargetPosition());
            telemetry.addData("Actual Position",  trainingMotor.getCurrentPosition());

            if (isMotorAtMax) {
                telemetry.addData("Motor State", "MAX");
            } else {
                telemetry.addData("Motor State", "MIN or MID");
            }
            
            telemetry.addData("Touch Count", touchCount);
            telemetry.addLine("-------------------");
            telemetry.addLine("A: Toggle Motor");
            telemetry.addLine("Touch: Increment Counter");
            telemetry.addLine("B: Run Action + Reset Counter");
            telemetry.addLine("Count=1 Move to Mid");
            telemetry.addLine("Count=2 sweep Min to Max");
            telemetry.addLine("Count=3 Move to Max");
            telemetry.addLine("Count=0 or >3 Move to Min");
            telemetry.update();
        }
    }

    /* -------------------------------------------------------------------------
     * METHODS
     * ------------------------------------------------------------------------- */

    private boolean isTouchPressed() {
        return trainingTouch.isPressed();
    }

    private void moveToPosition(int ticks) {
        // TEACHING NOTE: targetPosition tells the motor WHERE to go.
        trainingMotor.setTargetPosition(ticks);
    }

    /**
     * Moves the motor in steps.
     * TEACHING NOTE: This demonstrates why RUN_TO_POSITION is easier 
     * than manual looping for simple movements.
     */
    private void performSweep(int start, int end, int steps) {
        int currentTarget = start;
        int stepSize = (end - start) / steps;

        for (int i = 0; i <= steps; i++) {
            if (!opModeIsActive()) break;

            trainingMotor.setTargetPosition(currentTarget);
            currentTarget += stepSize;
            
            // Wait a little so the motor has time to reach the target
            sleep(100); 
            
            telemetry.addData("Sweep Status", "Moving to %d ticks", currentTarget);
            telemetry.update();
        }
        
        // Finally move back to the start
        trainingMotor.setTargetPosition(start);
    }
}
