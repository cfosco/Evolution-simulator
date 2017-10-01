/*
 * Decompiled with CFR 0_122.
 */
package com.redes.tpfinal.desktop;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.redes.tpfinal.TPFinal;

public class DesktopLauncher {
    public static void main(String[] arg) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.title = "TP Final Redes Neuronales";
        config.width = 1300;
        config.height = 700;
        new com.badlogic.gdx.backends.lwjgl.LwjglApplication((ApplicationListener)new TPFinal(), config);
    }
}

