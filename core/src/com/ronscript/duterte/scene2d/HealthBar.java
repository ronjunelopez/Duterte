package com.ronscript.duterte.scene2d;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;

/**
 * Copyright (C) 2016 Duterte on 8/12/2016
 * by Ron
 */
public class HealthBar extends Actor {


    private TextureRegion healthBackground;
    private TextureRegion region;

    private float ratio = 0;

    public HealthBar(TextureRegion background, TextureRegion bar) {
        this.healthBackground = background;
        this.region = bar;
    }

    public boolean setRatio(float ratio) {
        if(!(ratio > 1.0f) && !(ratio < 0.0f)) {
            this.ratio = ratio;
            return true;
        }
        return false;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.draw(healthBackground, getX(), getY(), getWidth() * getScaleX(), getHeight() * getScaleY());
        batch.draw(region.getTexture(),getX(),getY(),ratio * getWidth() * getScaleX() ,getHeight() * getScaleY(), region.getU(), region.getV(), region.getU() + ratio * (region.getU2() - region.getU()), region.getV2());
    }
}
