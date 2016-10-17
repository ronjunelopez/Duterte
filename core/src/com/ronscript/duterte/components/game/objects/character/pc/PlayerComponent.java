package com.ronscript.duterte.components.game.objects.character.pc;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;

/**
 * @author Ron
 * @since 8/9/2016
 */
public class PlayerComponent implements Component {

    public static final float MAX_LINEAR_SPEED = 1;
    public static final float MAX_LINEAR_ACCELERATION = 0.1f;




    public final Vector2 target = new Vector2();
}
