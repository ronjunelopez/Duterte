package com.ronscript.duterte.systems.ai;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.ronscript.duterte.components.ai.fsm.StateMachineComponent;
import com.ronscript.duterte.components.ai.msg.MessagingComponent;
import com.ronscript.duterte.components.ai.steer.SteeringComponent;
import com.ronscript.duterte.utils.Mapper;

/**
 * Copyright (C) 2016 Duterte on 9/4/2016
 *
 * for observation
 * by Ron
 */
public class AutonomousSystem extends EntitySystem {

    private Family stateMachineFamily = Family.all(StateMachineComponent.class).get();
    private Family steeringFamily = Family.all(SteeringComponent.class).get();
    private Family messagingFamily = Family.all(MessagingComponent.class).get();

    private ImmutableArray<Entity> stateMachineEntities;
    private ImmutableArray<Entity> steeringEntities;
    private ImmutableArray<Entity> messagingEntities;

    public AutonomousSystem() {
    }

    @Override
    public void addedToEngine(Engine engine) {
        stateMachineEntities = engine.getEntitiesFor(stateMachineFamily);
        steeringEntities = engine.getEntitiesFor(steeringFamily);
        messagingEntities = engine.getEntitiesFor(messagingFamily);
    }

    @Override
    public void removedFromEngine(Engine engine) {
        stateMachineEntities = null;
        steeringEntities = null;
        messagingEntities = null;
    }

    @Override
    public void update(float deltaTime) {
//        GdxAI.getTimepiece().update(deltaTime);
//        processStateMachineEntities(deltaTime);
//        processMessagingEntities(deltaTime);
//        processSteeringEntities(deltaTime);
    }

    private void processStateMachineEntities(float deltaTime) {
        for (Entity entity: stateMachineEntities){
            StateMachineComponent stateMachine = Mapper.stateMachine.get(entity);
            stateMachine.fsm.update();
        }
    }
    private void processMessagingEntities(float deltaTime) {
        for (Entity entity: messagingEntities){
            MessagingComponent messaging = Mapper.messaging.get(entity);
        }
    }
    private void processSteeringEntities(float deltaTime) {
        for (Entity entity: steeringEntities){
            SteeringComponent steering = Mapper.steering.get(entity);
            steering.agent.update(deltaTime);
        }
    }

}
