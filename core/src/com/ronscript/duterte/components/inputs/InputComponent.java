package com.ronscript.duterte.components.inputs;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector3;

/**
 * @author Ron
 * @since 8/9/2016
 */
public class InputComponent implements Component {

    public final Vector3 screenPoint = new Vector3();
    public boolean isTouched = false;

}
