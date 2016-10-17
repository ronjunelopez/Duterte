package com.ronscript.duterte.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;

/**
 * @author Ron
 * @since 8/8/2016
 */
public class BoundsComponent implements Component {
    public final Vector2 position = new Vector2();
    public float width = 0.0f;
    public float height = 0.0f;

}
