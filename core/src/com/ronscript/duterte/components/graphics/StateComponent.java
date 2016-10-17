package com.ronscript.duterte.components.graphics;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool;

/**
 * @author Ron
 * @since 8/8/2016
 */
public class StateComponent implements Component, Pool.Poolable {
    public float time = 0.0f;
    private int currentState = 0;

    public int get() {
        return  currentState;
    }

    public void set(int newState) {
        currentState = newState;
//        time = 0.0f;
    }

    @Override
    public void reset() {
        currentState = 0;
    }
}
