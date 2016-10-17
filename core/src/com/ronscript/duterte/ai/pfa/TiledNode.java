package com.ronscript.duterte.ai.pfa;

import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.utils.Array;

/**
 * Copyright (C) 2016 Duterte on 8/29/2016
 * by Ron
 */
public class TiledNode  {

    public final int x;
    public final int y;
    private final int index;
    private final Array<Connection<TiledNode>> connections;
    public boolean walkable;

    public TiledNode(NavigationMap graph, int x, int y) {
        this.x = x;
        this.y = y;
        this.index = x * graph.getHeight() + y;
        this.walkable = true;
        this.connections = new Array<Connection<TiledNode>>();
    }

    public int getIndex () {
        return index;
    }

    public Array<Connection<TiledNode>> getConnections () {
        return connections;
    }

    @Override
    public String toString() {
        return "TiledNode: (" + x + ", " + y + ")";
    }

}
