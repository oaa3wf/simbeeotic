/*
 * Copyright (c) 2012, The President and Fellows of Harvard College.
 * All Rights Reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions
 *  are met:
 *
 *  1. Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *  3. Neither the name of the University nor the names of its contributors
 *     may be used to endorse or promote products derived from this software
 *     without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE UNIVERSITY AND CONTRIBUTORS ``AS IS'' AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED.  IN NO EVENT SHALL THE UNIVERSITY OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS
 * OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
 * OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 */
package harvard.robobees.simbeeotic.model;


import com.bulletphysics.linearmath.Transform;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import harvard.robobees.simbeeotic.SimEngine;
import harvard.robobees.simbeeotic.configuration.ConfigurationAnnotations.GlobalScope;
import harvard.robobees.simbeeotic.model.sensor.PoseSensor;
import harvard.robobees.simbeeotic.model.sensor.PositionSensor;
import harvard.robobees.simbeeotic.util.MathUtil;
import harvard.robobees.simbeeotic.util.MedianPIDController;
import org.apache.log4j.Logger;

import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * A base class that implements a simple movement abstraction on top of the
 * raw helicopter control. The abstraction provides a mechanism to move to an
 * arbitrary point in 3D space, reorient in place, and hover in place.
 *
 * @author bkate
 * @author kar
 */
public abstract class BaseAutoHeliBehavior implements HeliBehavior {

    protected SimEngine simEngine;
    
    // state
    private Timer controlTimer;
    private Vector3f lastPos = new Vector3f();
    private Quat4f lastPose = new Quat4f();
    private long lastTime = 0;
    private MoveState currState = MoveState.IDLE;
    private Vector3f currTarget = new Vector3f();
    private double currEpsilon = 0.1;        // in meters
    private MoveCallback currMoveCallback;
    private List<AbstractHeli> allHelis;
    private Vector3f calcTarget;
    private Vector3f rVec;                   // repulsive vector for obst. avoidance
    private int myHeliId;
    private Vector3f landingSpot;            // where this helicopter should land
    private Vector3f hiveLocation;           // the center of the hive
    private double hiveRadius = 0.55;        // in meters; calculated later
    private BufferedWriter logWriter;

    private HeliControl control;
    private Platform platform;
    private PositionSensor posSensor;
    private PoseSensor orientSensor;
    private HeliDataStruct heliData;

    // controllers and set points
    private MedianPIDController throttlePID;
    private MedianPIDController pitchPID;
    private MedianPIDController rollPID;
    private MedianPIDController yawPID;
    private double[] yawDiffs;
    private int yawHistPtr;
    private double currYaw = 0, prevYaw = 0, idYaw = 0, fHeading = 0;
    private boolean goodOrientation = false;
    private double prevRoll = 0, prevPitch = 0;

    private double pitchSetPoint = 0, yawSetpoint = 0.0;  // radians

    // data logging parameters
    private boolean logData = true;
    private String logPath = "./heli_log.txt";

    private enum MoveState {IDLE, TAKEOFF, HOVER, RUN, MOVE, LAND, TERMINATED}

    private static Logger logger = Logger.getLogger(BaseAutoHeliBehavior.class);

//    private static final long CONTROL_LOOP_PERIOD = 10;           // ms (50 Hz)
//    private static final float COLLISION_BUFF = 1.0f;             // m
//    private static final float COLLISION_BUFF_HIVE = 0.4f;        // m
//    private static final float BOUNDARY_BUFF = 0.5f;              // m, whithin which we will avoid walls
//    private static final float DESTINATION_EPSILON = 0.3f;        // m
//    private static final float SLOWDOWN_DISTANCE = 0.8f;          // m
//    private static final float FLYING_ALTITUDE = 0.1f;            // m, below which we do not consider obstacles
    private static final float LANDING_EPSILON = 0.3f;            // m
    private static final float LANDING_ALTITUDE = 0.15f;          // m, below which we can drop
    private static final float LANDING_STAGING_ALTITUDE = 0.5f;   // m
//    private static final long LANDING_STAGING_TIME = 1;           // s
    private static final float TAKEOFF_ALTITUDE = 0.25f;
    private static final float TURNING_CIRCLE_RADIUS = 0.5f;        // don't update heading when inside this distance to target


    @Override
    public void start(final Platform platform, final HeliControl control, final Boundary bounds) {

        this.platform = platform;
        this.control = control;

        if (logData) {

            try {
                logWriter = new BufferedWriter(new FileWriter(logPath));
            }
            catch(IOException e) {
                throw new RuntimeException("Could not open log output file: " + logPath, e);
            }
        }

        allHelis = simEngine.findModelsByType(AbstractHeli.class);
        myHeliId = control.getHeliId();

        SimpleHive hive = simEngine.findModelByType(SimpleHive.class);

        if (hive == null) {
            throw new RuntimeException("A hive must be present and initialized before any helicopters.");
        }

        hiveLocation = hive.getTruthPosition();
        hiveRadius = hive.getSize() / 2;
        landingSpot = calcHiveLocation();

        posSensor = platform.getSensor("position-sensor", PositionSensor.class);
        orientSensor = platform.getSensor("pose-sensor", PoseSensor.class);
        heliData = new HeliDataStruct();

        if (posSensor == null) {
            throw new RuntimeModelingException("A position sensor is needed for BaseAutoHeliBehavior.");
        }

        if (orientSensor == null) {
            throw new RuntimeModelingException("A pose sensor is needed for the BaseAutoHeliBehavior.");
        }

        throttlePID = new MedianPIDController(0.0, 0.4, 0.4, 0.2, 0.5, 0.25, 1.0);
        pitchPID = new MedianPIDController(0.0, 0.6, 0.1, 0.2, 0.1, 1.0, 1.0);
        rollPID = new MedianPIDController(0.0, 0.6, 0.1, 0.1, 0.1, 1.0, 1.0);
        yawPID = new MedianPIDController(0.0, 0.3, 0.0, 0.0, 0.1, 1.0, 1.0);

        // send an inital command to the heli to put in a neutral state
        control.setThrust(control.getThrustTrim());
        control.setPitch(control.getPitchTrim());
        control.setRoll(control.getRollTrim());
        control.setYaw(control.getYawTrim());
        control.sendCommands();

        yawDiffs = new double[3];
        yawHistPtr = 0;

        // a timer that implements the low level control of the heli altitude and bearing
        controlTimer = new Timer();

        final long firstTime = System.currentTimeMillis();

         controlTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run(){
                
            	Vector3f pos = posSensor.getPosition();
                Quat4f pose = orientSensor.getPose();

                Vector3f euler = MathUtil.quaternionToEulerZYX(pose);

                goodOrientation = ( (Math.abs(euler.z) > 0.001) && ((Math.PI - Math.abs(euler.z)) > 0.001) );

                while( !goodOrientation ) {
                    pose = orientSensor.getPose();
                    euler = MathUtil.quaternionToEulerZYX(pose);
                    goodOrientation = ( (Math.abs(euler.z) > 0.001) && ((Math.PI - Math.abs(euler.z)) > 0.001) );

//                    System.out.println("Dropping orientation!");
                }

                long realTime = System.currentTimeMillis();

                if((realTime - lastTime) == 0)
                    return;
                logModelData(currState, lastPos, pos, lastPose, pose, (realTime - firstTime)/1000.0f, (realTime - lastTime) / 1000.0f);
                
                lastPos = pos;
                lastPose = pose;
                lastTime = realTime;

                fHeading = filterHeading(euler.z);

//                System.out.println("Filtered heading: " + fHeading);

                double targetWX = currTarget.getX()-pos.getX();
                double targetWY = currTarget.getY()-pos.getY();

                double targetBX = 1 * (targetWX * Math.cos(-fHeading) + targetWY * -Math.sin(-fHeading));
                double targetBY = 1 * (targetWX * Math.sin(-fHeading) + targetWY * Math.cos(-fHeading));

                // assume heading dynamics are fast!
//                yawSetpoint = 1.0 * Math.PI / 2.0;
//                double targetBX = 1 * (targetWX * Math.cos(-yawSetpoint) + targetWY * -Math.sin(-yawSetpoint));
//                double targetBY = 1 * (targetWX * Math.sin(-yawSetpoint) + targetWY * Math.cos(-yawSetpoint));

//                System.out.println("Target x: " + targetWX + " y: " + targetWY);
//                System.out.println("Trans x: " + targetBX + " y: " + targetBY);

                
//            	Vector3f disp = new Vector3f(pos);
//            	disp.sub(currTarget);
                
//                Transform orient = new Transform();
//                orient.setIdentity();
//                orient.setRotation(pose);

//                Vector3f bodyX = new Vector3f(1, 0, 0);
//                orient.transform(bodyX);
                
//                Vector3f bodyY = new Vector3f(0, 1, 0);
//                orient.transform(bodyY);

//                calcTarget = new Vector3f(currTarget);
//                orient.transform(calcTarget);

//                MoveCallback tmp = null;

                showState();

                long time = System.nanoTime();
                double dist = getDistfromPosition3d(currTarget);
                double dist2d = getDistfromPosition2d(currTarget);
                double avgrflow=0.0, avglflow=0.0;

                for(int i=0; i < 8; i++) {
                    avglflow += heliData.process[i];
                    avgrflow += heliData.process[8+i];
                }
                avglflow = (avglflow / 8.0 - 127.0)/127.0;
                avgrflow = (avgrflow / 8.0 - 127.0)/127.0;

                // Normalize roll so it doesnt react to how far it is from target
                switch(currState) {

                    case IDLE:

                        if (control.getThrust() > 0.0) {
                            control.setThrust(0.0);
                        }
                        break;

                    case TAKEOFF:
                        if( pos.z > TAKEOFF_ALTITUDE) {
                            currState = MoveState.MOVE;
                        }

                        control.setThrust(1.0);
                        control.setRoll(control.getRollTrim());
                        control.setPitch(control.getPitchTrim());
                        control.setYaw(control.getYawTrim());
                        break;

                    case LAND:

                        // throttle down to land
                        control.setThrust(control.getThrust()*0.999f);

                    	if (pos.z < LANDING_ALTITUDE) {
                            currState = MoveState.IDLE;

                            if (currMoveCallback != null) {

                                currMoveCallback.reachedDestination();
                                currMoveCallback = null;
                            }
                        }
                    	else {

                            // try to stay on target while landing
                            updateYaw(time, 0);
                            updatePitch(time, -targetBX);
                            updateRoll(time, targetBY);
                    	}
                        break;

                    case RUN:
                        if (dist <= currEpsilon) {
                            if (currMoveCallback != null) {
                                currMoveCallback.reachedDestination();
                                currMoveCallback = null;
                            }
                        }

//                        if( dist2d > TURNING_CIRCLE_RADIUS ) {
//                            //yawSetpoint = Math.atan2(targetWY, targetWX);
//                            //targetBY /= dist2d;
//                        }
                        updateThrottle(time, pos.z);
                        updateYaw(time, fHeading);
                        updatePitch(time, -targetBX);

                        if(false) {//(pos.getY() < 2.0) && (pos.getY() > -2.0)) {
                            // Setting roll using OF
                            double flowdiff = avglflow - avgrflow;
                            double newRoll = -10.0 * (0.15 - flowdiff);

    //                        double rollDelta = newRoll - prevRoll;
    ////                        if( rollDelta > 0.01 ) { rollDelta = 0.01; }
    ////                        if( rollDelta < -0.01 ) { rollDelta = -0.01; }
    //                        newRoll = prevRoll + rollDelta;
                            control.setRoll(newRoll + control.getRollTrim());
                            prevRoll = newRoll;

                            System.out.println(pos.getX() + " " + pos.getY() + " " + avgrflow + " " + avglflow + " " + flowdiff + " " + newRoll + " " + control.getRoll());
                        }
                        else {
                            updateRoll(time, targetBY);
                        }
                        break;

                    case MOVE:
                        if (dist <= currEpsilon) {
                            if (currMoveCallback != null) {
                                currMoveCallback.reachedDestination();
                                currMoveCallback = null;
                            }
                        }

                        // compute desired heading
//                        if( dist2d > TURNING_CIRCLE_RADIUS ) {
//                            //yawSetpoint = Math.atan2(targetWY, targetWX);
//                            //targetBY /= dist2d;
//                        }

                        updateThrottle(time, pos.z);
                        updateYaw(time, fHeading);
                        updatePitch(time, -targetBX);
                        updateRoll(time, targetBY);

                        break;

                    case HOVER:

                        updateThrottle(time, pos.z);
                        updateYaw(time, fHeading);
                        updatePitch(time, -targetBX);
                        updateRoll(time, targetBY);
                        break;

                    default:
                        // do nothing
                }
                control.sendCommands();     // send updated commands to heli
                heliData = control.receiveData();

            }
        }, 0, 10);        // start with delay of 0 ms and run every 10 ms
    }


    @Override
    public void stop() {

        logger.warn("Heli-" + myHeliId + " is stopping...\n");

        if (logData) {
            try {
                logWriter.close();
            }
            catch (IOException e) {
                // do nothing
            }
        }
        
        controlTimer.cancel();
        currState = MoveState.TERMINATED;
    }

    
    /**
     * Moves the helicopter to a point in space.
     *
     * @param x The coordinate in the global X axis (m).
     * @param y The coordinate in the global Y axis (m).
     * @param z The coordinate in the global Z axis (m).
     * @param epsilon The radius around the desired point that is considered acceptable (m).
     */
    protected void moveToPoint(double x, double y, double z, double epsilon) {
        moveToPoint(x, y, z, epsilon, null);
    }


    /**
     * Moves the helicopter to a point in space.
     *
     * @param x The coordinate in the global X axis (m).
     * @param y The coordinate in the global Y axis (m).
     * @param z The coordinate in the global Z axis (m).
     * @param epsilon The radius around the desired point that is considered acceptable (m).
     * @param callback An optional callback to be executed once the helicopter reaches the specified point.
     */
    protected void moveToPoint(double x, double y, double z, double epsilon, MoveCallback callback) {
        if(currState == MoveState.TERMINATED)
            return;

        Vector3f pos = platform.getSensor("position-sensor", PositionSensor.class).getPosition();
        if( pos.getZ() < LANDING_ALTITUDE ) {
            currState = MoveState.TAKEOFF;
        } else {
            currState = MoveState.MOVE;
        }

        currTarget = new Vector3f((float)x, (float)y, (float)z);
        throttlePID.setSetpoint(z);
        rollPID.setSetpoint(0.0);
        pitchPID.setSetpoint(0.0);

        currEpsilon = epsilon;
        currMoveCallback = callback;
    }

    protected void runToPoint(double x, double y, double z, double epsilon, MoveCallback callback) {
        if(currState == MoveState.TERMINATED)
            return;

        currState = MoveState.RUN;
        currTarget = new Vector3f((float)x, (float)y, (float)z);
        throttlePID.setSetpoint(z);
        rollPID.setSetpoint(0.0);
        pitchPID.setSetpoint(0.0);

        currEpsilon = epsilon;
        currMoveCallback = callback;
    }

    /**
     * Lands the helicopter at the current position.
     */
    protected void land() {

    	currTarget = posSensor.getPosition();
//    	yawSetpoint = MathUtil.quaternionToEulerZYX(orientSensor.getPose()).z;
        currState = MoveState.LAND;
        rollPID.setSetpoint(0.0);
        pitchPID.setSetpoint(0.0);
        throttlePID.setSetpoint(0.0); // So it is obvious in the log that we are landing. We do not updateThrottle while landing, so its ok
    }


    /**
     * Lands the helicopter at the specified position.
     */
//    protected void landAtPoint(Vector3f target) {
//
//        target.z = posSensor.getPosition().z;
//        currTarget = target;
//
//        yawSetpoint = MathUtil.quaternionToEulerZYX(orientSensor.getPose()).z;
//        currState = MoveState.LAND;
//    }


    /**
     * Lands the helicopter at the current position.
     */
//    protected void landAtPoint(Vector3f target, MoveCallback callback) {
//
//        currMoveCallback = callback;
//        landAtPoint(target);
//    }


    /**
     * Lands the helicopter at the hive.
     */
    protected void landAtHive() {

        moveToPoint(landingSpot.x, landingSpot.y, landingSpot.z, LANDING_EPSILON,
                    new MoveCallback() {

                        @Override
                        public void reachedDestination() {
                            land();
//                            hoverAtPoint(landingSpot);
//
//                            platform.createTimer(new TimerCallback() {
//
//                                @Override
//                                public void fire(SimTime time) {
//                                    landAtPoint(landingSpot);
//                                }
//                            }, LANDING_STAGING_TIME, TimeUnit.SECONDS);
                        }
                    });

    }


    /**
     * Lands the helicopter at the hive and informs the caller when the maneuver is complete.
     *
     * @param callback The callback to be invoked when the helicopter has landed.
     */
    protected void landAtHive(final MoveCallback callback) {

        moveToPoint(landingSpot.x, landingSpot.y, landingSpot.z, LANDING_EPSILON,
                    new MoveCallback() {

                        @Override
                        public void reachedDestination() {
                            land(); // ignores callback

//                            hoverAtPoint(landingSpot);
//
//                            platform.createTimer(new TimerCallback() {
//
//                                @Override
//                                public void fire(SimTime time) {
//
//                                    currMoveCallback = callback;
//                                    landAtPoint(landingSpot);
//                                }
//                            }, LANDING_STAGING_TIME, TimeUnit.SECONDS);
                        }
                    });
    }


    /**
     * Turns the helicopter counter-clockwise about the body Z axis (yaw).
     *
     * @param angle The angle to turn (in radians).
     */
    protected void face(double angle) {
        if(currState == MoveState.TERMINATED)
            return;

        yawSetpoint = angle;
    }

    protected void face(Vector3f point)
    {
        if(currState == MoveState.TERMINATED)
            return;

        Vector3f pos = posSensor.getPosition();
        face(Math.atan2(point.getY() - pos.getY(), point.getX() - pos.getX()));
    }

    protected void hover(float height) {
        if(currState == MoveState.TERMINATED)
            return;

        hover();
        throttlePID.setSetpoint(height);
    }

    /**
     * Indicates that the helicopter should hover at the current altitude setpoint.
     */
    protected void hover() {
        if(currState == MoveState.TERMINATED)
            return;

    	currTarget = posSensor.getPosition();
        throttlePID.setSetpoint(currTarget.getZ());
        rollPID.setSetpoint(0.0);
        pitchPID.setSetpoint(0.0);
        currState = MoveState.HOVER;
    }

    /**
     * Indicates that the helicopter should hover at the current altitude setpoint.
     */
    protected void hoverAtPoint(Vector3f target) {
        if(currState == MoveState.TERMINATED)
            return;
        currTarget = target;
        yawSetpoint = MathUtil.quaternionToEulerZYX(orientSensor.getPose()).z;
        throttlePID.setSetpoint(currTarget.z);
        rollPID.setSetpoint(0.0);
        pitchPID.setSetpoint(0.0);
        currState = MoveState.HOVER;
    }


    /**
     * Indicates that the helicopter should hover at the given altitude.
     *
     * @param altitude The hover altitude (m).
     */
    protected void hover(double altitude) {

        hover();

        currTarget.z = (float)altitude;
    	yawSetpoint = MathUtil.quaternionToEulerZYX(orientSensor.getPose()).z;
        throttlePID.setSetpoint(altitude);
    }


    /**
     * Indicates that the helicopter should hover about a given target point.
     *
     * @param target The point at which the heli should hover.
     */
    protected void hover(Vector3f target)
    {
        hover();

        currTarget = target;
    	yawSetpoint = MathUtil.quaternionToEulerZYX(orientSensor.getPose()).z;
        throttlePID.setSetpoint(target.z);
    }


    /**
     * Indicates that the helicopter should land and idle until given another command.
     */
    protected void idle() {
        currState = MoveState.IDLE;
        control.setThrust(0.0);
    }


    private void updateThrottle(long time, double alt)
    {
        Double throttleDelta = throttlePID.update(time, alt);

        // pid update can return null
        if (throttleDelta == null)
            throttleDelta = 0.0;

        control.setThrust(control.getThrustTrim() + throttleDelta);
    }

    private void updatePitch(long time, double xDisp)
    {
        Double newPitch = pitchPID.update(time, xDisp);

        // pid update can return null
        if (newPitch == null)
            newPitch = 0.0;

//        if( newPitch > 0.5 ) { newPitch = 0.5; }
//        if( newPitch < -0.5 ) { newPitch = -0.5; }

        double pitchDelta = newPitch - prevPitch;
        if( pitchDelta > 0.01 ) { pitchDelta = 0.01; }
        if( pitchDelta < -0.01 ) { pitchDelta = -0.01; }
        newPitch = prevPitch + pitchDelta;

        control.setPitch(control.getPitchTrim() + newPitch);

        prevPitch = newPitch;
    }

    private void updateRoll(long time, double yDisp)
    {
        Double newRoll = rollPID.update(time, yDisp);

        // pid update can return null
        if (newRoll == null)
            newRoll = 0.0;

//        if( newRoll > 0.5 ) { newRoll = 0.5; }
//        if( newRoll < -0.5 ) { newRoll = -0.5; }

        double rollDelta = newRoll - prevRoll;
        if( rollDelta > 0.01 ) { rollDelta = 0.01; }
        if( rollDelta < -0.01 ) { rollDelta = -0.01; }
        newRoll = prevRoll + rollDelta;

        control.setRoll(control.getRollTrim() + newRoll);

        prevRoll = newRoll;
    }

    private double filterHeading(double heading)
    {
        if( goodOrientation ) {
            prevYaw = currYaw;
            currYaw = heading;

            double yawDiff = currYaw - prevYaw;
            while( yawDiff >= Math.PI )
                yawDiff -= 2.0 * Math.PI;
            while( yawDiff < -Math.PI )
                yawDiff += 2.0 * Math.PI;

            if( yawDiff > 0.1 )
                yawDiff = 0.1;
            if( yawDiff < -0.1 )
                yawDiff = -0.1;

            yawDiffs[yawHistPtr] = yawDiff;
            yawHistPtr = (yawHistPtr + 1)%3;
        }

        double mn = yawDiffs[0], md = yawDiffs[1], mx = yawDiffs[2];
        if( mn > md ) { md = mn; mn = yawDiffs[1]; }
        if( md > mx ) { mx = md; md = yawDiffs[2]; }
        if( mn > md ) { double tmp = md; md = mn; mn = tmp; }

        double dYaw = md;

        idYaw += dYaw;

        if( goodOrientation ) {
            double yawErr = currYaw - idYaw;
            if( yawErr >= Math.PI )
                yawErr -= 2.0 * Math.PI;
            else if( yawErr < -Math.PI )
                yawErr += 2.0 * Math.PI;

            double yawCorrection = 0.05*yawErr;
            if( yawCorrection > 0.02 ) { yawCorrection = 0.02; }
            if( yawCorrection < -0.02 ) { yawCorrection = -0.02; }

            idYaw += yawCorrection;
        }

        if( idYaw >= Math.PI )
            idYaw -= 2 * Math.PI;
        else if( idYaw < -Math.PI )
            idYaw += 2 * Math.PI;

//        System.out.println("Heading: " + heading + " yawDiffs: " + yawDiffs[0] + " " + yawDiffs[1] + " " + yawDiffs[2] + " dYaw: " + dYaw + " idYaw: " + idYaw);

        return idYaw;
    }

    private void updateYaw(long time, double heading)
    {
//        if(currState == MoveState.MOVE)
//            yawSetpoint = Math.atan2((calcTarget.y - pos.y),(calcTarget.x - pos.x));
//        else if(currState == MoveState.HOVER)
//             yawSetpoint = 0.0;
//        yawSetpoint = 1.0 * Math.PI / 2.0;

        double yawDiff = yawSetpoint - idYaw;
        while( yawDiff >= Math.PI )
            yawDiff -= 2 * Math.PI;
        while( yawDiff < -Math.PI )
            yawDiff += 2 * Math.PI;

       Double yawDelta = yawPID.update(time, yawDiff);

        // pid update can return null
        if(yawDelta == null)
            yawDelta = 0.0;

        double yaw_clamp = 0.2;
        double yaw_clamp_neg = -1.0 * yaw_clamp;
        if(yawDelta > yaw_clamp)
            yawDelta = yaw_clamp;
        else if(yawDelta < yaw_clamp_neg)
            yawDelta = yaw_clamp_neg;

        control.setYaw(control.getYawTrim() + yawDelta);
//        control.setYaw(control.getYawTrim());

//        System.out.println("Heading: " + heading + " yawDiff: " + yawDiff + " yawDelta: " + yawDelta);
    }

    private void showState() {

        if (logger.isDebugEnabled()) {

            logger.debug("State: " + currState + " Pos: " + posSensor.getPosition() +
                         " Target: " + currTarget + " Dist: " + getDistfromPosition3d(currTarget));
        }
    }

    private void logModelData(MoveState s, Vector3f pos1, Vector3f pos2, Quat4f pose1, Quat4f pose2, float time, float dt) {

        if (logData) {

            Vector3f vel = new Vector3f();
            vel.sub(pos2, pos1);
            vel.scale(1/dt);

            Transform orient = new Transform();
            orient.setIdentity();
            orient.setRotation(pose2);
            Vector3f x = new Vector3f(1, 0, 0);
            Vector3f y = new Vector3f(0, 1, 0);
            Vector3f z = new Vector3f(0, 0, 1);
            orient.transform(x);
            orient.transform(y);
            orient.transform(z);

            double vel_x = vel.dot(x);
            double vel_y = vel.dot(y);
            double vel_z = vel.dot(z);

            Quat4f dQ = new Quat4f();
            dQ.mulInverse(pose2, pose1);

            Vector3f dEuler = MathUtil.quaternionToEulerZYX(dQ);
            dEuler.scale(1/dt);
            Vector3f pose = MathUtil.quaternionToEulerZYX(pose2);

            Vector3f dEulerApprox = new Vector3f(dQ.x, dQ.y, dQ.z);
            dEulerApprox.scale(2/dt);

            int thrust = AutoHeliBee.rawCommand(control.getThrust());
            int roll = AutoHeliBee.rawCommand(control.getRoll());
            int pitch = AutoHeliBee.rawCommand(control.getPitch());
            int yaw = AutoHeliBee.rawCommand(control.getYaw());

            try {
//                //logWriter.write(dt + ", 0, " + thrust + ", " + roll +
//                                ", " + pitch + ", " + yaw + ", " +
//                                vel_x + ", " + vel_y + ", " + vel_z + ", " + dEuler.x +
//                                ", " + dEuler.y + ", " + dEuler.z + "\n");

                //logWriter.write(System.currentTimeMillis() + " " + pos2.getX() + " " + pos2.getY() + " " + pos2.getZ() + " " + throttlePID.getMedianD() + "\n");
//                logger.info(System.currentTimeMillis() + " " + pose.z);
//                logWriter.write(System.currentTimeMillis() + " " + pose.z + "\n");
                 //  logWriter.write(s.toString() + " " + dt + " " + pos1.getX() + " " + pos2.getX() + " " + vel_z + " " + thrust + "\n");

                //logger.info(s.toString() + " " + time + " " + dt + " " + pos2.getX() + " " + pos2.getY() + " " + pos2.getZ() + " " + pose.getX() + " " + pose.getY() + " " + pose.getZ() + " " + thrust + " " + roll + " " + pitch + " " + yaw);

                logWriter.write(time + " " + dt + " "
                        + pos2.getX() + " " + pos2.getY() + " " + pos2.getZ() + " "
                        + pose.getX() + " " + pose.getY() + " " + pose.getZ() + " "
                        + currTarget.getX() + " " + currTarget.getY() + " " + currTarget.getZ() + " "
                        + thrust + " " + roll + " " + pitch + " " + yaw + " " + " "
                        + throttlePID.getSetPoint() + " " + throttlePID.getIErr() + " " + throttlePID.getDErr() + " "
                        + rollPID.getSetPoint() + " " + rollPID.getIErr() + " " + rollPID.getDErr() + " "
                        + pitchPID.getSetPoint() + " " + pitchPID.getIErr() + " " + pitchPID.getDErr() + " "
                        + yawPID.getSetPoint() + " " + yawPID.getIErr() + " " + yawPID.getDErr() + " "
                        + heliData.frameCount + " " + heliData.gyros[0] + " " + heliData.gyros[1] + " " + heliData.gyros[2] + " "
                        + (heliData.process[0]) + " " + (heliData.process[1]) + " " + (heliData.process[2]) + " " + (heliData.process[3]) + " "
                        + (heliData.process[4]) + " " + (heliData.process[5]) + " " + (heliData.process[6]) + " " + (heliData.process[7]) + " "
                        + (heliData.process[8]) + " " + (heliData.process[9]) + " " + (heliData.process[10]) + " " + (heliData.process[11]) + " "
                        + (heliData.process[12]) + " " + (heliData.process[13]) + " " + (heliData.process[14]) + " " + (heliData.process[15]) + " "
                        + (heliData.debug[0]) + " " + (heliData.debug[1]) + " " + (heliData.debug[2]) + " " + (heliData.debug[3]) + " "
                        + fHeading + "\n");
            }
            catch (IOException e) {
                // do nothing
            }
        }
    }


    private Vector3f calcHiveLocation() {

        int numHelis = allHelis.size();
        Vector3f hive = null;

        if (numHelis < 1) {
            return null;
        }
        else if (numHelis == 1) {
            hive =  new Vector3f(hiveLocation.x, hiveLocation.y, LANDING_STAGING_ALTITUDE);
        }
        else {

            double angle = 0; // in radians
            float x,y;

            for(AbstractHeli h: allHelis)
            {
                if (h.getHeliId() == myHeliId)
                {
                    x = hiveLocation.x + (float)(hiveRadius * Math.cos(angle));
                    y = hiveLocation.y + (float)(hiveRadius * Math.sin(angle));
                    hive = new Vector3f(x, y, LANDING_STAGING_ALTITUDE);
                }
                else {
                    angle += 2 * Math.PI / numHelis;
                }
            }
        }

        return hive;
    }


    private AbstractHeli findClosestHeli(List<AbstractHeli> helis, float threshold, float thresholdHive) {

        AbstractHeli closestHeli = null;
        Vector3f otherPos;
        float dist;
        float minDist = Float.MAX_VALUE;
        float thresh;

        for(AbstractHeli h: helis) {

            if (h.getHeliId() != myHeliId) {

                otherPos = h.getTruthPosition();
                dist = getDistfromPosition2d(otherPos);

                if (dist < minDist) {

                    thresh = threshold;

                    if (otherPos.z <= LANDING_ALTITUDE) {
                        thresh = thresholdHive;
                    }

                    if (dist <= thresh) {
                        
                        closestHeli = h;
                        minDist = dist;
                    }
                }
            }
        }

        return closestHeli;
    }


    private float getDistfromPosition2d(Vector3f value) {

        Vector3f pos = posSensor.getPosition();
        Vector3f temp = new Vector3f(value);
        temp.sub(pos);
        return((float)Math.sqrt(temp.x*temp.x + temp.y*temp.y));

    }


    private float getDistfromPosition3d(Vector3f value) {

        Vector3f pos = posSensor.getPosition();
        Vector3f temp = new Vector3f(value);
        temp.sub(pos);
        return(temp.length());
    }


    @Inject(optional = true)
    public final void setLogging(@Named("logging") final boolean logData) {
        this.logData = logData;
    }


    @Inject(optional = true)
    public final void setLogPath(@Named("log-path") final String logPath) {
        this.logPath = logPath;
    }


    @Inject
    public final void setSimEngine(@GlobalScope final SimEngine engine) {
        this.simEngine = engine;
    }


    /**
     * A callback that can be implemented by derived classes to be informed when the
     * heli has reached a desired destination.
     */
    protected static interface MoveCallback {

        public void reachedDestination();
    }
}
