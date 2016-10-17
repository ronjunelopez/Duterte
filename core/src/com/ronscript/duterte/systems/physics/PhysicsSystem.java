package com.ronscript.duterte.systems.physics;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IntervalIteratingSystem;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.ronscript.duterte.components.physics.PhysicsComponent;
import com.ronscript.duterte.components.properties.TransformComponent;
import com.ronscript.duterte.utils.Constants;
import com.ronscript.duterte.utils.Mapper;

/**
 * @author Ron
 * @since 8/8/2016
 */
public class PhysicsSystem extends IntervalIteratingSystem implements EntityListener {

    private World world;

    private ObjectMap<Entity, PhysicsComponent> physicsComponents = new ObjectMap<Entity, PhysicsComponent>();
    private Array<Body> bodiesScheduleForRemoval = new Array<Body>();

    public PhysicsSystem(World world) {
        super(Family.all(PhysicsComponent.class, TransformComponent.class).get(), Constants.TIME_STEP);
        this.world = world;
    }

    @Override
    protected void processEntity(Entity entity) {
        PhysicsComponent physics = Mapper.physics.get(entity);
        TransformComponent transform = Mapper.transform.get(entity);

        if(physics.body == null) {
            return;
        }

        Vector2 bodyPosition = physics.body.getPosition();

        transform.position.x = bodyPosition.x;
        transform.position.y = bodyPosition.y;
        transform.rotation = physics.body.getAngle(); // doesn't need convert degreeToRadians, rendering system will handle that
    }

    @Override
    protected void updateInterval() {
        world.step(Constants.TIME_STEP, Constants.VELOCITY_ITERATIONS, Constants.POSITION_ITERATIONS);
        super.updateInterval();
//        BodyManager.getInstance().debug();
        for (Body body: bodiesScheduleForRemoval) {
            world.destroyBody(body);
        }
        bodiesScheduleForRemoval.clear();
    }

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);
        engine.addEntityListener(getFamily(), this);
    }

    @Override
    public void removedFromEngine(Engine engine) {
        super.removedFromEngine(engine);
        engine.removeEntityListener(this);
    }

    @Override
    public void entityAdded(Entity entity) {
        physicsComponents.put(entity, Mapper.physics.get(entity));
    }

    @Override
    public void entityRemoved(Entity entity) {
        PhysicsComponent physics = physicsComponents.remove(entity);
        bodiesScheduleForRemoval.add(physics.body);
    }
}
