package com.ronscript.duterte.components.game.objects;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Pool;

/**
 * @author Ron
 * @since 8/8/2016
 */
public class BulletComponent implements Component, Pool.Poolable {

    public boolean alive = true;
    public boolean fired = false;
    public Entity shadow;

    @Override
    public void reset() {
        alive = true;
        fired = false;
    }

}
