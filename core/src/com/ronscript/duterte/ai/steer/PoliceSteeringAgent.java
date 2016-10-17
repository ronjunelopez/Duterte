package com.ronscript.duterte.ai.steer;

import com.badlogic.gdx.ai.steer.Steerable;
import com.badlogic.gdx.ai.steer.SteeringBehavior;
import com.badlogic.gdx.ai.steer.behaviors.Pursue;
import com.badlogic.gdx.ai.steer.behaviors.RaycastObstacleAvoidance;
import com.badlogic.gdx.ai.steer.behaviors.Wander;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.ronscript.duterte.utils.CharacterBehaviorBuilder;

/**
 * Copyright (C) 2016 Duterte on 9/3/2016
 * by Ron
 */
public class PoliceSteeringAgent  extends SteeringAgent {

    public Pursue<Vector2> pursue;
    public Vector2 tmp = new Vector2();
    RaycastObstacleAvoidance<Vector2> roa;
    Wander<Vector2> wander;
    SteeringBehavior<Vector2> priority;

    public PoliceSteeringAgent(World world, Body body) {
        super(body, true, 1);
        setMaxLinearAcceleration(0.2f);
        setMaxLinearSpeed(1);
        CharacterBehaviorBuilder bb = new CharacterBehaviorBuilder(this, world);
        roa = bb.createObstacleAvoidance();
        wander = bb.createWander();
        pursue = bb.createPursue(null);
        priority = bb.get();
        setBehavior(bb.get());
    }

    public void wander() {
        pursue.setTarget(null);
        pursue.setEnabled(false);
//        wander.setEnabled(true);
//        roa.setEnabled(true);
//        pursue.setEnabled(false);
    }

    public void pursue(Steerable<Vector2> target) {
//        wander.setEnabled(false);
//        roa.setEnabled(false);
        pursue.setTarget(target);
        pursue.setEnabled(true);
    }



    @Override
    public void update(float delta) {
        super.update(delta);
        boolean withinRange = isTargetWithinRange();
        if(pursue.isEnabled() && !withinRange) {
            setBehavior(null);
            getBody().setLinearVelocity(0,0);
        } else {
            setBehavior(priority);
        }
        pursue.setEnabled(withinRange);
        wander.setEnabled(!pursue.isEnabled());
        roa.setEnabled(!pursue.isEnabled());
    }

    private boolean isTargetWithinRange() {
        if(pursue.getTarget() != null) {
            tmp.set(pursue.getTarget().getPosition());
            tmp.sub(getPosition());
            float targetDistance = tmp.len2();
            return targetDistance >=  2 * 2;
        }
        return false;
    }
}
