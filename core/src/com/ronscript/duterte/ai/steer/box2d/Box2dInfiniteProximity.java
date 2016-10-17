package com.ronscript.duterte.ai.steer.box2d;

import com.badlogic.gdx.ai.steer.Proximity;
import com.badlogic.gdx.ai.steer.Steerable;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

/**
 * Copyright (C) 2016 Duterte on 9/2/2016
 * by Ron
 */
public class Box2dInfiniteProximity implements Proximity<Vector2> {

    private Steerable<Vector2> owner;
    private World world;

    public Box2dInfiniteProximity(Steerable<Vector2> owner, World world) {
        this.owner = owner;
        this.world = world;
    }

    @Override
    public Steerable<Vector2> getOwner() {
        return owner;
    }

    @Override
    public void setOwner(Steerable<Vector2> owner) {
        this.owner = owner;
    }

    @Override
    public int findNeighbors(ProximityCallback<Vector2> callback) {

        return 0;
    }
}
