package com.ronscript.duterte.utils;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.Vector2;

import java.util.Comparator;

/**
 * Copyright (C) 2016 Duterte on 9/20/2016
 * by Ron
 */
public class NearbyDistanceSquaredComparator implements Comparator<Entity> {

    public Entity owner;
    public ImmutableArray<Entity> entities;

    public NearbyDistanceSquaredComparator() {
    }

    @Override
    public int compare(Entity e1, Entity e2) {
        if (e1 == e2) {
            return 0;
        }
        if (!entities.contains(e1, true)) {
            return -1;
        }
        if (!entities.contains(e2, true)) {
            return 1;
        }

        Vector2 ownerPos = getPosition(owner);
        Vector2 nearbyPos1 = getPosition(e1);
        Vector2 nearbyPos2 = getPosition(e2);

        return (int) Math.signum(nearbyPos1.dst2(ownerPos)- nearbyPos2.dst2(ownerPos)); // lower
    }

    private Vector2 getPosition(Entity entity) {
        return Mapper.transform.get(entity).position;
    }

}