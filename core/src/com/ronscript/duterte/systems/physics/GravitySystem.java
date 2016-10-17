package com.ronscript.duterte.systems.physics;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.ronscript.duterte.components.physics.GravityComponent;
import com.ronscript.duterte.components.physics.MovementComponent;
import com.ronscript.duterte.utils.Mapper;

/**
 * @author Ron
 * @since 8/8/2016
 */
public class GravitySystem extends IteratingSystem {

    public GravitySystem() {
        super(Family.all(GravityComponent.class, MovementComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        GravityComponent gravity = Mapper.gravity.get(entity);
        MovementComponent movement = Mapper.movement.get(entity);
        movement.velocity.add(gravity.horizontal * gravity.scale * deltaTime, -gravity.vertical * gravity.scale * deltaTime);
    }
}
