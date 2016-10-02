package com.ronscript.duterte.ai.steer;

import com.badlogic.gdx.ai.steer.Steerable;
import com.badlogic.gdx.ai.steer.behaviors.Arrive;
import com.badlogic.gdx.ai.steer.behaviors.Face;
import com.badlogic.gdx.ai.steer.behaviors.Wander;
import com.badlogic.gdx.ai.steer.limiters.LinearAccelerationLimiter;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.ronscript.duterte.ai.steer.box2d.AgentLocation;
import com.ronscript.duterte.utils.Constants;

/**
 * Copyright (C) 2016 Duterte on 10/1/2016
 *
 * TODO
 * by Ron
 */
public class PlayerSteering extends SteeringAgent {

    public Arrive<Vector2> arrive;
    public Face<Vector2> face;
    public AgentLocation facingLocation;

    public PlayerSteering(Body body) {
        super(body, true, 1);
//        body.setFixedRotation(true);
        setMaxLinearSpeed(1);
        setMaxLinearAcceleration(0.1f);
        setMaxAngularAcceleration(0.01f);
        setMaxAngularSpeed(0.1f);

        facingLocation = new AgentLocation(Constants.WIDTH / 2, Constants.HEIGHT / 2);
        arrive = new Arrive<Vector2>(this, facingLocation);
        face = new Face<Vector2>(this, facingLocation);
        face.setTimeToTarget(0.01f) //
                .setAlignTolerance(0.0001f) //
                .setDecelerationRadius(MathUtils.degreesToRadians * 120);
        setBehavior(arrive);
    }

    public Wander<Vector2> createWander(Steerable<Vector2> agent) {
        return new Wander<Vector2>(agent)
                .setLimiter(new LinearAccelerationLimiter(0.2f)) //
                .setWanderOffset(1) //
                .setWanderOrientation(180) //
                .setWanderRadius(5) //
                .setWanderRate(MathUtils.PI2 * 4);
    }

    @Override
    public void update(float delta) {
        super.update(delta);
    }
}
