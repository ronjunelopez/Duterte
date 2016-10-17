package com.ronscript.duterte.systems.game.objects;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.ronscript.duterte.components.game.objects.character.CriminalComponent;
import com.ronscript.duterte.components.game.objects.character.PoliceComponent;
import com.ronscript.duterte.components.game.objects.character.VisionComponent;
import com.ronscript.duterte.components.game.objects.character.pc.PlayerComponent;
import com.ronscript.duterte.components.game.objects.character.stats.HealthComponent;
import com.ronscript.duterte.utils.EntityManager;
import com.ronscript.duterte.utils.Mapper;

/**
 * Copyright (C) 2016 Duterte on 9/22/2016
 * by Ron
 */
public class CriminalSystem extends IteratingSystem implements EntityListener, VisionSystem.VisionFilter {

    static Family criminalFamily = Family.all(CriminalComponent.class).get();
    Family policeFamily = Family.all(PoliceComponent.class).get();
    Family playerFamily = Family.all(PlayerComponent.class).get();
    ImmutableArray<Entity> policeEntities;
    VisionSystem visionSystem;
    HealthSystem healthSystem;
    DamageSystem damageSystem;


    public CriminalSystem() {
        super(criminalFamily);
    }

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);
        policeEntities = engine.getEntitiesFor(policeFamily);
        visionSystem = engine.getSystem(VisionSystem.class);
        healthSystem = engine.getSystem(HealthSystem.class);
        damageSystem = engine.getSystem(DamageSystem.class);
        engine.addEntityListener(criminalFamily, this);
    }

    @Override
    public void removedFromEngine(Engine engine) {
        super.removedFromEngine(engine);
        policeEntities = null;
        visionSystem = null;
        healthSystem = null;
        damageSystem = null;
        engine.removeEntityListener(this);
    }

    @Override
    public void entityAdded(Entity entity) {
        visionSystem.putFilter(entity, this);
    }

    @Override
    public void entityRemoved(Entity entity) {
        visionSystem.removeFilter(entity);
    }

    @Override
    public boolean shouldSee(Entity observable) {
        return policeFamily.matches(observable);
//        return policeFamily.matches(observable) || playerFamily.matches(observable);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        updateCriminal(entity);
    }
    private void updateCriminal(Entity criminal) {
        HealthComponent health = Mapper.health.get(criminal);

        if(health.current <= 0) {
            Vector2 pos = Mapper.transform.get(criminal).position;
            if(MathUtils.randomBoolean(0.5f)) {
                EntityManager.getInstance().createCoinLarge(pos.x, pos.y);
            } else {
                EntityManager.getInstance().createStar(pos.x, pos.y);
            }
            EntityManager.safeDestroy(criminal);
            return ;
        }


        VisionComponent vision = Mapper.vision.get(criminal);

        if(vision != null) {

            Vector2 linearVelocity = Mapper.physics.get(criminal).body.getLinearVelocity();
            if (linearVelocity.isZero()) {
                vision.facingAngle = 270;
            } else {
                vision.facingAngle = linearVelocity.nor().angle();
            }

        }

//        SteeringComponent<DruggieSteeringAgent> agent = Mapper.agent.get(criminal);
//        DruggieSteeringAgent agent = agent.agent;

        Entity police = visionSystem.getFirstVisionFromFamily(criminal, policeFamily);

        if(police != null) {
            Gdx.app.log("", "TAKBO");
        }

    }
}
