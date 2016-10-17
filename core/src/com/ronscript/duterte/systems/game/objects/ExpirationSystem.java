package com.ronscript.duterte.systems.game.objects;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.ronscript.duterte.components.game.objects.ExpirationComponent;
import com.ronscript.duterte.utils.EntityManager;
import com.ronscript.duterte.utils.Mapper;

/**
 * Copyright (C) 2016 Duterte on 8/14/2016
 * by Ron
 */
public class ExpirationSystem extends IteratingSystem {

    public ExpirationSystem() {
        super(Family.all(ExpirationComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        ExpirationComponent timer = Mapper.timer.get(entity);

        if(timer.start && timer.duration > 0.0f) {
            timer.time += deltaTime;
            if(timer.time >= timer.duration) {
                timer.finished = true;
                timer.start = false;
                if(timer.removeOnFinished) {
                    EntityManager.safeDestroy(entity);
                }
            }
        }

    }
}
