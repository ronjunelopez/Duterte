package com.ronscript.duterte.ai.pfa;

import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.ai.pfa.Heuristic;
import com.badlogic.gdx.ai.pfa.PathFinder;
import com.badlogic.gdx.ai.pfa.PathSmoother;
import com.badlogic.gdx.ai.pfa.indexed.IndexedAStarPathFinder;
import com.badlogic.gdx.ai.pfa.indexed.IndexedGraph;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.ronscript.duterte.ai.pfa.heuristics.ManhattanDistanceHeuristic;

import java.util.Iterator;

/**
 * Copyright (C) 2016 Duterte on 8/29/2016
 *
 *
 * by Ron
 */
public class AStarPathFinding {
    private static final int[][] NEIGHBORHOOD = new int[][] {
            new int[] {-1, 0}, // left
            new int[] { 0, -1}, // bottom
            new int[] { 0,  1}, // top
            new int[] { 1,  0}, // right

            new int[] { 1,  1},   // diagonal top-right
            new int[] {-1,  1},   // diagonal top-left
            new int[] {1,  -1},   // diagonal bottom-right
            new int[] {-1,  -1}   // diagonal bottom-left
    };
    private final NavigationMap map;
    private final PathFinder<TiledNode> pathfinder;
    private final Heuristic<TiledNode> heuristic;
    private final TiledSmoothableGraphPath<TiledNode> nodePath;
    private final PathSmoother<TiledNode, Vector2> pathSmoother;
    private Array<Vector2> wayPoints = new Array<Vector2>();
    private FinderOptions options;

    public AStarPathFinding(NavigationMap map) {
        options = new FinderOptions();
        this.map = map;

        this.pathfinder = new IndexedAStarPathFinder<TiledNode>(createGraph(map, options));
        this.heuristic = new ManhattanDistanceHeuristic<TiledNode>();
//        this.heuristic = new ChebyshevDistanceHeuristic<TiledNode>();

        nodePath = new TiledSmoothableGraphPath<TiledNode>();
        pathSmoother = new PathSmoother<TiledNode, Vector2>(new TiledRaycastCollisionDetector(this.map));
    }

    public static TiledIndexedGraph createGraph (NavigationMap map, FinderOptions options) {
        final int DIRECTIONAL_COUNT = options.allowDiagonal ? NEIGHBORHOOD.length : 4;
        final int height = map.getHeight();
        final int width = map.getWidth();

        TiledIndexedGraph graph = new TiledIndexedGraph(map);

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                TiledNode node = map.getNodeAt(x, y);
                if (!node.walkable) {
                    continue;
                }
                // Add a connection for each valid neighbor
                for (int offset = 0; offset < DIRECTIONAL_COUNT; offset++) {
                    int neighborX = node.x + NEIGHBORHOOD[offset][0];
                    int neighborY = node.y + NEIGHBORHOOD[offset][1];
                    if (neighborX >= 0 && neighborX < width && neighborY >= 0 && neighborY < height) {
                        TiledNode neighbor = map.getNodeAt(neighborX, neighborY);
                        if (neighbor.walkable) {
                            // Add connection to walkable neighbor
                            node.getConnections().add(new TiledConnection(node, neighbor));
                        }
                    }
                }
                node.getConnections().shuffle();
            }
        }
        return graph;
    }

    public boolean createPath(Vector2 start, Vector2 end) {
        return createPath(start, end, wayPoints);
    }

    public Array<Vector2> getWayPoints() {
        return wayPoints;
    }

    public boolean createPath(Vector2 start, Vector2 end, Array<Vector2> outWayPoints) {
        int sourceX = MathUtils.floor(start.x);
        int sourceY = MathUtils.floor(start.y);
        int targetX = MathUtils.floor(end.x);
        int targetY = MathUtils.floor(end.y);

        if (map == null
                || sourceX < 0 || sourceX >= map.getWidth()
                || sourceY < 0 || sourceY >= map.getHeight()
                || targetX < 0 || targetX >= map.getWidth()
                || targetY < 0 || targetY >= map.getHeight()) {
            return false;
        }

        TiledNode startNode = map.getNodeAt(sourceX, sourceY);
        TiledNode endNode = map.getNodeAt(targetX, targetY);
        nodePath.clear();
        boolean found = pathfinder.searchNodePath(startNode, endNode, heuristic, nodePath);
        if(options.allowSmoothPath) {
            pathSmoother.smoothPath(nodePath);
        }
        if(found) {
            Iterator<TiledNode> tiles = nodePath.iterator();
            while (tiles.hasNext()) {
                TiledNode tile = tiles.next();
                outWayPoints.add(nodeToWorldPoint(tile));
            }
        }
        return found;
    }

    public Vector2 nodeToWorldPoint(TiledNode node) {
        return new Vector2(node.x + 1 * 0.5f, node.y + 1 * 0.5f);
    }

    public Vector2 worldPointToNode(Vector2 worldPoint) {
        return new Vector2(worldPoint.x - 1 * 0.5f, worldPoint.y - 1 * 0.5f);
    }

    private static class TiledIndexedGraph implements IndexedGraph<TiledNode> {

        NavigationMap map;

        public TiledIndexedGraph(NavigationMap map) {
            this.map = map;
        }

        @Override
        public int getIndex(TiledNode node) {
            return node.getIndex();
        }

        @Override
        public Array<Connection<TiledNode>> getConnections(TiledNode fromNode) {
            return fromNode.getConnections();
        }

        @Override
        public int getNodeCount() {
            return map.getWidth() * map.getHeight();
        }

    }
}
