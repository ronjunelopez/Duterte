package com.ronscript.duterte.utils;

/**
 * @author Ron
 * @since 8/8/2016
 */
public class Constants {

    public final static float WIDTH = 25;
    public final static float HEIGHT = 15;

    public final static float PIXELS_TO_METERS = 1 / 32.0f;
    public final static float TIME_STEP = 1/60.0f;
    public final static int VELOCITY_ITERATIONS = 6;
    public final static int POSITION_ITERATIONS = 2;

    public final static short CATEGORY_CHARACTER = 0x001;
    public final static short CATEGORY_ITEM = 0x002;
    public final static short CATEGORY_WALL = 0x004;
    public final static short CATEGORY_SENSOR = 0x0016;

    public final static short MASK_CHARACTER = ~CATEGORY_CHARACTER; // cannot collide to playerCharacter
//    public final static short MASK_CHARACTER_ENEMY = ~CATEGORY_CHARACTER_ENEMY; // cannot collide to playerCharacter
    public final static short MASK_ITEM = ~CATEGORY_ITEM; // cannot collide to playerCharacter
    public final static short MASK_WALL = -1; // can collide all
    public final static short MASK_SENSOR = CATEGORY_CHARACTER; // can collide only to a playerCharacter

}
