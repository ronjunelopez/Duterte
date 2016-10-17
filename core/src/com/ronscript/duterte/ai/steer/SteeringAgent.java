package com.ronscript.duterte.ai.steer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.steer.Steerable;
import com.badlogic.gdx.ai.steer.SteeringAcceleration;
import com.badlogic.gdx.ai.steer.SteeringBehavior;
import com.badlogic.gdx.ai.utils.Location;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.ronscript.duterte.ai.steer.box2d.AgentLocation;
import com.ronscript.duterte.utils.SteeringUtils;

/**
 * Copyright (C) 2016 Duterte on 8/21/2016
 * by Ron
 */
public class SteeringAgent implements Steerable<Vector2> {

    private final float zeroLinearSpeedThreshold;
    private final SteeringAcceleration<Vector2> steeringOutput;
    public float orientation;
    private Body body;
    private float maxLinearSpeed, maxLinearAcceleration;
    private float maxAngularSpeed, maxAngularAcceleration;
    private float boundingRadius;
    private boolean tagged;
    private boolean independentFacing;
    private SteeringBehavior<Vector2> steeringBehavior;

    public SteeringAgent(Body body, boolean independentFacing, float boundingRadius) {
        this.body = body;
        this.independentFacing = independentFacing;
        this.boundingRadius = boundingRadius;
        tagged = false;
        zeroLinearSpeedThreshold = 0.001f;
        steeringOutput = new SteeringAcceleration<Vector2>(new Vector2());
        if(this.body.getUserData() == null) {
            this.body.setUserData(this);
            Gdx.app.log("SteeringAgent", "body set as this");
        }
    }

    @Override
    public Vector2 getLinearVelocity() {
        return body.getLinearVelocity();
    }

    @Override
    public float getAngularVelocity() {
        return body.getAngularVelocity();
    }

    @Override
    public float getBoundingRadius() {
        return boundingRadius;
    }

    @Override
    public boolean isTagged() {
        return tagged;
    }

    @Override
    public void setTagged(boolean tagged) {
        this.tagged = tagged;
    }

    @Override
    public float getZeroLinearSpeedThreshold() {
        return zeroLinearSpeedThreshold;
    }

    @Override
    public void setZeroLinearSpeedThreshold(float value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public float getMaxLinearSpeed() {
        return maxLinearSpeed;
    }

    @Override
    public void setMaxLinearSpeed(float maxLinearSpeed) {
        this.maxLinearSpeed = maxLinearSpeed;
    }

    @Override
    public float getMaxLinearAcceleration() {
        return maxLinearAcceleration;
    }

    @Override
    public void setMaxLinearAcceleration(float maxLinearAcceleration) {
        this.maxLinearAcceleration = maxLinearAcceleration;
    }

    @Override
    public float getMaxAngularSpeed() {
        return maxAngularSpeed;
    }

    @Override
    public void setMaxAngularSpeed(float maxAngularSpeed) {
        this.maxAngularSpeed = maxAngularSpeed;
    }

    @Override
    public float getMaxAngularAcceleration() {
        return maxAngularAcceleration;
    }

    @Override
    public void setMaxAngularAcceleration(float maxAngularAcceleration) {
        this.maxAngularAcceleration = maxAngularAcceleration;
    }

    @Override
    public Vector2 getPosition() {
        return body.getPosition();
    }

    @Override
    public float getOrientation() {
        return body.getAngle();
    }

    @Override
    public void setOrientation(float orientation) {
        body.setTransform(getPosition(), orientation);
    }

    @Override
    public float vectorToAngle(Vector2 vector) {
        return SteeringUtils.vectorToAngle(vector);
    }

    @Override
    public Vector2 angleToVector(Vector2 outVector, float angle) {
        return SteeringUtils.angleToVector(outVector, angle);
    }

    @Override
    public Location<Vector2> newLocation() {
        return new AgentLocation();
    }

    public void update (float delta) {
        if (steeringBehavior != null) {
            if(maxLinearAcceleration == 0 && maxLinearSpeed == 0 && maxAngularAcceleration == 0 && maxAngularSpeed == 0) {
                throw new IllegalStateException("Speed and Acceleration is zero");
            }
            // Calculate agent acceleration
            steeringBehavior.calculateSteering(steeringOutput);

            applySteering(steeringOutput, delta);
        }
    }

    private void applySteering(SteeringAcceleration<Vector2> steering, float deltaTime) {
        boolean anyAccelerations = false;

        if (!steering.linear.isZero()) {
            body.applyForceToCenter(steering.linear, true);
            anyAccelerations = true;
        }
        if(independentFacing) {
            if(steering.angular != 0) {
//                Gdx.app.log("", "" + steering.angular);
                body.applyTorque(steering.angular, true);
                anyAccelerations = true;
            }
        } else {
            float newOrientation = calculateOrientationFromLinearVelocity();
            if (newOrientation != getOrientation()) {
                float angularVelocity = (newOrientation - getOrientation()) * deltaTime;
                body.setAngularVelocity(angularVelocity);
                setOrientation(newOrientation);
            }
        }

        if (anyAccelerations) {
            // Cap the linear speed
            Vector2 linearVelocity = body.getLinearVelocity();
            // limit the speed using len2
            linearVelocity.limit(this.getMaxLinearSpeed());
            body.setLinearVelocity(linearVelocity);

            // Cap the angular speed

            float maxAngVelocity = getMaxAngularSpeed();
            if (body.getAngularVelocity() > maxAngVelocity) {
                body.setAngularVelocity(maxAngVelocity);
            }
        }
    }

    public boolean isZero() {
        return getLinearVelocity().isZero(getZeroLinearSpeedThreshold());
    }

    public boolean isIndependentFacing() {
        return independentFacing;
    }

    public SteeringUtils.SteerDirection getDirection() {
        float degreeAngle = getLinearVelocity().nor().angle();
        return  !isZero() ? SteeringUtils.getDirection(degreeAngle) : SteeringUtils.SteerDirection.NONE;
    }

    public float calculateOrientationFromLinearVelocity () {
        return isZero() ? getOrientation() : vectorToAngle(getLinearVelocity());
    }

    public SteeringBehavior<Vector2> getBehavior() {
        return steeringBehavior;
    }

    public void setBehavior(SteeringBehavior<Vector2> steeringBehavior) {
        this.steeringBehavior = steeringBehavior;
    }

    public Body getBody() {
        return body;
    }
}
