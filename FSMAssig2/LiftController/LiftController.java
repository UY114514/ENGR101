/* Code for ENGR110 Assignment
 * Name:
 * Usercode:
 * ID:
 */

import ecs100.*;

import javax.print.DocFlavor;

/**
 * FSM Controller for a simulated Lift.
 * The core of the controller is the signal(String sensor) method
 * which is called by the lift every time a sensor
 * is signalled.
 * <p>
 * Note, when your controller is started, the lift will be on floor 1 with the doors closed.
 * You should set the initial state of the controller to match this.
 */
public class LiftController {

    /**
     * The field that stores the current state of the FSM
     */
    private String  state = "1F";     // initial state should be at floor 1 with the door closed.
    // Note, you probably want to "factor" the state by having additional variables
    // to represent aspects of the state
    /*# YOUR CODE HERE */
//    private boolean liftAt1F = false;
//    private boolean liftAt2F = false;
//    private boolean liftAt3F = false;
    private boolean r1    = false;
    private boolean r2    = false;
    private boolean r3    = false;

    private boolean from1 = false;
    private boolean from3 = false;

    int liftWaitTime      = 5000;
    int liftExtraWaitTime = 1000;

    /**
     * The field containing the Lift.
     * The signal method will call methods on the lift to operate it
     */
    private Lift lift;  // the lift that is being controlled.

    // possible actions on the lift that you can perform:
    // lift.moveUp()             to start the lift moving up
    // lift.moveDown()           to start the lift moving down
    // lift.stop()               to stop the lift
    // lift.openDoor()           to start the doors opening
    // lift.closeDoor()          to start the doors closing
    // lift.restartTimer(1000)   to set the time for 1000 milliseconds
    // lift.turnWarningLightOn() to turn the warning light on
    // lift.turnWarningLightOff()to turn the warning light off


    /**
     * The Constructor is passed the lift that it is controlling.
     */
    public LiftController(Lift lift) {
        this.lift = lift;
    }


    /**
     * Receives a change in a sensor value that may trigger an action and state change.
     * If there is a transition out of the current state associated with this
     * sensor signal,
     * - it will perform the appropriate action (if any)
     * - it will transition to the next state
     * (by calling changeToState with the new state).
     * <p>
     * Possible sensor values that you can respond to:
     * (note, you may not need to respond to all of them)
     * "request1"   "request2"   "request3"
     * "atF1"   "atF2"   "atF3"
     * "startUp"   "startDown"
     * "doorClosed"   "doorOpened"   "doorMoving"
     * "timerExpired"
     * "doorSensor"
     * "withinCapacity"   "overCapacity"
     * <p>
     * You can either have one big method, or you can break it up into
     * a separate method for each state
     */
    public void signal(String sensor) {
        String OVERLOAD = "overCapacity";
        String WITHIN = "withinCapacity";
        String OPENED = "doorOpened";
        String CLOSED = "doorClosed";
        String REQUEST1 = "request1";
        String REQUEST2 = "request2";
        String REQUEST3 = "request3";
        String DOORSENSOR = "doorSensor";
        String TIMEREXPIRED = "timerExpired";


        UI.printf("In state: %s, got sensor: %s%n", state, sensor);
        /*# YOUR CODE HERE */
        if (state.equals("1F")) {
            if (sensor.equals(REQUEST1)) {
                lift.openDoor();
                state = "1F_opening";
            } else if (sensor.equals(REQUEST2)) {
                r2 = true;
                lift.moveUp();
                state = "UP";
            } else if (sensor.equals(REQUEST3)) {
                r3 = true;
                lift.moveUp();
                state = "UP";
            }
        } else if (state.equals("1F_opening")) {
            r1 = false;
            if (sensor.equals(REQUEST1)) {
                r1 = true;
                state = "1F_opening";
            } else if (sensor.equals(REQUEST2)) {
                r2 = true;
            } else if (sensor.equals(REQUEST3)) {
                r3 = true;
            }
            if (sensor.equals(OPENED)) {
                lift.restartTimer(liftWaitTime);
                state = "1F_loading";
            }

        } else if (state.equals("1F_loading")) {
            if (sensor.equals(REQUEST1)) r1 = true;
            else if (sensor.equals(REQUEST2)) r2 = true;
            else if (sensor.equals(REQUEST3)) r3 = true;
            if (sensor.equals(TIMEREXPIRED)) {
                lift.closeDoor();
                state = "1F_closing";
            } else if (sensor.equals(OVERLOAD)) {
                lift.openDoor();
                lift.turnWarningLightOn();
                state = "1F_overload";
            }
        } else if (state.equals("1F_overload")) {
            if (sensor.equals(REQUEST1)) r1 = true;
            else if (sensor.equals(REQUEST2)) r2 = true;
            else if (sensor.equals(REQUEST3)) r3 = true;
            if (sensor.equals(WITHIN)) {
                lift.turnWarningLightOff();
                lift.closeDoor();
                state = "1F_closing";
            }
        } else if (state.equals("1F_closing")) {
            if (sensor.equals(REQUEST1)) {
                r1 = true;
                lift.openDoor();
                state = "1F_opening";
            } else if (sensor.equals(REQUEST2)) r2 = true;
            else if (sensor.equals(REQUEST3)) r3 = true;
            if (sensor.equals(CLOSED) && (r2 || r3)) {
                lift.moveUp();
                from1 = true;
                from3 = false;
                state = "UP";
            } else if (sensor.equals(CLOSED)) {
                state = "1F";
            }
        } else if (state.equals("UP")) {
            if (sensor.equals(REQUEST1)) r1 = true;
            else if (sensor.equals(REQUEST2)) r2 = true;
            else if (sensor.equals(REQUEST3)) r3 = true;
            if (sensor.equals("atF3") && r3) {
                lift.stop();
                lift.openDoor();
                state = "3F_opening";
            } else if (sensor.equals("atF2") && r2) {
                lift.stop();
                lift.openDoor();
                state = "2F_opening";
            }
        } else if (state.equals("2F")) {
            if (sensor.equals(REQUEST1)) {
                r1 = true;
                lift.moveDown();
                state = "DOWN";
            } else if (sensor.equals(REQUEST2)) {
                lift.openDoor();
                r2 = true;
                state = "2F_opening";
            } else if (sensor.equals(REQUEST3)) {
                r3 = true;
                lift.moveUp();
                state = "UP";
            }
        } else if (state.equals("2F_opening")) {
            r2 = false;

            if (sensor.equals(OPENED)) {
                lift.restartTimer(liftWaitTime);
                state = "2F_loading";
            }
        } else if (state.equals("2F_loading")) {
            if (sensor.equals(REQUEST1)) r1 = true;
            else if (sensor.equals(REQUEST2)) {
                r2 = true;
                lift.openDoor();
                state = "2F_opening";
            } else if (sensor.equals(REQUEST3)) r3 = true;
            if (sensor.equals(OVERLOAD)) {
                lift.openDoor();
                lift.turnWarningLightOn();
                state = "2F_overload";
            } else if (sensor.equals(TIMEREXPIRED)) {
                lift.closeDoor();
                state = "2F_closing";
            }
        } else if (state.equals("2F_overload")) {
            if (sensor.equals(REQUEST1)) r1 = true;
            else if (sensor.equals(REQUEST2)) {
                r2 = true;
//                lift.openDoor();
//                state = "2F_opening";
            }
            else if (sensor.equals(REQUEST3)) r3 = true;
            if (sensor.equals(WITHIN)) {
                lift.turnWarningLightOff();
                lift.closeDoor();
                state = "2F_closing";
            }
        } else if (state.equals("2F_closing")) {

            if (sensor.equals(REQUEST1)) r1 = true;
            else if (sensor.equals(REQUEST2)) {
                r2 = true;
                lift.openDoor();
                state = "2F_opening";
            } else if (sensor.equals(REQUEST3)) r3 = true;
            if (sensor.equals(CLOSED) && r3 && !from3) {
                lift.moveUp();
                state = "UP";
            } else if (sensor.equals(CLOSED) && r1 && !from1) {
                lift.moveDown();
                state = "DOWN";
            } else if (sensor.equals(CLOSED)) {
                state = "2F";
            }
        } else if (state.equals("DOWN")) {

            if (sensor.equals(REQUEST1)) r1 = true;
            else if (sensor.equals(REQUEST2)) r2 = true;
            else if (sensor.equals(REQUEST3)) r3 = true;
            if (sensor.equals("atF2") && r2) {
                lift.stop();
                lift.openDoor();
                state = "2F_opening";
            } else if (sensor.equals("atF1") && r1) {
                lift.stop();
                lift.openDoor();
                state = "1F_opening";
            }
        } else if (state.equals("3F")) {
            if (sensor.equals(REQUEST1)) {
                lift.moveDown();
                r1 = true;
                state = "DOWN";
            } else if (sensor.equals(REQUEST2)) {
                lift.moveDown();
                r2 = true;
                state = "DOWN";
            } else if (sensor.equals(REQUEST3)) {
                lift.openDoor();
                r3 = true;
                state = "3F_opening";
            }
        } else if (state.equals("3F_opening")) {
            r3 = false;
            if (sensor.equals(REQUEST1)) r1 = true;
            else if (sensor.equals(REQUEST2)) r2 = true;
            if (sensor.equals(OPENED)) {
                lift.restartTimer(liftWaitTime);
                state = "3F_loading";
            }
        } else if (state.equals("3F_loading")) {
            if (sensor.equals(REQUEST1)) r1 = true;
            else if (sensor.equals(REQUEST2)) r2 = true;
//            else if (sensor.equals(REQUEST3)) r3 = true;
            if (sensor.equals(OVERLOAD)) {
                lift.turnWarningLightOn();
                lift.openDoor();
                state = "3F_overload";
            } else if (sensor.equals(TIMEREXPIRED)) {
                lift.closeDoor();
                state = "3F_closing";
            }
        } else if (state.equals("3F_overload")) {
            if (sensor.equals(REQUEST1)) r1 = true;
            else if (sensor.equals(REQUEST2)) r2 = true;
//            else if (sensor.equals(REQUEST3)) r3 = true;
            if (sensor.equals(WITHIN)) {
                lift.turnWarningLightOff();
                lift.closeDoor();
                state = "3F_closing";
            }
        } else if (state.equals("3F_closing")) {
            from1 = false;
            from3 = true;
            if (sensor.equals(REQUEST1)) r1 = true;
            else if (sensor.equals(REQUEST2)) r2 = true;
            else if (sensor.equals(REQUEST3)) {
                r3 = true;
                lift.openDoor();
                state = "3F_opening";
            }
            if (sensor.equals(CLOSED) && (r2 || r1)) {
                lift.moveDown();
                state = "DOWN";
            } else if (sensor.equals(CLOSED)) {
                state = "3F";
            }
        }
    }
}

        