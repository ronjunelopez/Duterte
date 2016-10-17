package com.ronscript.duterte.ai.pfa.heuristics;

import com.badlogic.gdx.ai.pfa.Heuristic;
import com.ronscript.duterte.ai.pfa.TiledNode;

/**
 *
 *
 *
 * Diagonal
 */
public class ChebyshevDistanceHeuristic<N extends TiledNode> implements Heuristic<N> {

    @Override
    public float estimate (N node, N endNode) {
        float dX = Math.abs(endNode.x - node.x);
        float dY = Math.abs(endNode.y - node.y);
        return (dX + dY) + Math.min(dX, dY);
    }
}