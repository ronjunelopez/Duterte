package com.ronscript.duterte.systems.physics;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectSet;
import com.ronscript.duterte.components.physics.CollisionComponent;
import com.ronscript.duterte.components.physics.PhysicsComponent;
import com.ronscript.duterte.physics.EntityContactListener;
import com.ronscript.duterte.utils.Mapper;
import com.ronscript.duterte.utils.WorldManager;

/**
 * @author Ron
 * @since 8/10/2016
 */
public class CollisionSystem extends EntitySystem implements EntityListener, ContactListener {

    private Family collisionFamily = Family.all(CollisionComponent.class, PhysicsComponent.class).get();
    private ImmutableArray<Entity> collisionEntities;
    private ObjectMap<Entity, ObjectSet<Entity>> collision = new ObjectMap<Entity, ObjectSet<Entity>>();
    private ObjectMap<Entity, ObjectSet<Entity>> collisionScheduledForRemoval = new ObjectMap<Entity, ObjectSet<Entity>>();
    private ObjectMap<Entity, EntityContactListener> listeners = new ObjectMap<Entity, EntityContactListener>();

    public CollisionSystem() {
        WorldManager.getWorld().setContactListener(this);
    }

    @Override
    public void addedToEngine(Engine engine) {
        collisionEntities = getEngine().getEntitiesFor(collisionFamily);
        engine.addEntityListener(this);
    }

    @Override
    public void removedFromEngine(Engine engine) {
        collisionEntities = null;
    }

    @Override
    public void entityAdded(Entity entity) {
        collision.put(entity, new ObjectSet<Entity>());
        collisionScheduledForRemoval.put(entity, new ObjectSet<Entity>());
    }

    @Override
    public void entityRemoved(Entity entity) {
        collision.remove(entity);
        collisionScheduledForRemoval.remove(entity);
    }

    private boolean addToCollision(Entity owner, Entity other) {
        return collision.get(owner).add(other);
    }

    private boolean removeFromCollision(Entity owner, Entity other) {
        if(collision.get(owner).remove(other)) {
            collisionScheduledForRemoval.get(owner).add(other);
            return true;
        }
        return false;
    }

    public boolean hasCollision(Entity owner) {
        return collision.get(owner).size > 0;
    }

    public boolean inCollision(Entity owner, Entity other) {
        return collision.get(owner).contains(other);
    }

    public ObjectSet<Entity> getCollision(Entity owner) {
        return collision.get(owner);
    }

    public boolean isScheduleForRemoval(Entity owner, Entity other) {
        return collisionScheduledForRemoval.get(owner).contains(other);
    }

    public void putContactListener(Entity owner, EntityContactListener listener) {
        listeners.put(owner, listener);
    }

    public void removeContactListener(Entity owner) {
        listeners.remove(owner);
    }

    @Override
    public void update(float deltaTime) {
        for (Entity entity: collisionEntities) {
            CollisionComponent collisionComponent = Mapper.collision.get(entity);
            if(listeners.containsKey(entity)) {
                listeners.get(entity).beginContact(entity, collision.get(entity));
                listeners.get(entity).endContact(entity, collisionScheduledForRemoval.get(entity));
            }
            collisionScheduledForRemoval.get(entity).clear();
        }
    }


    public void beginEntityContact(Fixture fixtureA, Fixture fixtureB) {
        int categoryBits = fixtureA.getFilterData().categoryBits | fixtureB.getFilterData().categoryBits;

        if(fixtureA.getUserData() instanceof Entity && fixtureB.getUserData() instanceof Entity) {
            Entity entityA = (Entity) fixtureA.getUserData();
            Entity entityB = (Entity) fixtureB.getUserData();
            addToCollision(entityA, entityB);
            addToCollision(entityB, entityA);
        }
    }

    public void endEntityContact(Fixture fixtureA, Fixture fixtureB) {
        int categoryBits = fixtureA.getFilterData().categoryBits | fixtureB.getFilterData().categoryBits;

        if(fixtureA.getUserData() instanceof Entity && fixtureB.getUserData() instanceof Entity) {
            Entity entityA = (Entity) fixtureA.getUserData();
            Entity entityB = (Entity) fixtureB.getUserData();
            removeFromCollision(entityA, entityB);
            removeFromCollision(entityB, entityA);
        }
    }

    @Override
    public void beginContact(Contact contact) {
        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();
        beginEntityContact(fixtureA, fixtureB);
    }

    @Override
    public void endContact(Contact contact) {
        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();
        endEntityContact(fixtureA, fixtureB);
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }



}
