package com.ronscript.duterte.ai.steer;

import com.badlogic.gdx.ai.steer.Steerable;
import com.badlogic.gdx.ai.steer.utils.rays.RayConfigurationBase;
import com.badlogic.gdx.ai.utils.Ray;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

/**
 * Copyright (C) 2016 Duterte on 10/5/2016
 *
 * implementation of http://fightingkitten.webcindario.com/?p=610
 * by Ron
 */
public class RayStar extends RayConfigurationBase<Vector2> {

    Vector2 tmp = new Vector2();

    public RayStar(Steerable<Vector2> owner) {
        super(owner, 6);
    }

    @Override
    public Ray<Vector2>[] updateRays() {
        final Vector2 vector =  tmp.set(owner.getLinearVelocity()).nor();

        SetOrigin();

        North(vector);
        South(vector);

        owner.angleToVector(vector, owner.vectorToAngle(owner.getLinearVelocity()) - Angle());

        Northeast(vector);
        Southwest(vector);

        owner.angleToVector(vector, owner.vectorToAngle(owner.getLinearVelocity()) + Angle());

        Southeast(vector);
        Northwest(vector);

        return rays;
    }

    private float Angle()
    {
        return 60 * MathUtils.degreesToRadians;
    }

    private float Length()
    {
        return owner.getBoundingRadius() + owner.getBoundingRadius();
    }

    private void SetOrigin() { rays[0].start.set(owner.getPosition()); }

    private void North(Vector2 vector){ setRay(Stars.Direction.NORTH(), vector, Length()); }

    private void Northeast(Vector2 vector)
    {
        setRay(Stars.Direction.NORTHEAST(), vector, Length());
    }

    private void Southeast(Vector2 vector)
    {
        setRay(Stars.Direction.SOUTHEAST(), vector, -Length());
    }

    private void South(Vector2 vector)
    {
        setRay(Stars.Direction.SOUTH(), vector, -Length());
    }

    private void Southwest(Vector2 vector)
    {
        setRay(Stars.Direction.SOUTHWEST(), vector, -Length());
    }

    private void Northwest(Vector2 vector)
    {
        setRay(Stars.Direction.NORTHWEST(), vector, Length());
    }

    private void setRay(int index, Vector2 vector, float length)
    {
        rays[index].end.set(vector).scl(length).add(owner.getPosition());
    }

    private enum Stars
    {
        Direction()
                {
                    @Override public int NORTH(){return 0;}
                    @Override public int NORTHEAST(){return 1;}
                    @Override public int SOUTHEAST(){return 2;}
                    @Override public int SOUTH(){return 3;}
                    @Override public int SOUTHWEST(){return 4;}
                    @Override public int NORTHWEST(){return 5;}
                };

        Stars(){}

        public abstract int NORTH();

        public abstract int NORTHEAST();

        public abstract int SOUTHEAST();

        public abstract int SOUTH();

        public abstract int SOUTHWEST();

        public abstract int NORTHWEST();
    }
}
