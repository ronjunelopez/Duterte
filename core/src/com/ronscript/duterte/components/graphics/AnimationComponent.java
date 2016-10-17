package com.ronscript.duterte.components.graphics;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.Pool;

/**
 * @author Ron
 * @since 8/8/2016
 */
public class AnimationComponent implements Component, Pool.Poolable {
    public final IntMap<Animation> map = new IntMap<Animation>();
    public boolean looping = false;

    @Override
    public void reset() {
        looping = false;
        map.clear();
    }
}
