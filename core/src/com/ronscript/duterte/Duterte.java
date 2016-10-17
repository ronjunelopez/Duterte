package com.ronscript.duterte;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Duterte extends Game {

	SpriteBatch batch;
	AssetManager assetManager;
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		assetManager = new AssetManager();
		GameAssets.load(assetManager);

		setScreen(new PlayScreen(batch));
	}

	@Override
	public void render () {
		super.render();
	}
}
