package com.ronscript.duterte.components.game.objects.character;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;

/**
 * Copyright (C) 2016 Duterte on 9/5/2016
 * by Ron
 */
public class PoliceComponent implements Component {

    public static final int GUN_FIRE_LEFT = 6;
    public static final int GUN_FIRE_RIGHT = 7;

    public float minChaseDistanceThreshold = 3;
    public Entity target;
    public Entity leader;
}
