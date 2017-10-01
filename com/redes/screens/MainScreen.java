/*
 * Decompiled with CFR 0_122.
 */
package com.redes.screens;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.redes.helpers.InputHandler;
import com.redes.helpers.TPGlobals;
import com.redes.tpfinal.TPFinal;
import com.redes.ui.UIHandler;
import com.redes.world.TPRenderer;
import com.redes.world.TPWorld;

public class MainScreen
implements Screen {
    private TPWorld world;
    private TPRenderer renderer;
    private UIHandler uih;
    private float runTime = 0.0f;
    private InputHandler ih;

    public MainScreen(TPFinal tp) {
        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();
        float gameWidth = 500.0f;
        float totalHeight = screenHeight / (screenWidth / gameWidth);
        TPGlobals.gameWidth = gameWidth;
        TPGlobals.gameHeight = totalHeight * 0.75f;
        TPGlobals.totalHeight = totalHeight;
        TPGlobals.statHeight = totalHeight - TPGlobals.gameHeight;
        Gdx.app.log("MainScreen", "totalHeight:" + totalHeight + " gameHeight: " + TPGlobals.gameHeight);
        this.world = new TPWorld();
        this.uih = new UIHandler();
        this.ih = new InputHandler(this.world, this.uih, screenWidth / gameWidth, screenHeight / totalHeight, totalHeight);
        Gdx.input.setInputProcessor(this.ih);
        this.renderer = new TPRenderer(this.world, this.uih);
        this.world.setRenderer(this.renderer);
        this.world.setInputHandler(this.ih);
    }

    @Override
    public void render(float delta) {
        this.runTime += delta;
        this.world.update(delta);
        this.renderer.render(delta);
    }

    @Override
    public void resize(int width, int height) {
        Gdx.app.log("GameScreen", "resizing: w:" + width + " h:" + height);
        this.ih.setFactors((float)width / TPGlobals.gameWidth, (float)height / TPGlobals.totalHeight);
    }

    @Override
    public void show() {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
    }
}

