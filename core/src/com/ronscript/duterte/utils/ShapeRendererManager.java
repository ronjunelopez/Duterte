package com.ronscript.duterte.utils;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/**
 * Copyright (C) 2016 Duterte on 9/24/2016
 * by Ron
 */
public class ShapeRendererManager {

    private static ShapeRendererManager ourInstance = new ShapeRendererManager();
    private ShapeRenderer renderer;

    private ShapeRendererManager() {
    }

    public static ShapeRendererManager getInstance() {
        return ourInstance;
    }

    public static void init(ShapeRenderer shapeRenderer) {
        getInstance().renderer = shapeRenderer;
    }

    public static  ShapeRenderer getRenderer() {
        return  getInstance().renderer;
    }
}
