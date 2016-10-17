package com.ronscript.duterte.systems.physics;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector2;
import com.ronscript.duterte.components.physics.MovementComponent;
import com.ronscript.duterte.components.properties.TransformComponent;
import com.ronscript.duterte.utils.Mapper;

/**
 * @author Ron
 * @since 8/9/2016
 */
public class MovementSystem extends IteratingSystem {

    Vector2 temp = new Vector2();

    public MovementSystem() {
        super(Family.all(MovementComponent.class, TransformComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {

        MovementComponent movement = Mapper.movement.get(entity);
        TransformComponent transform = Mapper.transform.get(entity);

        temp.set(movement.velocity).scl(deltaTime);

        transform.position.add(temp);
    }
}
