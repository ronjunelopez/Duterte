package com.ronscript.duterte.ai.steer;

import com.badlogic.gdx.ai.steer.Steerable;
import com.badlogic.gdx.ai.steer.SteeringAcceleration;
import com.badlogic.gdx.ai.steer.behaviors.Evade;
import com.badlogic.gdx.math.Vector;

/**
 * Copyright (C) 2016 Duterte on 10/1/2016
 *
 * Evade ahead
 * by Ron
 */
public class EvadeAhead <T extends Vector<T>> extends Evade<T> {

    protected float maxAheadRadius;

    public EvadeAhead(Steerable<T> owner, Steerable<T> target) {
        this(owner, target, 1);
    }

    public EvadeAhead(Steerable<T> owner, Steerable<T> target, float maxAheadRadius) {
        super(owner, target);
        this.maxAheadRadius = maxAheadRadius;
    }

    public void setAheadRadius(float maxAheadRadius) {
        this.maxAheadRadius = maxAheadRadius;
    }

    @Override
    public SteeringAcceleration<T> calculateSteering(SteeringAcceleration<T> steering) {
        boolean isOnTargetSight = isOnTargetSight(target, getAhead(target), maxAheadRadius);
        setEnabled(isOnTargetSight);
        return super.calculateSteering(steering);
    }

    private T getAhead(Steerable<T> leader) {
        T tv = leader.getLinearVelocity().cpy();
        // calculate the ahead point
        tv.nor();
        tv.scl(1);
        return leader.getPosition().cpy().add(tv);
    }

    private boolean isOnTargetSight(Steerable<T> leader, T leaderAhead, float radius) {
        T ownerPosition = this.getOwner().getPosition();
        float radius2 = radius * radius;
        return (leader.getPosition().dst2(ownerPosition) <= radius2
                || leaderAhead.dst2(ownerPosition) <= radius2);}

}
