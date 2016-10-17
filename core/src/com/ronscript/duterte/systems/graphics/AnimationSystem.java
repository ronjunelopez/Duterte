package com.ronscript.duterte.systems.graphics;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.ronscript.duterte.components.graphics.AnimationComponent;
import com.ronscript.duterte.components.graphics.SpriteComponent;
import com.ronscript.duterte.components.graphics.StateComponent;
import com.ronscript.duterte.utils.Mapper;

/**
 * @author Ron
 * @since 8/8/2016
 */
public class AnimationSystem extends IteratingSystem {

    public AnimationSystem() {
        super(Family.all(SpriteComponent.class, AnimationComponent.class, StateComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        AnimationComponent animation = Mapper.animation.get(entity);
        StateComponent state = Mapper.state.get(entity);
        SpriteComponent sprite = Mapper.sprite.get(entity);

        if(animation.map.containsKey(state.get())) {
            sprite.textureRegion = animation.map.get(state.get()).getKeyFrame(state.time, animation.looping);
        }

        state.time += deltaTime;
    }
}
