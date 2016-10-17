package com.ronscript.duterte.systems.graphics;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.SortedIteratingSystem;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.ronscript.duterte.GameAssets;
import com.ronscript.duterte.components.graphics.SpriteComponent;
import com.ronscript.duterte.components.properties.SizeComponent;
import com.ronscript.duterte.components.properties.TransformComponent;
import com.ronscript.duterte.utils.Constants;
import com.ronscript.duterte.utils.Mapper;

import java.util.Comparator;

/**
 * @author Ron
 * @since 8/8/2016
 */
public class RenderingSystem extends SortedIteratingSystem {

    TiledMap tiledMap;
    OrthogonalTiledMapRenderer tiledMapRenderer;
    int[] bottomLayers = { 0};
    int[] topLayers = {1, 2};
    private Batch batch;
    private Camera camera;

    public RenderingSystem(Batch batch, Camera camera) {
        super(Family.all(SpriteComponent.class, SizeComponent.class, TransformComponent.class).get(), new ZIndexComparator());
        this.batch = batch;
        this.camera = camera;
//        particleEffect.load(Gdx.files.internal("data/atlas/fire.p"), GameAssets.atlas);
//        particleEffect.setPosition(5,5);
//        particleEffect.scaleEffect(Constants.PIXELS_TO_METERS);
//
//        particleEffect.setDuration( 10 * 60 * 1000);
//        particleEffect.start();

        tiledMap = GameAssets.tiledMap;
        tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap, Constants.PIXELS_TO_METERS, batch);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        SpriteComponent sprite = Mapper.sprite.get(entity);
        SizeComponent size = Mapper.size.get(entity);
        TransformComponent transform = Mapper.transform.get(entity);
//        AlignmentComponent alignment = Mapper.alignment.get(entity);

        TextureRegion texture = sprite.textureRegion;

        if(texture == null) {
            return;
        }

        float originX = texture.getRegionWidth() * transform.origin.x;
        float originY = texture.getRegionHeight() * transform.origin.y;

        if(!size.dirty) {
            // if not dirty get size from texture
            float width = texture.getRegionWidth();
            float height = texture.getRegionHeight();
            size.width = width;
            size.height = height;
        }

        if(transform.dirty) {
//            forceSort();
            transform.dirty = false;
        }

        float positionX = transform.position.x - originX;
        float positionY = transform.position.y - originY;
//        float rotation = Mapper.fixedRotation.has(entity) ? 0 : transform.rotation * MathUtils.radiansToDegrees;

        batch.draw(texture.getTexture(),
                positionX,
                positionY,
                originX,
                originY,
                size.width  * sprite.widthRatio,
                size.height,
                transform.scale.x * Constants.PIXELS_TO_METERS,
                transform.scale.y * Constants.PIXELS_TO_METERS,
                transform.rotation * MathUtils.radiansToDegrees,
                texture.getRegionX() ,
                texture.getRegionY(),
                (int) (texture.getRegionWidth()  * sprite.widthRatio),
                texture.getRegionHeight(),
                false, false);

    }

    @Override
    public void update(float deltaTime) {
        batch.setProjectionMatrix(camera.combined);
        tiledMapRenderer.setView((OrthographicCamera) camera);
        tiledMapRenderer.render(bottomLayers);
        batch.begin();
        super.update(deltaTime);
        batch.end();

        tiledMapRenderer.render(topLayers);
    }

    private static class ZIndexComparator implements Comparator<Entity> {
        @Override
        public int compare(Entity entityA, Entity entityB) {
            return (int)Math.signum(Mapper.sprite.get(entityA).zIndex - Mapper.sprite.get(entityB).zIndex);
        }
    }
}
