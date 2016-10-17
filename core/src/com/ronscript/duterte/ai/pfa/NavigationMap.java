package com.ronscript.duterte.ai.pfa;

/**
 * Copyright (C) 2016 Duterte on 8/29/2016
 * by Ron
 */
public class NavigationMap {

    private final int width;
    private final int height;
    private TiledNode[][] map;

    public NavigationMap(int width, int height) {
        this.width = width;
        this.height = height;

        map = new TiledNode[width][height];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                map[x][y] = new TiledNode(this, x, y);
            }
        }
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public TiledNode getNodeAt(int x, int y) {
        return map[x][y];
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("\n Height:" + height + ", Width:" + width + "\n");

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                stringBuilder.append(map[x][y].walkable ? "0": "1");
            }
            stringBuilder.append("\n");
        }
        return stringBuilder.toString();
    }

}
