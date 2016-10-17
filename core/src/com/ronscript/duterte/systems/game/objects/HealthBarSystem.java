package com.ronscript.duterte.systems.game.objects;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.ronscript.duterte.components.game.objects.HealthBarComponent;
import com.ronscript.duterte.components.game.objects.character.stats.HealthComponent;
import com.ronscript.duterte.components.properties.AttachmentComponent;
import com.ronscript.duterte.utils.Mapper;

/**
 * Copyright (C) 2016 Duterte on 8/11/2016
 * by Ron
 */
public class HealthBarSystem extends IteratingSystem {

    public HealthBarSystem() {
        super(Family.all(HealthBarComponent.class).get());
    }
    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        HealthBarComponent healthBar = Mapper.healthBar.get(entity);
        AttachmentComponent attachment = Mapper.attachment.get(entity);

        HealthComponent health = Mapper.health.get(attachment.owner);
        if(health == null || health.current <= 0) {
            healthBar.background.removeAll();
            entity.removeAll();
            return;
        }
        Mapper.sprite.get(entity).widthRatio = health.current / health.MAX;
    }

}
