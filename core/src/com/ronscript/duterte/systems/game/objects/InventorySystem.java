package com.ronscript.duterte.systems.game.objects;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectSet;
import com.ronscript.duterte.components.game.objects.InventoryComponent;
import com.ronscript.duterte.components.properties.AttachmentComponent;
import com.ronscript.duterte.utils.Mapper;

/**
 * Copyright (C) 2016 Duterte on 10/14/2016
 * by Ron
 */
public class InventorySystem extends IteratingSystem implements EntityListener {

    private ObjectMap<Entity, ObjectSet<Entity>> inventory = new ObjectMap<Entity, ObjectSet<Entity>>();

    public InventorySystem() {
        super(Family.all(InventoryComponent.class, AttachmentComponent.class).get());
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
        inventory.put(Mapper.attachment.get(entity).owner, new ObjectSet<Entity>());
    }

    @Override
    public void entityRemoved(Entity entity) {
        inventory.remove(Mapper.attachment.get(entity).owner);
    }


    public boolean addItemToInventory(Entity owner, Entity item) {
        ObjectSet<Entity> items = inventory.get(owner);
        if(items == null) {
            throw new IllegalAccessError("Owner has no inventory");
        }
        if(owner == item) {
            throw new IllegalArgumentException("Owner must not be equal to item");
        }
        return items.add(item);
    }

    public boolean removeItemFromInventory(Entity owner, Entity item) {
        ObjectSet<Entity> items = inventory.get(owner);
        if(items == null) {
            throw new IllegalAccessError("Owner has no inventory");
        }
        if(owner == item) {
            throw new IllegalArgumentException("Owner must not be equal to item");
        }
        return items.remove(item);
    }

    public void getItems(Entity owner) {

    }

    private void updateInventory() {

    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        updateInventory();
    }
}
