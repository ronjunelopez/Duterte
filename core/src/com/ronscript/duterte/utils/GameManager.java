package com.ronscript.duterte.utils;

/**
 * Copyright (C) 2016 Duterte on 9/8/2016
 * by Ron
 */
public class GameManager {
    private static GameManager ourInstance = new GameManager();

    private GameManager() {
    }

    public static GameManager getInstance() {
        return ourInstance;
    }


}
