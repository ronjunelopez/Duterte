package com.ronscript.duterte.components.game.objects;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Array;

/**
 * Copyright (C) 2016 Duterte on 8/11/2016
 * by Ron
 */
public class InventoryComponent implements Component {
    public final Array<Entity> items = new Array<Entity>();
}
