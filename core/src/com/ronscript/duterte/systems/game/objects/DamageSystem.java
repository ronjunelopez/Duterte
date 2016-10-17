package com.ronscript.duterte.systems.game.objects;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.Queue;
import com.ronscript.duterte.components.game.objects.character.stats.DamageComponent;
import com.ronscript.duterte.components.game.objects.character.stats.HealthComponent;

/**
 * Copyright (C) 2016 Duterte on 9/26/2016
 * by Ron
 */
public class DamageSystem extends IteratingSystem implements EntityListener {

    Family healthFamily = Family.all(HealthComponent.class).get();
    ImmutableArray<Entity> healthEntities;
    HealthSystem healthSystem;

    ObjectMap<Entity, Queue<Entity>> damageMap = new ObjectMap<Entity, Queue<Entity>>();



    public DamageSystem() {
        super(Family.all(DamageComponent.class).get());
    }

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);
        healthEntities = engine.getEntitiesFor(healthFamily);
        healthSystem = engine.getSystem(HealthSystem.class);
        engine.addEntityListener(this);
    }

    @Override
    public void removedFromEngine(Engine engine) {
        super.removedFromEngine(engine);
        healthEntities = null;
        healthSystem = null;
        engine.removeEntityListener(this);
    }

    @Override
    public void entityAdded(Entity entity) {
        damageMap.put(entity, new Queue<Entity>());
    }

    @Override
    public void entityRemoved(Entity entity) {
        damageMap.remove(entity);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        updateDamage(entity);
    }

    private void updateDamage(Entity damageDealer) {
        Queue<Entity> queue = damageMap.get(damageDealer);
        if(queue.size > 0) {
            Entity damageReceiver = queue.removeLast();
            healthSystem.decreaseHealth(damageDealer, damageReceiver);
        }

    }

    public void queueDamage(Entity damageDealer, Entity damageReceiver) {
        damageMap.get(damageDealer).addFirst(damageReceiver);
    }

//    public Entity getDmg() {
//        ObjectMap.Entries<Entity, Queue<Entity>> i = damageMap.iterator();
//        while (i.hasNext()) {
//            ObjectMap.Entry<Entity, Queue<Entity>> e = i.next();
//
//        }
//    }

}
