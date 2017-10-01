/*
 * Decompiled with CFR 0_122.
 */
package com.redes.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.redes.helpers.AssetLoader;
import com.redes.helpers.TPGlobals;

public class UIHandler {
    boolean hwOn = false;

    public void toggleHelpWindow() {
        this.hwOn = !this.hwOn;
    }

    public void toggleStats() {
        TPGlobals.drawLongStats = !TPGlobals.drawLongStats;
    }

    public void showParameterWindow() {
    }

    public boolean isHelpWindow() {
        return this.hwOn;
    }

    public void drawHelpWindow(Batch batcher) {
        float w = TPGlobals.gameWidth * 0.7f;
        float h = TPGlobals.gameHeight * 0.7f;
        float x = TPGlobals.gameWidth / 2.0f - w / 2.0f;
        float y = TPGlobals.totalHeight / 2.0f - h / 2.0f;
        float margin = w * 0.032f;
        float scale = 0.25f;
        BitmapFont font = AssetLoader.courierNewBlackFont;
        Color c = batcher.getColor();
        batcher.setColor(1.0f, 1.0f, 1.0f, 0.8f);
        batcher.draw(AssetLoader.windowBg, x, y, 0.0f, 0.0f, w, h, 1.0f, 1.0f, 0.0f);
        batcher.setColor(c);
        String helpStr = "TP Final Redes Neuronales: Simulador de evolucion.\nComandos:\n-R: generar 10 criaturas aleatorias\n-S: Ver mas parametros\n-Espacio: Seleccionar criatura con mejor desempeno (mayor tiempo de vida)\n-Arriba: Selecionar ultima criatura generada\n-Hacer click sobre un agente: mostrar sus detalles\n-H: Cerrar este menu\n";
        this.drawTextWrapped(batcher, helpStr, x + margin, y + h - margin, w - 2.0f * margin, font, scale);
    }

    public void drawTextWrapped(Batch batcher, String str, float X, float Y, float areaWidth, BitmapFont font, float scale) {
        float prevScaleX = font.getScaleX();
        float prevScaleY = font.getScaleY();
        font.setScale(prevScaleX * scale, prevScaleY * scale);
        font.drawWrapped(batcher, str, X, Y, areaWidth);
        font.setScale(prevScaleX, prevScaleY);
    }
}

