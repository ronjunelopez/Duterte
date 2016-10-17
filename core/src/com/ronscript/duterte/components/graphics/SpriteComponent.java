package com.ronscript.duterte.components.graphics;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Pool;

/**
 * @author Ron
 * @since 8/8/2016
 */
public class SpriteComponent implements Component, Pool.Poolable {
    public TextureRegion textureRegion;
    public float widthRatio = 1.0f;
    public int zIndex = 0;

    @Override
    public void reset() {
        textureRegion = null;
        widthRatio = 1.0f;
        zIndex = 0;
    }
}
