package com.ronscript.duterte.systems.game.objects;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.badlogic.gdx.utils.ObjectMap;
import com.ronscript.duterte.components.game.objects.BulletComponent;
import com.ronscript.duterte.components.game.objects.ExpirationComponent;
import com.ronscript.duterte.components.game.objects.WeaponComponent;
import com.ronscript.duterte.components.physics.GravityComponent;
import com.ronscript.duterte.components.physics.MovementComponent;
import com.ronscript.duterte.components.properties.TransformComponent;
import com.ronscript.duterte.utils.Constants;
import com.ronscript.duterte.utils.EntityManager;
import com.ronscript.duterte.utils.Mapper;
import com.ronscript.duterte.utils.WorldManager;

/**
 *  A projectile is any object that is cast, fired, flung, heaved, hurled, pitched, tossed, or thrown. (This is an informal definition.) The path of a projectile is called its trajectory.
 * @author Ron
 * @since 8/8/2016
 */
public class WeaponSystem extends IteratingSystem implements EntityListener, RayCastCallback {

    private static Family weaponFamily = Family.all(WeaponComponent.class).get();
    private final Vector2 tmp = new Vector2();
    private final Vector2 start = new Vector2();
    private final Vector2 end = new Vector2();
    HealthSystem healthSystem;
    DamageSystem damageSystem;
    private ObjectMap<Entity, Entity> weaponOwners = new ObjectMap<Entity, Entity>();
    private float time = 0;
    private float reloadDuration = 0.5f;
    private float launchHeight = 0;

    // RayCast variables
    private float lastDuration = 0;
    private float maxDistanceThreshold = 0.5f;
    private Entity owner;
    private Vector2 point = new Vector2();

    public WeaponSystem() {
        super(weaponFamily);
    }

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);
        healthSystem = engine.getSystem(HealthSystem.class);
        damageSystem = engine.getSystem(DamageSystem.class);
        getEngine().addEntityListener(weaponFamily, this);
    }

    @Override
    public void removedFromEngine(Engine engine) {
        super.removedFromEngine(engine);
        healthSystem = null;
        damageSystem = null;
        getEngine().removeEntityListener(this);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        updateWeapon(entity);
    }

    private void updateWeapon(Entity entity) {
        WeaponComponent weapon = Mapper.weapon.get(entity);
        if(weapon.use) {
            switch (weapon.type) {
                case MELEE:

                    break;
                case RANGE:
                    // set rayCast weapon owner

                    start.set(Mapper.transform.get(entity).position);
                    end.set(weapon.target);
                    // set the point to characters foot
                    tmp.set(end);
                    float dst2 = tmp.dst2(start);
                    if(dst2 >= maxDistanceThreshold) {
                        shoot(entity, tmp);

                        updateWeaponDamage(entity);

                    }
                    break;
            }
        }
    }

    private void updateWeaponDamage(Entity entity) {
        owner = Mapper.attachment.get(entity).owner;
        WeaponComponent weapon = Mapper.weapon.get(entity);
        weapon.hit = null;
        WorldManager.getWorld().rayCast(this, start, end);
        if(weapon.hit != null) {
            EntityManager.getInstance().createExplosion(point.x, point.y + 0.5f);
//            Gdx.app.log("", "hit");
            healthSystem.decreaseHealth(entity, weapon.hit);
//            damageSystem.queueDamage(entity, weapon.hit);
        }

    }


    @Override
    public void update(float deltaTime) {
        time += deltaTime;
        if(time >= reloadDuration) {
            time = 0;
            super.update(deltaTime);
        }

    }

    @Override
    public void entityAdded(Entity entity) {
        weaponOwners.put(Mapper.attachment.get(entity).owner, entity);
    }

    @Override
    public void entityRemoved(Entity entity) {
        weaponOwners.remove(Mapper.attachment.get(entity).owner);
    }

    public boolean hasWeapon(Entity owner) {
        return weaponOwners.containsKey(owner);
    }

    public boolean useWeapon(Entity owner,  Vector2 target) {
        Entity weaponEntity = weaponOwners.get(owner);
        if(weaponEntity == null) {
            return false;
        }
        WeaponComponent weapon = Mapper.weapon.get(weaponOwners.get(owner));
        weapon.use = true;
        weapon.target.set(target);
        return true;
    }

    public boolean isUsing(Entity owner) {
        return Mapper.weapon.get(weaponOwners.get(owner)).use;
    }

    public boolean haltWeapon(Entity owner) {
        Entity weaponEntity = weaponOwners.get(owner);
        if(weaponEntity == null) {
            return false;
        }
        WeaponComponent weapon = Mapper.weapon.get(weaponEntity);
        weapon.use = false;
        weapon.target.setZero();
        return true;
    }

    public Entity getWeapon(Entity owner) {
        return weaponOwners.get(owner);
    }

    /**
     *
     * @param weaponEntity the weapon entity
     * @param targetPoint Vector2 target point
     */
    public void shoot(Entity weaponEntity, Vector2 targetPoint) {
        if(weaponFamily.matches(weaponEntity)) {
            WeaponComponent weapon = Mapper.weapon.get(weaponEntity);
            TransformComponent transform = Mapper.transform.get(weaponEntity);

            float spatialX = transform.position.x;
            float spatialY = transform.position.y;

//            Entity bulletShadow = weapon.factory.createBulletShadow(spatialX,spatialY - 0.5f);
            Entity bulletEntity = weapon.factory.createBullet(spatialX,spatialY + 0.5f, null);
//            Entity bulletEntity = weapon.factory.createBullet(spatialX,spatialY, bulletShadow);

//            if(applyProjectileMotion(bulletEntity, targetPoint , 10))
//                applyLinearMotion(bulletShadow, targetPoint, lastDuration);
            seek(bulletEntity, targetPoint.add(0, 0.5f));
        }
    }

    /** Set the linear velocity of a shadow and set time schedule when to remove
     * @param shadow The shadow
     * @param targetPosition The target current position
     * @param duration The time to reach its destination  */
    public void applyLinearMotion(Entity shadow, Vector2 targetPosition, float duration) {
        ExpirationComponent timer  = Mapper.timer.get(shadow);
//        LifespanComponent lifespan  = Mapper.lifespan.get(shadow);
        TransformComponent transform = Mapper.transform.get(shadow);
//        PhysicsComponent physics = Mapper.physics.get(shadow);

        Vector2 bulletPosition = new Vector2(transform.position.x, transform.position.y);
        Vector2 targetOffset = targetPosition.cpy().sub(bulletPosition);
        Vector2 targetDirection = targetOffset.cpy().nor();


        transform.rotation = targetDirection.angle() * MathUtils.degreesToRadians;

        float targetDistance = targetDirection.cpy().dot(targetOffset);
//        float targetDistance =  targetPosition.dst(bulletPosition);
        float targetSpeed = targetDistance / duration;

        Vector2 targetLinearVelocity = targetDirection.cpy().scl(targetSpeed);
//        physics.body.setLinearVelocity(targetLinearVelocity);
        MovementComponent movement = Mapper.movement.get(shadow);
        movement.velocity.set(targetLinearVelocity);
//        lifespan.duration = duration;
        timer.start(duration);
    }

    public void seek(Entity owner, Vector2 target) {
        TransformComponent ownerT = Mapper.transform.get(owner);
//        TransformComponent targetT = Mapper.transform.get(target);

        Vector2 toTarget = target.cpy().sub(ownerT.position).nor();
        ownerT.rotation = toTarget.angle() * MathUtils.degreesToRadians;
        Mapper.movement.get(owner).velocity.set(toTarget.scl(5));
    }

    /**
     * Calculate the projectile motion and set as linear velocity of a body and also schedule the duration of removal
     * @param entity The entity where to get/set the motion
     * @param targetPosition The target current position
     * @param speed The speed to scale with calculated velocity
     * @return The duration of time to reach its destination*/
    public boolean applyProjectileMotion(Entity entity, Vector2 targetPosition, float speed) {
        ExpirationComponent timer  = Mapper.timer.get(entity);
        BulletComponent bullet = Mapper.bullet.get(entity);
        TransformComponent transform = Mapper.transform.get(entity);
        MovementComponent movement = Mapper.movement.get(entity);
        GravityComponent gravity = Mapper.gravity.get(entity);

        gravity.scale = 1;

        float verticalGravity = gravity.vertical;
//        float launchHeight = 0;

        Vector2 bulletPosition = transform.position;
        Vector2 targetOffset = targetPosition.cpy().sub(bulletPosition);
        Vector2 targetDirection = targetOffset.cpy().nor();
        Vector2 targetLinearVelocity = targetDirection.cpy().scl(speed);

        float targetDistance = targetDirection.cpy().dot(targetOffset);
        float targetDuration = targetDistance / speed;

        float calculatedArc = targetDuration * verticalGravity / 2 - launchHeight / targetDuration;
        Vector2 targetArcVelocity = targetLinearVelocity.add(0.0f, calculatedArc);

        movement.velocity.set(targetArcVelocity);
        bullet.fired = true;
        timer.start(targetDuration - 0.05f);
        lastDuration = targetDuration;
        return true;
    }



    @Override
    public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction) {
        WeaponComponent weapon = Mapper.weapon.get(getWeapon(owner));
        weapon.hit = null;
        if(fixture.getUserData() == owner || fixture.getBody().getUserData() == owner) {
            return -1;
        }

        if(fixture.getFilterData().categoryBits == Constants.CATEGORY_CHARACTER) {
            weapon.hit =  (Entity) fixture.getBody().getUserData();
            this.point = point;
            return 1;
        }
        return 0;
    }
}
