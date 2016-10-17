package com.ronscript.duterte.ai.pfa.heuristics;

import com.badlogic.gdx.ai.pfa.Heuristic;
import com.ronscript.duterte.ai.pfa.TiledNode;

/**
 * 4-way movement
 *
 *
 *
 */
public class ManhattanDistanceHeuristic<N extends TiledNode> implements Heuristic<N> {

    public ManhattanDistanceHeuristic() {
        super();
    }

    @Override
    public float estimate (N node, N endNode) {
        return Math.abs(endNode.x - node.x) + Math.abs(endNode.y - node.y);
    }
}
