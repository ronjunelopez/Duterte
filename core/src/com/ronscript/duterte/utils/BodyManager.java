package com.ronscript.duterte.utils;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

/**
 * @author Ron
 * @since 8/10/2016
 */
public class BodyManager {
    private static BodyManager ourInstance = new BodyManager();
//    private final AtomicInteger bodyCounter = new AtomicInteger();
//    private final IntMap<Body> bodiesToUpdate = new IntMap<Body>();
    private final ObjectMap<Entity,Body> bodiesToUpdate = new ObjectMap<Entity, Body>();
    private final Array<Body> bodiesToDestroy = new Array<Body>();
    private World world;

    private BodyManager() {
    }

    public static BodyManager getInstance() {
        return ourInstance;
    }
//    private final ObjectMap<Entity, Integer> userdataIds = new ObjectMap<Entity,Integer>();

    public static void registerWorld(World world) {
        getInstance().world = world;
//        getInstance().bodyCounter.compareAndSet(1000, 0);
    }

//    private int generateEntityId(Entity userdata) {
//        int id = bodyCounter.getAndIncrement();
//        userdataIds.put(userdata, id);
//        return id;
//    }

//    private void removeEntity(Entity userdata) {
//        if(userdataIds.containsKey(userdata)) {
//            userdataIds.remove(userdata);
//        }
//    }

//    private int getEntityId(Entity userdata) {
//        return userdataIds.get(userdata);
//    }

    public void update() {
        for (Body body: bodiesToDestroy) {
//            Entity entity = (Entity) body.getUserData();
//            if(entity.isScheduledForRemoval()) {
                world.destroyBody(body);
//            }
        }
        bodiesToDestroy.clear();
    }

    public void add(Entity entity,Body body) {
//        bodiesToUpdate.put(generateEntityId(userdata), body);
        bodiesToUpdate.put(entity, body);
    }

    public void remove(Entity userdata) {
//        int id = getEntityId(userdata);
        if(bodiesToUpdate.containsKey(userdata)) {
            bodiesToDestroy.add(bodiesToUpdate.get(userdata));
            bodiesToUpdate.remove(userdata);
//            removeEntity(userdata);
        }
    }
}
