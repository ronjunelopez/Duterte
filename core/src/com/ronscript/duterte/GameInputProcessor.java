package com.ronscript.duterte;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.math.Vector3;

/**
 * Copyright (C) 2016 Duterte on 7/3/2016
 * by Ron
 */

public class GameInputProcessor extends InputAdapter {
    private final Vector3 screenPoint = new Vector3();
    public boolean touchDragged = false;
    private boolean touched = false;

    public GameInputProcessor() {
        Gdx.input.setCatchMenuKey(false);
        Gdx.input.setCatchBackKey(false);
        Gdx.input.setOnscreenKeyboardVisible(true);
//        Gdx.input.get
    }


    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (button != Input.Buttons.LEFT || pointer > 0) return false;
        screenPoint.set(screenX, screenY, 0);
        touched = true;
        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if (button != Input.Buttons.LEFT || pointer > 0) return false;
        screenPoint.set(screenX, screenY, 0);
        touched = false;
        return true;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        if (!touched) {
            touchDragged = false;
            return false;
        }
        touchDragged = true;
        screenPoint.set(screenX, screenY, 0);
        return true;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        screenPoint.set(screenX, screenY, 0);
        return true;
    }

    public Vector3 getScreenPoint() {
        return screenPoint;
    }

    public boolean isTouched(){
        return touched;
    }
}
