package com.ronscript.duterte.utils;

import com.badlogic.gdx.maps.objects.PolylineMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;

/**
 * Copyright (C) 2016 Duterte on 8/31/2016
 * by Ron
 */
public class BodyBuilder {

    private static FixtureDef fixtureDef = new FixtureDef();


    public  static Body createBoxBody(World world,  float x, float y, float width, float height, short categoryBits, short maskBits, Object userdata) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(x, y);
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.awake = false;
        Body body = world.createBody(bodyDef);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.filter.categoryBits = categoryBits;
        fixtureDef.filter.maskBits = maskBits;

        PolygonShape polygon = new PolygonShape();
        polygon.setAsBox(width, height);
        fixtureDef.shape = polygon;

        body.createFixture(fixtureDef).setUserData(userdata);
        polygon.dispose();
        return body;
    }

    public static Shape createMapRectangle(RectangleMapObject rectangleObject, float unitScale) {
        Rectangle rectangle = rectangleObject.getRectangle();
        PolygonShape polygon = new PolygonShape();
        Vector2 size = new Vector2((rectangle.x + rectangle.width * 0.5f) * unitScale,
                (rectangle.y + rectangle.height * 0.5f ) * unitScale);
        polygon.setAsBox(rectangle.width * 0.5f * unitScale,
                rectangle.height * 0.5f * unitScale,
                size,
                0.0f);
        return polygon;
    }

    public static ChainShape createMapPolyline(PolylineMapObject polylineMapObject, float unitScale) {
        float[] vertices = polylineMapObject.getPolyline().getTransformedVertices();
        Vector2[] worldVertices = new Vector2[vertices.length / 2];

        for (int i = 0; i < vertices.length / 2; ++i) {
            worldVertices[i] = new Vector2();
            worldVertices[i].x = vertices[i * 2] * unitScale;
            worldVertices[i].y = vertices[i * 2 + 1] * unitScale;
        }

        ChainShape chain = new ChainShape();
        chain.createChain(worldVertices);
        return chain;
    }

    public static Body createStaticBody(World world, float x, float y) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(x, y);
        bodyDef.type = BodyDef.BodyType.StaticBody;
//        bodyDef.awake = false;
        return world.createBody(bodyDef);
    }


    public static Body createDynamicBody(World world, float x, float y) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(x, y);
        bodyDef.type = BodyDef.BodyType.DynamicBody;
//        bodyDef.awake = false;
        return world.createBody(bodyDef);
    }

    public static void createWallFixture(Body body, Shape shape, Object userdata) {
         createFixture(body, shape,
                Constants.CATEGORY_WALL, Constants.MASK_WALL,
                1, 1, 0,
                false, userdata);
    }


    public static void createCircleSensor(Body body, float radius, Object userdata) {
        CircleShape circle = new CircleShape();
        circle.setRadius(radius);
        createFixture(body, circle, Constants.CATEGORY_SENSOR, Constants.MASK_SENSOR, 0, 0, 0, true, userdata);
    }

    public static void createCharacterVisionBody(Body body, float radius, Object userdata) {
        PolygonShape circle = new PolygonShape();
        circle.setAsBox(1,1);
//        circle.setPosition(circle.getPosition().cpy().add(,0.5f));
        createFixture(body, circle, Constants.CATEGORY_CHARACTER, Constants.MASK_CHARACTER, 0, 0, 0, true, userdata);
    }

    public static void createCharacterFoot(Body body, Object userdata) {
//        body.setFixedRotation(true);
        CircleShape circle = new CircleShape();
//        Vector2  position = circle.getPosition();
//        circle.setPosition(new Vector2(0, position.y - 0.5f));
        circle.setRadius(0.1f);
        createFixture(body, circle, Constants.CATEGORY_CHARACTER, Constants.MASK_CHARACTER, 1,1,0, false, userdata);
    }

    public static void createCircleCharacter(Body body, float radius, Object userdata) {
        CircleShape circle = new CircleShape();
        circle.setRadius(radius);
        createFixture(body, circle, Constants.CATEGORY_CHARACTER, Constants.MASK_CHARACTER, 0, 0, 0, false, userdata);
    }

    public static void createFixture(Body body, Shape shape, short categoryBits, short maskBits, float density, float friction, float restitution,  boolean isSensor, Object userdata) {
        fixtureDef.filter.categoryBits = categoryBits;
        fixtureDef.filter.maskBits = maskBits;
        fixtureDef.density = density;
        fixtureDef.friction = friction;
        fixtureDef.restitution = restitution;
        fixtureDef.isSensor = isSensor;
        fixtureDef.shape = shape;
        body.createFixture(fixtureDef).setUserData(userdata);
        shape.dispose();
    }



//    public static Body createCircleBody(World world, float x, float y, float radius, boolean isSensor, short categoryBits, short maskBits, Object userdata) {
//        Body body = createDynamicBody(world, x, y);
//        CircleShape circle = new CircleShape();
//        circle.setRadius(radius);
//        createFixture(body, circle, categoryBits, maskBits, 1, 1, 0, isSensor, userdata);
//        return body;
//    }
}
