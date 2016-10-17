package com.ronscript.duterte.ai.pfa;

import com.badlogic.gdx.ai.pfa.DefaultGraphPath;
import com.badlogic.gdx.ai.pfa.SmoothableGraphPath;
import com.badlogic.gdx.math.Vector2;

/**
 * Copyright (C) 2016 Duterte on 8/30/2016
 * by Ron
 */
public class TiledSmoothableGraphPath <N extends TiledNode> extends DefaultGraphPath<N> implements
        SmoothableGraphPath<N, Vector2> {

    private final Vector2 tmpPosition = new Vector2();

    @Override
    public Vector2 getNodePosition(int index) {
        N node = nodes.get(index);
        return tmpPosition.set(node.x, node.y);
    }

    @Override
    public void swapNodes(int index1, int index2) {
        nodes.set(index1, nodes.get(index2));
    }

    @Override
    public void truncatePath(int newLength) {
        nodes.truncate(newLength);
    }
}
