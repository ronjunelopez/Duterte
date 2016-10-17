package com.ronscript.duterte.systems.game.objects;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Logger;
import com.badlogic.gdx.utils.ObjectMap;
import com.ronscript.duterte.DebugDrawable;
import com.ronscript.duterte.components.game.objects.character.CharacterComponent;
import com.ronscript.duterte.components.game.objects.character.VisionComponent;
import com.ronscript.duterte.systems.physics.CollisionSystem;
import com.ronscript.duterte.utils.Constants;
import com.ronscript.duterte.utils.DebugShape;
import com.ronscript.duterte.utils.Mapper;
import com.ronscript.duterte.utils.NearbyDistanceSquaredComparator;
import com.ronscript.duterte.utils.WorldManager;

/**
 * Copyright (C) 2016 Duterte on 9/14/2016
 *
 * Vision System for character entities
 * by Ron
 */
public class VisionSystem extends IteratingSystem implements EntityListener, DebugDrawable {


    public static CollisionSystem collisionSystem;
    private final Vector2 toObservable = new Vector2();
    private final Vector2 tmp = new Vector2();
    private final Vector2 angleToVector = new Vector2();
    private final NearbyDistanceSquaredComparator comparator = new NearbyDistanceSquaredComparator();
    private final Array<Entity> tmpVision = new Array<Entity>();
    private Logger logger;
    private Family characterFamily = Family.all(CharacterComponent.class).get();
    private ImmutableArray<Entity> characterEntities;
    private ObjectMap<Entity, Array<Entity>> vision = new ObjectMap<Entity, Array<Entity>>();
    private ObjectMap<Entity, VisionFilter> visionFilter = new ObjectMap<Entity, VisionFilter>();
    private LineOfSight lineOfSight  = new LineOfSight();
    private VisionProximityCallback proximityCallback = new VisionFieldOfView();
    private boolean shouldSortVision = true;
    private boolean useFov = true;

    public VisionSystem() {
        this(Logger.INFO);
    }

    public VisionSystem(int loggerLevel) {
        super(Family.all(VisionComponent.class).get());
        logger = new Logger("Vision System", loggerLevel);
    }

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);
        engine.addEntityListener(characterFamily, this);
        collisionSystem = engine.getSystem(CollisionSystem.class);
        characterEntities = engine.getEntitiesFor(characterFamily);
    }

    @Override
    public void removedFromEngine(Engine engine) {
        super.removedFromEngine(engine);
        engine.removeEntityListener(this);
        collisionSystem = null;
        characterEntities = null;
    }

    @Override
    public void entityAdded(Entity entity) {
        vision.put(entity, new Array<Entity>());
    }

    @Override
    public void entityRemoved(Entity entity) {
        vision.remove(entity);
    }

    public boolean addToVision(Entity owner, Entity observable) {
        if(!inVision(owner, observable)) {
            shouldSortVision = true;
            vision.get(owner).add(observable);
            return true;
        }
        return false;
    }

    public boolean removeFromVision(Entity owner, Entity observable) {
        if(vision.get(owner).removeValue(observable, true)) {
            shouldSortVision = true;
            return true;
        }
        return false;
    }

    public boolean inVision(Entity owner, Entity target) {
        return vision.get(owner).contains(target, true);
    }

    public boolean hasVision(Entity owner) {
        return vision.get(owner).size > 0;
    }

    public Array<Entity> getVision(Entity owner) {
        return vision.get(owner);
    }

    public boolean removeVisionFromOwner (Entity observable) {
        for (Entity observer: getEntities()) { // vision entities
            if(vision.get(observer).contains(observable, true)) {
                return removeFromVision(observer, observable);
            }
        }
        return false;
    }

    private void sortVision(Entity owner) {
        if(shouldSortVision) {
            comparator.owner = owner;
            vision.get(owner).sort(comparator);
            shouldSortVision = false;
        }
    }

    public Entity getFirstVision(Entity owner) {
        Array<Entity> v = vision.get(owner);
        if(v.size == 0) {
            return null;
        }
        return v.first();
    }

    public Entity getLastVision(Entity owner) {
        Array<Entity> v = vision.get(owner);
        if(v.size == 0) {
            return null;
        }
        return v.peek();
    }

    public Entity getFirstVisionFromFamily(Entity owner, Family family) {
        Array<Entity> v = vision.get(owner);
        if(v.size == 0) {
            return null;
        }

        for (Entity e : v) {
            if(family.matches(e)) {
                return e;
            }
        }
        return null;
    }

    public Entity getFirstVisionFromEntities(Entity owner, ImmutableArray<Entity> entities) {
        Array<Entity> v = vision.get(owner);
        if(v.size == 0) {
            return null;
        }

        for (Entity e : v) {
            if(entities.contains(e, true)) {
                return e;
            }
        }
        return null;
    }

    public Entity getLastVisionFromEntities(Entity owner, ImmutableArray<Entity> entities) {
        Array<Entity> v = vision.get(owner);
        if(v.size == 0) {
            return null;
        }

        tmpVision.clear();
        tmpVision.addAll(v);
        tmpVision.reverse();

        for (Entity e : tmpVision) {
            if(entities.contains(e, true)) {
                return e;
            }
        }
        return null;
    }


    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        removeVisionNotExistsFromEntities(entity, characterEntities);
        comparator.entities = characterEntities;
        sortVision(entity);
        updateVision(entity, characterEntities);
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
    }

    public void removeFilter (Entity owner) {
        visionFilter.remove(owner);
    }

    public void putFilter(Entity owner, VisionFilter filter) {
        visionFilter.put(owner, filter);
    }

    private boolean isLineHit(Entity owner, Entity target) {
        Vector2 ownerPosition = Mapper.transform.get(owner).position;
        Vector2 targetPosition =  Mapper.transform.get(target).position;

//        Vector2 ownerFootPosition = tmp.cpy().set(ownerPosition.x, ownerPosition.y - 0.5f);
//        Vector2 targetFootPosition = tmp.cpy().set(targetPosition.x, targetPosition.y - 0.5f);
        lineOfSight.prepare(owner, target, ownerPosition, targetPosition);
        if(!lineOfSight.isValid()) {
            return false;
        }
        lineOfSight.cast();
        // check if the line of sight has detected any entity
        return lineOfSight.isHit();
    }

    /**
     * @see <a href="http://blog.wolfire.com/2009/07/linear-algebra-for-game-developers-part-2/">reference</a>
     * @param observer is the entity owner of vision
     * @param observable is the entity target
     * @return true if the observer is in the FOV otherwise return false
     */
    private boolean inFov(Entity observer, Entity observable) {
        VisionComponent vision = Mapper.vision.get(observer);
        Vector2 start = Mapper.transform.get(observer).position;
        Vector2 end = Mapper.transform.get(observable).position;
        Vector2 targetOffset = toObservable.set(end).sub(start);
        Vector2 targetDestination = targetOffset.cpy().nor();
        float targetLen2 = targetOffset.len2();

        if(observer == observable
                || start.isZero()
                || end.isZero()
                || targetLen2 > vision.distance * vision.distance) {
            return false;
        }

        float angleRad = vision.fovAngle * 0.5f * MathUtils.degreesToRadians;
        // orientation should be normalize, assume this is normalize
        Vector2 ownerOrientation = angleToVector2(angleToVector, vision.facingAngle);
        float product = ownerOrientation.dot(targetDestination);
        float checkAngle = (float) Math.acos(product);
        return checkAngle < angleRad;
    }

    public boolean inRadius(Entity owner, Entity target) {
        VisionComponent vision = Mapper.vision.get(owner);
        Vector2 start = Mapper.transform.get(owner).position;
        Vector2 end = Mapper.transform.get(target).position;
        Vector2 targetOffset = toObservable.set(end).sub(start);
        float targetLen2 = targetOffset.len2();

        return (owner == target
                || start.isZero()
                || end.isZero()
                || targetLen2 > vision.distance * vision.distance);
    }

    /**
     * Convert degree angle to counter clock-wise vector
     * @param angleDeg is  a float angle in degrees
     * @return filled output of vector
     */
    public Vector2 angleToVector2(Vector2 out, float angleDeg) {
        if(out == null) out = new Vector2();
        float angleRad = angleDeg * MathUtils.degreesToRadians;
        return out.set((float)Math.cos(angleRad), (float) Math.sin(angleRad));
    }

    /**
     * TODO: https://en.wikipedia.org/wiki/Quadtree
     * @param observer entity key that will be stored in vision
     * @param observables is an ImmutableArray of Entity
     */
    private void updateVision(Entity observer, ImmutableArray<Entity> observables) {
        for (Entity observable: observables) {
            boolean shouldSee = true;
            boolean inProximity;

            if(visionFilter.containsKey(observer)) {
                shouldSee =  visionFilter.get(observer).shouldSee(observable);
            }

            inProximity = proximityCallback.inProximity(observer, observable);

            Array<Entity> observerVisions = vision.get(observer);

            if(shouldSee && inProximity && isLineHit(observer, observable)) {
                if(!observerVisions.contains(observable, true)) {
                    shouldSortVision = true;
                    observerVisions.add(observable);
                    logger.info("queue, total " + observerVisions.size);
                }
            } else {
                if(observerVisions.removeValue(observable, true)) {
                    shouldSortVision = true;
                    logger.info("removed, total " + observerVisions.size);
                }
            }

        }
    }

    private boolean removeVisionNotExistsFromEntities(Entity observer, ImmutableArray<Entity> observables) {
        Array<Entity> observerVisions = vision.get(observer);
        if(observerVisions == null || observerVisions.size == 0) {
            return false;
        }
        for (Entity observable: observerVisions) {
            if(!observables.contains(observable, true) && observerVisions.removeValue(observable, true)) {
                shouldSortVision = true;
                logger.info("not exists from collection, removed, total " + observerVisions.size);
            }
        }
        return false;
    }

    private void drawVisionMap(ShapeRenderer shape) {
        shape.begin();

        for (Entity visionEntity : getEntities()) {
            Vector2 ownerPosition = Mapper.transform.get(visionEntity).position;
            Vector2 ownerLinearVelocity =  Mapper.physics.get(visionEntity).body.getLinearVelocity();

            VisionComponent vis = Mapper.vision.get(visionEntity);

            shape.set(ShapeRenderer.ShapeType.Line);
            if(hasVision(visionEntity)) {
                shape.setColor(Color.RED);
            } else {
                shape.setColor(Color.BLACK);
            }
            // vision distance line
            shape.line(ownerPosition, tmp.cpy().set(ownerLinearVelocity).nor().scl(vis.distance).add(ownerPosition));
            // vision radius or field of view
            if(useFov) {
                DebugShape.drawFov(shape, ownerPosition, vis.facingAngle, vis.fovAngle, vis.distance);
            } else {
                shape.circle(ownerPosition.x, ownerPosition.y, vis.distance, (int) (6 * vis.distance));
            }

        }

        shape.end();
    }

    private void drawLineOfSight(ShapeRenderer shape) {
        shape.begin();
        if(lineOfSight.hit) {
            shape.setColor(Color.GREEN);
        } else {
            shape.setColor(Color.RED);
        }
        shape.line(lineOfSight.start, lineOfSight.end);
        shape.end();
    }

    @Override
    public void debugDraw(ShapeRenderer shape, float deltaTime) {
        drawVisionMap(shape);
        drawLineOfSight(shape);
    }

    public interface VisionProximityCallback {
        boolean inProximity(Entity observer, Entity observable);
    }

    public interface VisionFilter {
        boolean shouldSee (Entity observable);
    }

    public class VisionFieldOfView implements VisionProximityCallback {
        @Override
        public boolean inProximity(Entity observer, Entity observable) {
            return inFov(observer, observable);
        }
    }

    private class LineOfSight implements RayCastCallback {
        private Entity owner;
        private Entity target;
        private Vector2 start = new Vector2();
        private Vector2 end = new Vector2();

        private boolean hit;

        public LineOfSight() {
        }

        public void prepare(Entity owner, Entity target,  Vector2 start, Vector2 end) {
            hit = false;
            if(owner == null || target == null) {
                throw new NullPointerException("Arguments cannot be null");
            }
            this.owner = owner;
            this.target = target;
            this.start.set(start);
            this.end.set(end);
        }

        public boolean isHit() {
            return hit;
        }

        public boolean isValid() {
            return !start.epsilonEquals(end, MathUtils.FLOAT_ROUNDING_ERROR);
        }

        public void cast() {
            WorldManager.getWorld().rayCast(this, start, end);

        }

        @Override
        public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction) {
            hit = false;
            Filter filter = fixture.getFilterData();
            Object fixtureUserdata = fixture.getUserData();
            Object bodyUserdata = fixture.getBody().getUserData();

            if(owner == fixtureUserdata || owner == bodyUserdata || fixture.isSensor()) {
                return -1;
            } else if(fixtureUserdata instanceof  Entity
                    && filter.categoryBits == Constants.CATEGORY_CHARACTER
                   ) {
                hit = target == fixtureUserdata;
                return 1;
            }
            return 0;
        }
    }
}
