package com.ronscript.duterte.systems.game.objects;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.ronscript.duterte.components.game.objects.BulletComponent;
import com.ronscript.duterte.components.game.objects.ExpirationComponent;
import com.ronscript.duterte.components.physics.GravityComponent;
import com.ronscript.duterte.components.physics.MovementComponent;
import com.ronscript.duterte.components.properties.TransformComponent;
import com.ronscript.duterte.utils.CameraManager;
import com.ronscript.duterte.utils.EntityManager;
import com.ronscript.duterte.utils.Mapper;

/**
 * @author Ron
 * @since 8/8/2016
 */
public class BulletSystem extends IteratingSystem {

    public BulletSystem() {
        super(Family.all(BulletComponent.class, TransformComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        TransformComponent transform = Mapper.transform.get(entity);
//        BulletComponent bullet = Mapper.bullet.get(entity);
        ExpirationComponent timer = Mapper.timer.get(entity);

//        transform.position

//        MovementComponent shadowMovement = Mapper.movement.get(bullet.shadow);
//        TransformComponent shadowTransform = Mapper.transform.get(bullet.shadow);
//
//        shadowTransform.position.x = transform.position.x;
//        shadowTransform.position.y = transform.position.y;

        if(timer.isFinished()) {
            timer.start(0.5f);
            timer.removeOnFinished = true;
            entity.remove(GravityComponent.class);
            entity.remove(MovementComponent.class);
//            entity.remove(GravityComponent.class);
//            EntityManager.safeDestroy(entity);
            return;
        }

        removeOutsideCamera(entity);
    }

    private void removeOutsideCamera(Entity entity) {
        BulletComponent bullet = Mapper.bullet.get(entity);
        TransformComponent transform = Mapper.transform.get(entity);
        if(!bullet.alive && CameraManager.getInstance().isOutsideCamera(transform.position.x, transform.position.y, 1)) {
            EntityManager.safeDestroy(entity);
        }
    }

}
