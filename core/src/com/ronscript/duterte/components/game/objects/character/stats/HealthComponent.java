package com.ronscript.duterte.components.game.objects.character.stats;

import com.badlogic.ashley.core.Component;

/**
 * Copyright (C) 2016 Duterte on 8/10/2016
 * by Ron
 */
public class HealthComponent implements Component {
    public final int MAX = 100;
    public float current = MAX;
    public boolean dead = false;
}
