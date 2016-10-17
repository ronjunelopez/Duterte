package com.ronscript.duterte.utils;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Body;
import com.ronscript.duterte.components.game.objects.character.CharacterComponent;
import com.ronscript.duterte.components.game.objects.character.VisionComponent;
import com.ronscript.duterte.components.game.objects.character.stats.HealthComponent;
import com.ronscript.duterte.components.graphics.AnimationComponent;
import com.ronscript.duterte.components.graphics.SpriteComponent;
import com.ronscript.duterte.components.graphics.StateComponent;
import com.ronscript.duterte.components.physics.CollisionComponent;
import com.ronscript.duterte.components.physics.PhysicsComponent;
import com.ronscript.duterte.components.properties.SizeComponent;
import com.ronscript.duterte.components.properties.TransformComponent;

/**
 * Copyright (C) 2016 Duterte on 10/9/2016
 * by Ron
 */
public class CharacterBuilder {

    final private PooledEngine engine;
    final private EntityManager manager;
    private Entity entity;

    private VisionComponent vision;
    private CharacterComponent character;
    private HealthComponent health;
    private TransformComponent transform;
    private SizeComponent size;
    private SpriteComponent sprite;
    private StateComponent state;
    private AnimationComponent animation;
    private CollisionComponent collision;
    private PhysicsComponent physics;

    public CharacterBuilder(EntityManager manager) {
        this.manager = manager;
        this.engine = manager.engine;
        createComponents();
    }


    /**
     * create default components
     */
    private void createComponents () {
        // Game Objects
        vision = engine.createComponent(VisionComponent.class);
        character = engine.createComponent(CharacterComponent.class);
        health = engine.createComponent(HealthComponent.class);
        // Properties
        transform = engine.createComponent(TransformComponent.class);
        size = engine.createComponent(SizeComponent.class);
        // Graphics
        sprite = engine.createComponent(SpriteComponent.class);
        state = engine.createComponent(StateComponent.class);
        animation = engine.createComponent(AnimationComponent.class);
        // Physics
        collision = engine.createComponent(CollisionComponent.class);
        physics = engine.createComponent(PhysicsComponent.class);
    }


    public void setTexture(TextureRegion textureRegion) {
        sprite.textureRegion = textureRegion;
    }

    public void setPosition(float x, float y) {
        transform.position.set(x, y);
    }

    public void setOriginY(float y) {
        transform.origin.y = y;
    }

    public void setBody(Body body) {
        physics.body = body;
    }

    public void addAnimation(int key, Animation value) {
        animation.looping = true;
        animation.map.put(key, value);
    }

    public Entity createEntity() {
        return entity = engine.createEntity();
    }


    public Entity build(int flags) {
        entity.flags = flags;
//        entity.add(vision);
        entity.add(character);
        entity.add(health);
        entity.add(transform);
        entity.add(size);
        entity.add(sprite);
        entity.add(state);
        entity.add(animation);
        entity.add(collision);
        entity.add(physics);
        engine.addEntity(entity);
        return entity;
    }


}
