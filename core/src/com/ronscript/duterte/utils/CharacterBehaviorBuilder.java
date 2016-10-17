package com.ronscript.duterte.utils;

import com.badlogic.gdx.ai.steer.Proximity;
import com.badlogic.gdx.ai.steer.Steerable;
import com.badlogic.gdx.ai.steer.SteeringBehavior;
import com.badlogic.gdx.ai.steer.behaviors.Alignment;
import com.badlogic.gdx.ai.steer.behaviors.BlendedSteering;
import com.badlogic.gdx.ai.steer.behaviors.Cohesion;
import com.badlogic.gdx.ai.steer.behaviors.CollisionAvoidance;
import com.badlogic.gdx.ai.steer.behaviors.FollowPath;
import com.badlogic.gdx.ai.steer.behaviors.PrioritySteering;
import com.badlogic.gdx.ai.steer.behaviors.Pursue;
import com.badlogic.gdx.ai.steer.behaviors.RaycastObstacleAvoidance;
import com.badlogic.gdx.ai.steer.behaviors.Separation;
import com.badlogic.gdx.ai.steer.behaviors.Wander;
import com.badlogic.gdx.ai.steer.limiters.LinearAccelerationLimiter;
import com.badlogic.gdx.ai.steer.proximities.RadiusProximity;
import com.badlogic.gdx.ai.steer.utils.paths.LinePath;
import com.badlogic.gdx.ai.steer.utils.rays.CentralRayWithWhiskersConfiguration;
import com.badlogic.gdx.ai.steer.utils.rays.RayConfigurationBase;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.ronscript.duterte.ai.steer.ArriveBehind;
import com.ronscript.duterte.ai.steer.AvoidWall;
import com.ronscript.duterte.ai.steer.EvadeAhead;
import com.ronscript.duterte.ai.steer.box2d.Box2dRadiusProximity;
import com.ronscript.duterte.ai.steer.box2d.Box2dRaycastWallCollisionDetector;

/**
 * Copyright (C) 2016 Duterte on 10/4/2016
 * by Ron
 */
public class CharacterBehaviorBuilder {

    final World world;
    private Steerable<Vector2> owner;
    private PrioritySteering<Vector2> priority;
    private ArriveBehind<Vector2> arriveBehind;
    private EvadeAhead<Vector2> evadeAhead;
    private Separation<Vector2> separation;
    private Wander<Vector2> wander;
    private Proximity<Vector2> proximity;

    public CharacterBehaviorBuilder(Steerable<Vector2> owner, World world) {
        this.owner = owner;
        this.world = world;
        priority = new PrioritySteering<Vector2>(this.owner);
    }

    public void add(SteeringBehavior<Vector2> behavior) {
        priority.add(behavior);
    }

    public SteeringBehavior<Vector2> get() {
        return priority;
    }

    public void createCollisionAvoidance(float radius, Array<Steerable<Vector2>> agents) {
        Proximity<Vector2> proximity = new RadiusProximity<Vector2>(owner, agents, radius);
        CollisionAvoidance<Vector2> collisionAvoidance = new CollisionAvoidance<Vector2>(owner, proximity);
        priority.add(collisionAvoidance);
    }

    public Wander<Vector2> createWander() {
        Wander<Vector2> wander =  new Wander<Vector2>(owner)
                .setLimiter(new LinearAccelerationLimiter(0.2f)) //
                .setWanderOffset(1) //
                .setWanderOrientation(180) //
                .setWanderRadius(5) //
                .setWanderRate(MathUtils.PI2 * 4);
        priority.add(wander);
        return wander;
    }

    public BlendedSteering<Vector2> createFollowPlayer(Steerable<Vector2> player) {

        BlendedSteering<Vector2> followLeader  = new BlendedSteering<Vector2>(owner);
        proximity = new Box2dRadiusProximity(owner, world, 10);

        ArriveBehind<Vector2> arriveBehind = new ArriveBehind<Vector2>(owner, player);
        EvadeAhead<Vector2> evadeAhead = new EvadeAhead<Vector2>(owner, player);

        Separation<Vector2> separation = new Separation<Vector2>(owner, proximity);
        Cohesion<Vector2> cohesion = new Cohesion<Vector2>(owner, proximity);
        Alignment<Vector2> alignment = new Alignment<Vector2>(owner, proximity);
        AvoidWall avoidWall = new AvoidWall(owner);


//        evadeAhead.setAheadRadius(5);
//        separation.setDecayCoefficient(100);
//        arriveBehind.setLimiter(new LinearLimiter(0.5f, 4));
        arriveBehind.setTimeToTarget(0.1f);
        arriveBehind.setArrivalTolerance(0.001f);

//        followLeader.add(new Pursue<Vector2>(owner, player), 1);
        followLeader.add(arriveBehind, 1);
        followLeader.add(evadeAhead, 1);
//        followLeader.add(createObstacleAvoidance(), 0.5f);
        followLeader.add(cohesion, 0.3f);
        followLeader.add(alignment, 0.1f);
        followLeader.add(separation, 1f);

        priority.add(followLeader);
//        priority.add(separation);
        return followLeader;
    }

    public RaycastObstacleAvoidance<Vector2> createObstacleAvoidance() {
        RayConfigurationBase rayConfigurationBase =  new CentralRayWithWhiskersConfiguration<Vector2>(owner, 0.5f,0.5f, 45 * MathUtils.degreesToRadians);
        Box2dRaycastWallCollisionDetector collisionDetector = new Box2dRaycastWallCollisionDetector(WorldManager.getWorld());
        RaycastObstacleAvoidance roa =  new RaycastObstacleAvoidance<Vector2>(owner, rayConfigurationBase ,
                collisionDetector, 0);
        priority.add(roa);
        return roa;
    }

    public Pursue<Vector2> createPursue(Steerable<Vector2> target) {
        Pursue<Vector2> pursue = new Pursue<Vector2>(owner, target);
        priority.add(pursue);
        return pursue;
    }

    public FollowPath createFollowPath(Array<Vector2> path) {
        LinePath<Vector2> linePath = new LinePath<Vector2>(path, true);
        FollowPath<Vector2, LinePath.LinePathParam> followPath
                = new FollowPath<Vector2, LinePath.LinePathParam>(owner, linePath, 0.1f)
                // Setters below are only useful to arrive at the end of an open path
                .setTimeToTarget(1) //
                .setArrivalTolerance(0.001f) //
                .setDecelerationRadius(0.5f);
//        LookWhereYouAreGoing<Vector2> lookWhereYouAreGoing = new LookWhereYouAreGoing<Vector2>(owner);
//
//        BlendedSteering<Vector2> b = new BlendedSteering<Vector2>(owner);
//
//        b.add(followPath, 1);
//        b.add(lookWhereYouAreGoing, 1);

        priority.add(followPath);

        return followPath;
    }


}
