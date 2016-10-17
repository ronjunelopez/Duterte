package com.ronscript.duterte.components.properties;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;

/**
 * Copyright (C) 2016 Duterte on 8/13/2016
 * Alignment: 1 = top, 2 = bottom, 3 = left, 4 = right
 * by Ron
 */
public class AlignmentComponent implements Component {

    public static final int CENTER = 0;
    public static final int TOP = 1;
    public static final int BOTTOM = 2;

    public int align = CENTER;

    public Vector2 position = new Vector2();

}
