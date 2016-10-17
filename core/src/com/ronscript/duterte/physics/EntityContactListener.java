package com.ronscript.duterte.physics;

import com.badlogic.ashley.core.Entity;

/**
 * Copyright (C) 2016 Duterte on 9/7/2016
 * by Ron
 */
public interface EntityContactListener {
    void beginContact(Entity owner, Iterable<Entity> contacts);
    void endContact(Entity owner, Iterable<Entity> contacts);
}
