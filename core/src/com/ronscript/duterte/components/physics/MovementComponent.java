package com.ronscript.duterte.components.physics;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;

/**
 * @author Ron
 * @since 8/8/2016
 */
public class MovementComponent implements Component {

    public final Vector2 acceleration = new Vector2();
    public final Vector2 velocity = new Vector2();

}
