package com.ronscript.duterte.systems.game.objects;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.ai.fma.Formation;
import com.badlogic.gdx.ai.fma.FreeSlotAssignmentStrategy;
import com.badlogic.gdx.ai.fma.SlotAssignmentStrategy;
import com.badlogic.gdx.ai.fma.patterns.DefensiveCircleFormationPattern;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.ronscript.duterte.DebugDrawable;
import com.ronscript.duterte.GameWorld;
import com.ronscript.duterte.ai.fsm.Police;
import com.ronscript.duterte.ai.fsm.PoliceState;
import com.ronscript.duterte.ai.steer.PoliceSteeringAgent;
import com.ronscript.duterte.ai.steer.box2d.AgentLocation;
import com.ronscript.duterte.components.ai.fsm.StateMachineComponent;
import com.ronscript.duterte.components.ai.steer.SteeringComponent;
import com.ronscript.duterte.components.game.objects.character.CriminalComponent;
import com.ronscript.duterte.components.game.objects.character.PoliceComponent;
import com.ronscript.duterte.components.game.objects.character.VisionComponent;
import com.ronscript.duterte.components.game.objects.character.pc.PlayerComponent;
import com.ronscript.duterte.systems.physics.CollisionSystem;
import com.ronscript.duterte.utils.Mapper;

/**
 * Copyright (C) 2016 Duterte on 9/7/2016
 * by Ron
 */
public class PoliceSystem extends IteratingSystem implements EntityListener, VisionSystem.VisionFilter, DebugDrawable {

    private static Family policeFamily = Family.all(PoliceComponent.class).get();
    private static Family criminalFamily = Family.all(CriminalComponent.class).get();
    private static Family playerFamily = Family.all(PlayerComponent.class).get();

    private ImmutableArray<Entity> criminalEntities;

    private CollisionSystem collisionSystem;
    private VisionSystem visionSystem;
    private WeaponSystem weaponSystem;

    private Vector2 target = new Vector2();

    private AgentLocation leaderLocation = new AgentLocation();
//    private Array<Vector2> leaderWayPoints = new Array<Vector2>();
//    private Array<Steerable<Vector2>> agents = new Array<Steerable<Vector2>>();

    // Global behaviors
    private Formation<Vector2> formation;

    public PoliceSystem() {
        super(policeFamily);

        DefensiveCircleFormationPattern<Vector2> defensiveCirclePattern = new DefensiveCircleFormationPattern<Vector2>(1);
        // Create the slot assignment strategy
        SlotAssignmentStrategy<Vector2> slotAssignmentStrategy = new FreeSlotAssignmentStrategy<Vector2>();
        formation = new Formation<Vector2>(leaderLocation, defensiveCirclePattern, slotAssignmentStrategy);

    }

//    public Array<Steerable<Vector2>> getAgents() {
//        return agents;
//    }

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);
        criminalEntities = engine.getEntitiesFor(criminalFamily);
        collisionSystem = engine.getSystem(CollisionSystem.class);
        weaponSystem = engine.getSystem(WeaponSystem.class);
        visionSystem = engine.getSystem(VisionSystem.class);
        engine.addEntityListener(policeFamily, this);
    }

    @Override
    public void removedFromEngine(Engine engine) {
        super.removedFromEngine(engine);
        criminalEntities = null;
        collisionSystem = null;
        weaponSystem = null;
        visionSystem = null;
        engine.removeEntityListener(this);
    }

    @Override
    public void entityAdded(Entity entity) {
        visionSystem.putFilter(entity, this);
//        SteeringAgent agent  = Mapper.agent.get(entity).agent;
//        formation.addMember((FormationMember<Vector2>) agent);
//        agents.add(agent);
//        PoliceComponent police = Mapper.police.get(entity);
//        police.leader = GameWorld.playerCharacter;
    }

    @Override
    public void entityRemoved(Entity entity) {
        visionSystem.removeFilter(entity);
//        SteeringAgent agent  = Mapper.agent.get(entity).agent;
//        formation.removeMember((FormationMember<Vector2>) agent);
//        agents.removeValue(agent, true);
    }

    @Override
    public boolean shouldSee(Entity observable) {
        return criminalFamily.matches(observable);
//        return criminalFamily.matches(observable) || playerFamily.matches(observable);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        // update police not related to AI
        updatePolice(entity);
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        leaderLocation.getPosition().set(Mapper.transform.get(GameWorld.playerCharacter).position);
        formation.updateSlots();
//        leaderWayPoints.add(leaderLocation.getPosition());
    }

    public void updatePolice(Entity entity) {
        StateMachineComponent<Police, PoliceState> sm = Mapper.stateMachine.get(entity);
        PoliceComponent police = Mapper.police.get(entity);
        // update weapon

            if (weaponSystem.hasWeapon(entity)) {
                Entity target = visionSystem.getFirstVisionFromEntities(entity, criminalEntities);

                police.target = target;

                if (target != null) {
                    Vector2 criminalPos = Mapper.transform.get(target).position;
                    this.target.set(criminalPos.x, criminalPos.y); // foot position
                    weaponSystem.useWeapon(entity, this.target);
                } else {
                    weaponSystem.haltWeapon(entity);
                }
            }



        // update steering behaviors
            SteeringComponent<PoliceSteeringAgent> steering = Mapper.steering.get(entity);
            if(police.target != null) {
                steering.agent.pursue(Mapper.steering.get(police.target).agent);
            } else {
                steering.agent.wander();
            }

        // update vision
        if(Mapper.vision.has(entity)) {
            VisionComponent vision = Mapper.vision.get(entity);
            if(steering.agent.pursue.isEnabled()) {
                vision.facingAngle = steering.agent.tmp.cpy().nor().angle();
            } else {
                vision.facingAngle = steering.agent.getLinearVelocity().nor().angle();
            }
        }
    }


    @Override
    public void debugDraw(ShapeRenderer shape, float deltaTime) {
        shape.begin();
//        DebugShape.debugDrawPath(shape, leaderWayPoints, false);
        shape.end();
    }
}
