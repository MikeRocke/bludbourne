package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.mygdx.game.screens.MainGameScreen;

public class BludBourne extends Game {

	private static MainGameScreen mainGameScreen = new MainGameScreen();
	
	@Override
	public void create () {
		setScreen(mainGameScreen);
	}
	
	@Override
	public void dispose () {
		mainGameScreen.dispose();
	}
}
