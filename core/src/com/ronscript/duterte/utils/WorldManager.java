package com.ronscript.duterte.utils;

import com.badlogic.gdx.physics.box2d.World;

/**
 * @author Ron
 * @since 8/10/2016
 */
public class WorldManager {
    private static WorldManager ourInstance = new WorldManager();
    private World world;

    private WorldManager() {
    }

    public static WorldManager getInstance() {
        return ourInstance;
    }

    public static World getWorld() {
        return getInstance().world;
    }

    public void init (World world) {
        this.world = world;
    }

}
