package com.ronscript.duterte.components.game.objects.character;

import com.badlogic.ashley.core.Component;

/**
 * @author Ron
 * @since 8/9/2016
 */
public class CharacterComponent implements Component {
    public static final int IDLE = 0;
    public static final int MOVE_UP = 1;
    public static final int MOVE_DOWN = 2;
    public static final int MOVE_LEFT = 3;
    public static final int MOVE_RIGHT = 4;
    public static final int DIE = 5;

    public int currentState = IDLE;
}
