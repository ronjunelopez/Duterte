package com.ronscript.duterte.components.properties;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool;

/**
 * Copyright (C) 2016 Duterte on 8/12/2016
 * by Ron
 */
public class SizeComponent implements Component, Pool.Poolable {
    public float width = 0.0f;
    public float height = 0.0f;

    public boolean dirty = false; // to override texture size

    @Override
    public void reset() {
        width = 0.0f;
        height = 0.0f;
    }
}
