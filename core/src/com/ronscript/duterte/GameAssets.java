package com.ronscript.duterte;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;

/**
 * @author Ron
 * @since 8/8/2016
 */
public class GameAssets {

    public static TextureAtlas atlas;
    public static Skin entitySkin;
//    public static TextureRegion cannon;
    public static TextureRegion bullet;
//    public static TextureRegion bullet_shadow;
    public static TextureRegion health_bar;
    public static TextureRegion health_background;
    // Animations character
//    public static Animation duterte_idle;
    public static Animation duterte_walk_front;
    public static Animation duterte_walk_back;
    public static Animation duterte_walk_left_side;
    public static Animation duterte_walk_right_side;
    public static Animation police_walk_front;
    public static Animation police_walk_back;
    public static Animation police_walk_left_side;
    public static Animation police_walk_right_side;
    public static Animation police_firing_left_side;
    public static Animation police_firing_right_side;
    // Items
    public static Animation coin_large;
    public static Animation explosion;
    public static Animation star;
    public static TiledMap tiledMap;

    // Map
    static Array<TextureRegion> frames = new Array<TextureRegion>();
    private AssetManager assetManager;

    public static void load(AssetManager manager) {

        manager.load("data/atlas/duterte.pack", TextureAtlas.class);
        manager.setLoader(TiledMap.class, new TmxMapLoader((new InternalFileHandleResolver())));
        manager.load("data/tiled/tmx-test.tmx", TiledMap.class);
        manager.finishLoading();

        tiledMap = manager.get("data/tiled/tmx-test.tmx");
        atlas = manager.get("data/atlas/duterte.pack");

        entitySkin = new Skin(atlas);

//        cannon = atlas.findRegion("items/cannon");
        bullet = atlas.findRegion("bullet");
//        bullet_shadow = atlas.findRegion("items/bullet_shadow");
        health_bar = atlas.findRegion("health_bar");
        health_background = atlas.findRegion("health_background");


        explosion = new Animation(0.3f, get("explosion", 1, 9, 16, 16));
        star = new Animation(0.1f, get("star", 1, 4, 16, 16));
        coin_large = new Animation(0.1f, atlas.findRegions("coin2"));
//        duterte_idle = new Animation(0.2f, atlas.findRegions("characters/duterte02_idle"));
        duterte_walk_front = new Animation(0.2f, atlas.findRegions("walk-cycle-front"));
        duterte_walk_back = new Animation(0.2f, atlas.findRegions("walk-cycle-back"));
        duterte_walk_left_side = new Animation(0.2f, atlas.findRegions("walk-cycle-side"));
        duterte_walk_right_side = new Animation(0.2f, atlas.findRegions("walk-cycle-right-side"));

        police_walk_front = new Animation(0.2f, atlas.findRegions("police-walk-front"));
        police_walk_back = new Animation(0.2f, atlas.findRegions("police-walk-back"));
        police_walk_left_side = new Animation(0.2f, atlas.findRegions("police-walk-left-side"));
        police_walk_right_side = new Animation(0.2f, atlas.findRegions("police-walk-right-side"));

//        police_walk_front = new Animation(0.2f, atlas.findRegions("police-walk-front"));
//        police_walk_back = new Animation(0.2f, atlas.findRegions("police-walk-back"));
        police_firing_left_side = new Animation(0.2f, atlas.findRegions("police-firing-left-side"));
        police_firing_right_side = new Animation(0.2f, atlas.findRegions("police-firing-right-side"));
    }

   private static Array<TextureRegion> get (String assetName, int col, int row, int tileWith, int tileHeight) {
       TextureRegion[][] tmpCoin2 = atlas.findRegion(assetName).split(tileWith, tileHeight);
       frames.clear();
       for(int w = 0; w < col; w++) {
           for (int h = 0; h < row; h++) {
               frames.add(tmpCoin2[w][h]);
           }
       }
       return frames;
   }


}
