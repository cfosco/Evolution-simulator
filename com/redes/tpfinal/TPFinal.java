/*
 * Decompiled with CFR 0_122.
 */
package com.redes.tpfinal;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.redes.helpers.AssetLoader;
import com.redes.screens.MainScreen;

public class TPFinal
extends Game {
    @Override
    public void create() {
        AssetLoader.load();
        this.setScreen(new MainScreen(this));
    }

    @Override
    public void dispose() {
        super.dispose();
        AssetLoader.dispose();
    }
}

