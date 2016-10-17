package com.ronscript.duterte.ai.steer.box2d;

import com.badlogic.gdx.ai.utils.Location;
import com.badlogic.gdx.math.Vector2;
import com.ronscript.duterte.utils.SteeringUtils;

/**
 * Copyright (C) 2016 Duterte on 8/22/2016
 * by Ron
 */
public class AgentLocation implements Location<Vector2> {


    private Vector2 position = new Vector2();
    private float orientation = 0.0f;

    public AgentLocation() {

    }

    public AgentLocation(float x, float y) {
        position.set(x, y);
    }

    @Override
    public Vector2 getPosition() {
        return position;
    }

    @Override
    public float getOrientation() {
        return orientation;
    }

    @Override
    public void setOrientation(float orientation) {
        this.orientation = orientation;
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

}
