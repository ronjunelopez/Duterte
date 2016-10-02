package com.ronscript.duterte.ai.steer;

import com.badlogic.gdx.ai.steer.Steerable;
import com.badlogic.gdx.ai.steer.SteeringAcceleration;
import com.badlogic.gdx.ai.steer.behaviors.Arrive;
import com.badlogic.gdx.math.Vector;

/**
 * Copyright (C) 2016 Duterte on 9/30/2016
 * Arrive behind the target
 * by Ron
 */
public class ArriveBehind<T extends Vector<T>> extends Arrive<T> {

    protected Steerable<T> target;
    protected T behind;
    protected final float maxBehindDistance = 0.8f;

    public ArriveBehind(Steerable<T> owner, Steerable<T> target) {
        super(owner);
        this.target = target;
    }

    @Override
    protected SteeringAcceleration<T> calculateRealSteering(SteeringAcceleration<T> steering) {
        return follow(steering, target);
    }

    protected SteeringAcceleration<T> follow(SteeringAcceleration<T> steering, Steerable<T> target) {
        T tv = target.getLinearVelocity().cpy();
        // calculate the behind point
        tv.nor();
        tv.scl(maxBehindDistance);
        tv.scl(-1);
        behind = target.getPosition().cpy().add(tv);
        return arrive(steering, behind);
    }

}
