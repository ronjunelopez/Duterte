package com.ronscript.duterte.components.game.objects;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool;

/**
 * Copyright (C) 2016 Duterte on 8/14/2016
 * by Ron
 */
public class ExpirationComponent implements Component, Pool.Poolable {

    public float time = 0.0f;
    public boolean removeOnFinished = false;
    public float duration = 0.0f;
    public boolean finished = false;
    public boolean start = false;

    public void start(float duration) {
        if(!start) {
            this.start = true;
            this.duration = duration;
        }
    }

    public float getDuration() {
        return duration;
    }

    public void stop() {
        start = false;
        finished = true;
    }

    public boolean isFinished() {
        return finished;
    }

    public boolean hasStarted() {
        return start;
    }

    @Override
    public void reset() {
        time = 0.0f;
        removeOnFinished = false;
        duration = 0.0f;
        finished = false;
        start = false;
    }
}
