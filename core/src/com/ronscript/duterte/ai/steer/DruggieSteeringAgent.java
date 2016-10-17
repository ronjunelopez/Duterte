package com.ronscript.duterte.ai.steer;

import com.badlogic.gdx.ai.steer.Steerable;
import com.badlogic.gdx.ai.steer.behaviors.Arrive;
import com.badlogic.gdx.ai.steer.behaviors.BlendedSteering;
import com.badlogic.gdx.ai.steer.behaviors.Face;
import com.badlogic.gdx.ai.steer.behaviors.Hide;
import com.badlogic.gdx.ai.steer.behaviors.LookWhereYouAreGoing;
import com.badlogic.gdx.ai.steer.behaviors.Pursue;
import com.badlogic.gdx.ai.steer.behaviors.RaycastObstacleAvoidance;
import com.badlogic.gdx.ai.steer.behaviors.Wander;
import com.badlogic.gdx.ai.steer.limiters.LinearAccelerationLimiter;
import com.badlogic.gdx.ai.steer.limiters.NullLimiter;
import com.badlogic.gdx.ai.steer.proximities.InfiniteProximity;
import com.badlogic.gdx.ai.steer.utils.rays.CentralRayWithWhiskersConfiguration;
import com.badlogic.gdx.ai.steer.utils.rays.RayConfigurationBase;
import com.badlogic.gdx.ai.utils.Location;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.ronscript.duterte.DebugDrawable;
import com.ronscript.duterte.ai.steer.box2d.AgentLocation;
import com.ronscript.duterte.ai.steer.box2d.Box2dRaycastWallCollisionDetector;
import com.ronscript.duterte.utils.DebugShape;
import com.ronscript.duterte.utils.SteeringUtils;

/**
 * Copyright (C) 2016 Duterte on 9/1/2016
 * by Ron
 */
public class DruggieSteeringAgent extends SteeringAgent implements DebugDrawable {

    public Arrive<Vector2> arrive;
    public Wander<Vector2> wander;
    public Face<Vector2> face;
    public Hide<Vector2> hide;
    public LookWhereYouAreGoing<Vector2> lookWhereYouAreGoing;
    public Pursue<Vector2> pursue;
    public RaycastObstacleAvoidance<Vector2> raycastObstacleAvoidance;
//    public CollisionAvoidance<Vector2> collisionAvoidance;
//    public FieldOfViewProximity<Vector2> fov;

//    private World world;

//    public final float FOV_RADIUS = 4;
//    public final float FOV_ANGLE = 180 * MathUtils.degreesToRadians;

    public Location<Vector2> target = new AgentLocation(0, 0);
    public boolean hiding;
    public float THREAT_RADIUS = 4;

    RayConfigurationBase rayConfigurationBase;
    Array<Steerable<Vector2>> agents;
    World world;

    public DruggieSteeringAgent(Array<Steerable<Vector2>> agents, World world, Body body, boolean independentFacing, float boundingRadius) {
        super(body, independentFacing, boundingRadius);
        this.agents = agents;
        this.world = world;
        createBehaviors();
    }

    private void createBehaviors() {
        setMaxLinearSpeed(1);
        setMaxLinearAcceleration(0.2f);
        setMaxAngularAcceleration(.5f); // greater than 0 because independent facing is enabled
        setMaxAngularSpeed(1);
//        getBody().setFixedRotation(true);
//        fov = new FieldOfViewProximity<Vector2>(this, agents, FOV_RADIUS, FOV_ANGLE );

//        fov = new Box2dFieldOfViewProximity(this, world, FOV_RADIUS,FOV_ANGLE);

        arrive = SteeringUtils.createArrive(this, target);
        wander = createWander(this);
        face = new Face<Vector2>(this);
        face.setEnabled(false);

        raycastObstacleAvoidance = createObstacleAvoidance(this);

//        collisionAvoidance = new CollisionAvoidance<Vector2>(this, fov);

        InfiniteProximity<Vector2> inf = new InfiniteProximity(this, agents);
        hide = SteeringUtils.createHide(this, target, inf);

        lookWhereYouAreGoing = new LookWhereYouAreGoing<Vector2>(this);
        lookWhereYouAreGoing.setTimeToTarget(0.1f) //
                .setAlignTolerance(0.001f) //
                .setDecelerationRadius(MathUtils.PI / 3);

        BlendedSteering<Vector2> blended = new BlendedSteering<Vector2>(this);
        blended.setLimiter(NullLimiter.NEUTRAL_LIMITER); //
        blended.add(raycastObstacleAvoidance, 1.5f);
//        blended.add(hide, 1);
        blended.add(wander, 1f);
        blended.add(lookWhereYouAreGoing, 1f);
//        blended.add(face, 1);

//        blended.add(hide, 1);
        setBehavior(blended);
    }
    public RaycastObstacleAvoidance<Vector2> createObstacleAvoidance(Steerable<Vector2> agent) {
        rayConfigurationBase =  new CentralRayWithWhiskersConfiguration<Vector2>(this, 1, 1, 45 * MathUtils.degreesToRadians);
        Box2dRaycastWallCollisionDetector collisionDetector = new Box2dRaycastWallCollisionDetector(world);
        return  new RaycastObstacleAvoidance<Vector2>(agent, rayConfigurationBase ,
                collisionDetector, 1);
    }


    public Wander<Vector2> createWander(Steerable<Vector2> agent) {
        return new Wander<Vector2>(agent)
                .setLimiter(new LinearAccelerationLimiter(0.2f)) //
                .setWanderOffset(1) //
                .setWanderOrientation(180) //
                .setWanderRadius(2) //
                .setWanderRate(MathUtils.PI2 * 6);
    }

    @Override
    public void update(float delta) {
        super.update(delta);
//        if(hide.getTarget() == null) {
//            return;
//        }
//        hiding = (hide.getTarget().getPosition().dst2(getPosition()) < THREAT_RADIUS * THREAT_RADIUS);
//        hide.setEnabled(hiding);
//        wander.setEnabled(!hiding);
    }

    @Override
    public void debugDraw(ShapeRenderer shape, float deltaTime) {
        shape.begin();
        // bouding radius
//        shape.setColor(Color.GOLD);
//        shape.circle(getPosition().x, getPosition().y, getBoundingRadius() + 4, 12);

//        DebugShape.debugDrawHide(shape, hide, THREAT_RADIUS, hiding);
//        DebugShape.drawFov(shape, fov.getOwner().getPosition(),
//                fov.getOwner().getOrientation(),
//                fov.getAngle(),
//                fov.getRadius(), true);
        DebugShape.drawRays(shape, rayConfigurationBase.getRays());

        shape.end();
    }
}
