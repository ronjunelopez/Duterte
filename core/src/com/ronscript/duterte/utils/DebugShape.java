package com.ronscript.duterte.utils;

import com.badlogic.gdx.ai.steer.behaviors.Hide;
import com.badlogic.gdx.ai.utils.Ray;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

/**
 * Copyright (C) 2016 Duterte on 9/24/2016
 * by Ron
 */
public class DebugShape {


    public static void debugDrawPath(ShapeRenderer shape, Array<Vector2> wayPoints, boolean closedPath) {
        if(wayPoints.size == 0) {
            return;
        }
        Vector2 lastPoint  = new Vector2();
        Vector2 first = new Vector2();
        first.set(wayPoints.first());
        for (int i = 0; i < wayPoints.size; i ++) {
            Vector2 wayPoint = wayPoints.get(i);
            if(i > 0) {
                shape.setColor(Color.WHITE);
                shape.line(lastPoint.x, lastPoint.y, 0, wayPoint.x, wayPoint.y, 0);
            }
            if(i == 0) {
                shape.setColor(Color.BLUE);
            } else if(i == wayPoints.size - 1) {
                shape.setColor(Color.RED);
            } else {
                shape.setColor(Color.GREEN);
            }
            shape.box(wayPoint.x - 0.1f * 0.5f, wayPoint.y - 0.1f * 0.5f, 0, 0.1f, 0.1f, 0);
            lastPoint.set(wayPoint);

        }
        if(closedPath) {
            shape.setColor(Color.BROWN);
            shape.line(lastPoint, first);
        }
    }

    public static void drawFov(ShapeRenderer shape, Vector2 position, float orientation, float fov, float fovRadius) {
        shape.arc(position.x,position.y, fovRadius,
                orientation - fov / 2f , fov ,
                (int)(6 * fovRadius));
    }
    public static void drawRays(ShapeRenderer shape, Ray<Vector2>[] rays) {
        for( Ray<Vector2> ray: rays) {
            shape.line(ray.start, ray.end);
        }
    }
    public static void debugDrawHide(ShapeRenderer shape, Hide<Vector2> hide, float threatRadius, boolean hiding) {
        // hide behavior debug drawing
        if(hiding) {
            shape.setColor(Color.RED);
        } else {
            shape.setColor(Color.GREEN);
        }
        shape.circle(hide.getOwner().getPosition().x, hide.getOwner().getPosition().y , threatRadius, 12);
        if( hide.getTarget() !=  null) {
            Vector2 hidePosition = hide.getTarget().getPosition();
            shape.box(hidePosition.x, hidePosition.y, 0, 0.1f, 0.1f, 0);
        }
    }

}
