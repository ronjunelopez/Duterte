package com.ronscript.duterte.ai.steer;

import com.badlogic.gdx.ai.steer.Steerable;
import com.badlogic.gdx.ai.steer.SteeringAcceleration;
import com.badlogic.gdx.ai.steer.SteeringBehavior;
import com.badlogic.gdx.ai.utils.Ray;
import com.badlogic.gdx.ai.utils.RaycastCollisionDetector;
import com.badlogic.gdx.math.Vector2;
import com.ronscript.duterte.ai.steer.box2d.Box2dRaycastWallCollisionDetector;
import com.ronscript.duterte.utils.WorldManager;

/**
 * Copyright (C) 2016 Duterte on 10/5/2016
 *
 * implementation of http://fightingkitten.webcindario.com/?p=610
 * by Ron
 */
public class AvoidWall  extends SteeringBehavior<Vector2> {

    private final RayStar radar;
    private final RaycastCollisionDetector<Vector2> wallCollisionDetector;

    public AvoidWall(Steerable<Vector2> owner) {
        super(owner);
        radar = new RayStar(owner);
        wallCollisionDetector = new Box2dRaycastWallCollisionDetector(WorldManager.getWorld());
    }


    @Override
    protected SteeringAcceleration<Vector2> calculateRealSteering(SteeringAcceleration<Vector2> steeringAcceleration) {
        radar.updateRays();

        Vector2 direction = new Vector2();

        float minDistance = 0;
        int counter = 0;

        for(Ray<Vector2> ray : radar.getRays())
        {
            if(collides(ray))
            {
                direction.add(ray.end);

                float distance = (ray.end).dst(ray.start);

                if(minDistance == 0 || minDistance > distance)
                {
                    minDistance = distance;
                }

                counter++;
            }
        }

        direction.nor();

        if(counter > 0)
        {
            int minDistCollision = 4;

            float maxAcceleration = (minDistCollision * minDistCollision) * owner.getMaxLinearAcceleration();
            minDistance = minDistance - owner.getBoundingRadius();
            float magnitude = maxAcceleration / (float)Math.pow(minDistance, 2);

            owner.getLinearVelocity().add(direction.scl(magnitude));
            steeringAcceleration.angular = 0;
        }

        return null;
    }

    private boolean collides(Ray ray)
    {
        return (wallCollisionDetector.collides(ray));
    }
}
