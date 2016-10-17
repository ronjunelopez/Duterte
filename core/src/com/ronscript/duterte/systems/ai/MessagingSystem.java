package com.ronscript.duterte.systems.ai;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.ai.msg.MessageManager;
import com.badlogic.gdx.utils.Disposable;
import com.ronscript.duterte.components.ai.msg.MessagingComponent;
import com.ronscript.duterte.utils.Mapper;

/**
 * Copyright (C) 2016 Duterte on 9/5/2016
 * by Ron
 */
public class MessagingSystem extends IteratingSystem implements EntityListener, Disposable {

    private MessageManager manager;

    public MessagingSystem() {
        super(Family.all(MessagingComponent.class).get());
        manager = MessageManager.getInstance();
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        MessagingComponent messaging = Mapper.messaging.get(entity);

    }

    @Override
    public void update(float deltaTime) {
        // to enable message delays
        MessageManager.getInstance().update();
        super.update(deltaTime);
    }

    @Override
    public void entityAdded(Entity entity) {

    }

    @Override
    public void entityRemoved(Entity entity) {

    }

    @Override
    public void dispose() {
        MessageManager.getInstance().clear();
    }
}
