package com.ronscript.duterte.utils;

import com.badlogic.gdx.ai.steer.Proximity;
import com.badlogic.gdx.ai.steer.Steerable;
import com.badlogic.gdx.ai.steer.behaviors.Arrive;
import com.badlogic.gdx.ai.steer.behaviors.FollowPath;
import com.badlogic.gdx.ai.steer.behaviors.Hide;
import com.badlogic.gdx.ai.steer.behaviors.RaycastObstacleAvoidance;
import com.badlogic.gdx.ai.steer.behaviors.Wander;
import com.badlogic.gdx.ai.steer.limiters.LinearAccelerationLimiter;
import com.badlogic.gdx.ai.steer.utils.RayConfiguration;
import com.badlogic.gdx.ai.steer.utils.paths.LinePath;
import com.badlogic.gdx.ai.utils.Location;
import com.badlogic.gdx.ai.utils.RaycastCollisionDetector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.ronscript.duterte.GameWorld;

/**
 * Copyright (C) 2016 Duterte on 8/21/2016
 * by Ron
 */
public class SteeringUtils {

    public static float vectorToAngle(Vector2 vector) {
        return (float)Math.atan2(-vector.x, vector.y);
    }

    public static Vector2 angleToVector(Vector2 outVector, float angle) {
        outVector.x = -(float)Math.sin(angle);
        outVector.y = (float)Math.cos(angle);
        return outVector;
    }

    public static SteerDirection getDirection(float degreeAngle) {
        SteerDirection direction = SteerDirection.NONE;
        if(degreeAngle > 0 && degreeAngle < 22.5 || degreeAngle > 337.5 && degreeAngle < 360){
            direction = SteerDirection.WEST;
        }

        if(degreeAngle > 22.5 && degreeAngle <67.5) {
            direction = SteerDirection.NORTH_WEST;
        }

        if(degreeAngle > 67.5 && degreeAngle < 112.5){
            direction = SteerDirection.NORTH;
        }

        if(degreeAngle > 112.5 && degreeAngle < 157.5){
            direction = SteerDirection.NORTH_EAST;
        }

        if(degreeAngle > 157.5 && degreeAngle < 202.5){
            direction = SteerDirection.EAST;
        }

        if(degreeAngle > 202.5 && degreeAngle < 247.5){
            direction = SteerDirection.SOUTH_EAST;
        }

        if(degreeAngle > 247.5 && degreeAngle < 292.5) {
            direction = SteerDirection.SOUTH;
        }

        if(degreeAngle > 292.5 && degreeAngle < 336.5) {
            direction = SteerDirection.SOUTH_WEST;
        }

        return direction;
    }

    public static Hide<Vector2> createHide(Steerable<Vector2> agent, Location<Vector2> target, Proximity<Vector2> proximity) {
        Hide<Vector2> hide = new Hide<Vector2>(agent, target, proximity);
        hide.setTimeToTarget(0.1f) //
                .setArrivalTolerance(0.001f) //
                .setDecelerationRadius(2.5f)
                .setDistanceFromBoundary(1);
        return hide;
    }

    public static Wander<Vector2> createWander(Steerable<Vector2> agent) {
        return new Wander<Vector2>(agent)
                .setFaceEnabled(false) // We want to use Face internally (independent facing is on)
//                .setFaceEnabled(true) // We want to use Face internally (independent facing is on)
//                .setAlignTolerance(0.001f) // Used by Face
//                .setDecelerationRadius(1) // Used by Face
                .setLimiter(new LinearAccelerationLimiter(0.2f)) //
                .setWanderOffset(1) //
                .setWanderOrientation(180) //
                .setWanderRadius(1) //
                .setWanderRate(MathUtils.degreesToRadians * 120);
    }

    public static Arrive<Vector2> createArrive(Steerable<Vector2> agent, Location<Vector2> target) {
        return new Arrive<Vector2>(agent, target)
            .setArrivalTolerance(0.001f)
            .setDecelerationRadius(3);
    }

    public static RaycastObstacleAvoidance<Vector2> createRaycastObstacleAvoidance(Steerable<Vector2> agent, RaycastCollisionDetector<Vector2> raycastCollisionDetector, RayConfiguration<Vector2> rayConfiguration) {
        return new RaycastObstacleAvoidance<Vector2>(agent, rayConfiguration ,
                raycastCollisionDetector, 1);
    }

    public static FollowPath createOpenedFollowPath(Steerable<Vector2> agent,  Vector2 position,
                                                  Vector2 target, Array<Vector2> out) {
        return createFollowPath(agent, position, target, out, true);
    }

    public static FollowPath createClosedFollowPath(Steerable<Vector2> agent,  Vector2 position,
                                                    Vector2 target, Array<Vector2> out) {
        return createFollowPath(agent, position, target, out, false);
    }

    private static FollowPath createFollowPath(Steerable<Vector2> agent,
                                                                                Vector2 position,
                                                                         Vector2 target, Array<Vector2> out, final boolean openPath) {
        if(GameWorld.navigator.createPath(position, target, out)) {
            LinePath<Vector2> linePath = new LinePath<Vector2>(out, openPath);
            return new FollowPath<Vector2, LinePath.LinePathParam>(agent, linePath, 0.1f)
                    // Setters below are only useful to arrive at the end of an open path
                    .setTimeToTarget(1) //
                    .setArrivalTolerance(0.001f) //
                    .setDecelerationRadius(0.5f);
        }
        return null;
    }

  public static FollowPath createFollowPath(Steerable<Vector2> agent, Array<Vector2> path) {
      LinePath<Vector2> linePath = new LinePath<Vector2>(path, true);
      FollowPath<Vector2, LinePath.LinePathParam> followPath
              = new FollowPath<Vector2, LinePath.LinePathParam>(agent, linePath, 0.1f)
              // Setters below are only useful to arrive at the end of an open path
              .setTimeToTarget(1) //
              .setArrivalTolerance(0.001f) //
              .setDecelerationRadius(0.5f);
      return followPath;
  }

    public enum SteerDirection {
        WEST, NORTH_WEST, NORTH,NORTH_EAST,EAST,SOUTH_EAST, SOUTH, SOUTH_WEST, NONE
    }

}
