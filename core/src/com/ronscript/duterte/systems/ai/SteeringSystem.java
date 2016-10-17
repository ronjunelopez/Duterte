package com.ronscript.duterte.systems.ai;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.ai.GdxAI;
import com.badlogic.gdx.ai.steer.Steerable;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.ronscript.duterte.DebugDrawable;
import com.ronscript.duterte.components.ai.steer.SteeringComponent;
import com.ronscript.duterte.utils.CameraManager;
import com.ronscript.duterte.utils.Mapper;

/**
 * Copyright (C) 2016 Duterte on 8/22/2016
 * by Ron
 */
public class SteeringSystem extends IteratingSystem implements
        EntityListener,
        DebugDrawable{

    private final ObjectMap<Entity, Steerable<Vector2>> agentMap = new ObjectMap<Entity, Steerable<Vector2>>();
    private final Array<Steerable<Vector2>> agentsScheduledForRemoval = new Array<Steerable<Vector2>>();

    public SteeringSystem() {
        super(Family.all(SteeringComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        Mapper.steering.get(entity). agent.update(deltaTime);
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
        agentMap.put(entity, Mapper.steering.get(entity).agent);
    }

    @Override
    public void entityRemoved(Entity entity) {
        agentsScheduledForRemoval.add(agentMap.remove(entity));
    }

    public Array<Steerable<Vector2>> getAgents() {
        return agentMap.values().toArray();
    }

    @Override
    public void update(float deltaTime) {
        GdxAI.getTimepiece().update(deltaTime);
        super.update(deltaTime);

    }



    @Override
    public void debugDraw(ShapeRenderer shape, float deltaTime) {
        shape.setProjectionMatrix(CameraManager.getProjectionMatrix());
        ObjectMap.Values<Steerable<Vector2>>  agents = agentMap.values();

        while (agents.hasNext()) {
            Steerable<Vector2> agent = agents.next();
            if(agent instanceof DebugDrawable) {
                ((DebugDrawable) agent).debugDraw(shape, deltaTime);
            }
        }
    }

}
