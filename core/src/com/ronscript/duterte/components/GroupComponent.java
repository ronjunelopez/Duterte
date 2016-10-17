package com.ronscript.duterte.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Array;

/**
 * Copyright (C) 2016 Duterte on 8/12/2016
 * by Ron
 */
public class GroupComponent implements Component {
    public final Array<Entity> members = new Array<Entity>();
}
