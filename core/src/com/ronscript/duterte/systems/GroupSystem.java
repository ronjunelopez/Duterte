package com.ronscript.duterte.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector2;
import com.ronscript.duterte.components.properties.AlignmentComponent;
import com.ronscript.duterte.components.properties.AttachmentComponent;
import com.ronscript.duterte.components.properties.SizeComponent;
import com.ronscript.duterte.components.properties.TransformComponent;
import com.ronscript.duterte.utils.Constants;
import com.ronscript.duterte.utils.Mapper;

/**
 * Copyright (C) 2016 Duterte on 8/12/2016
 *  Alignment: 1 = top, 2 = bottom, 3 = left, 4 = right
 * by Ron
 */
public class GroupSystem extends IteratingSystem implements EntityListener {

    private Vector2 tmpPos = new Vector2();

    public GroupSystem() {
        super(Family.all(AttachmentComponent.class, TransformComponent.class, AlignmentComponent.class).get());
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
        transformBy(entity);
    }

    @Override
    public void entityRemoved(Entity entity) {

    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        transformBy(entity);
    }

    public void transformBy(Entity entity) {
        AttachmentComponent attachment = Mapper.attachment.get(entity);
        TransformComponent transform = Mapper.transform.get(entity);
        AlignmentComponent alignment = Mapper.alignment.get(entity);

        if(attachment.owner == null) {
            return;
        }

        Vector2 newPos = new Vector2();
        newPos.set(Mapper.transform.get(attachment.owner).position);

            SizeComponent parentSize = Mapper.size.get(attachment.owner);

        if(alignment.position.isZero()) {
            if(alignment.align == AlignmentComponent.TOP) {
                newPos.add(0, parentSize.height * 0.5f * Constants.PIXELS_TO_METERS);
            }
        } else {
            newPos.add(alignment.position);
        }

        transform.position.set(newPos);
    }


}
