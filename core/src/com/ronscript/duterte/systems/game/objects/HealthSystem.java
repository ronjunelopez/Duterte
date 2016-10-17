package com.ronscript.duterte.systems.game.objects;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.MathUtils;
import com.ronscript.duterte.components.game.objects.character.stats.DamageComponent;
import com.ronscript.duterte.components.game.objects.character.stats.HealthComponent;
import com.ronscript.duterte.components.game.objects.character.stats.RegenerationComponent;
import com.ronscript.duterte.components.properties.TransformComponent;
import com.ronscript.duterte.utils.Mapper;

/**
 * Copyright (C) 2016 Duterte on 8/11/2016
 * by Ron
 */
public class HealthSystem extends IteratingSystem {

    public HealthSystem() {
        super(Family.all(HealthComponent.class, TransformComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        HealthComponent health = Mapper.health.get(entity);
        if(health.current <= 0) {
            health.dead = true;
        }
    }

    public void increaseHealth(Entity caster, Entity healthReceiver) {
        if(Mapper.regeneration.has(caster) && Mapper.health.has(healthReceiver)) {
            RegenerationComponent regeneration = Mapper.regeneration.get(caster);
            HealthComponent health = Mapper.health.get(healthReceiver);
            if(health.current != health.MAX) {
                health.current += MathUtils.clamp(regeneration.current, 0, regeneration.MAX);
            }
        }
    }

    public void decreaseHealth(Entity damageDealer, Entity damageReceiver) {
        if(Mapper.damage.has(damageDealer) && Mapper.health.has(damageReceiver)) {
            DamageComponent damage = Mapper.damage.get(damageDealer);
            HealthComponent health = Mapper.health.get(damageReceiver);
            if(health.current > 0.001f) {
                health.current -= MathUtils.clamp(damage.current, 0, damage.MAX);
            }
        }
    }

}
