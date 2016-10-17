package com.ronscript.duterte.components.properties;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool;

/**
 * @author Ron
 * @since 8/8/2016
 */
public class TransformComponent implements Component, Pool.Poolable{

    public final Vector2 position = new Vector2();
    public final Vector2 scale = new Vector2(1, 1);
    public final Vector2 origin = new Vector2(0.5f, 0.5f); // x & y ratio of width or height
    public float rotation = 0.0f;
    public boolean dirty = false;

    @Override
    public void reset() {
        position.setZero();
        rotation = 0.0f;
        scale.set(1,1);
        origin.set(0.5f, 0.5f);
    }
}
