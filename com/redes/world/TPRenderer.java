/*
 * Decompiled with CFR 0_122.
 */
package com.redes.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.redes.creature.Creature;
import com.redes.helpers.AssetLoader;
import com.redes.helpers.TPGlobals;
import com.redes.ui.UIHandler;
import com.redes.world.TPStats;
import com.redes.world.TPWorld;
import com.redes.worldobjects.Fire;
import com.redes.worldobjects.Food;
import java.util.List;

public class TPRenderer {
    private TPWorld world;
    private UIHandler uih;
    private OrthographicCamera cam;
    private ShapeRenderer shapeRenderer;
    private SpriteBatch batcher;

    public TPRenderer(TPWorld world, UIHandler uih) {
        this.uih = uih;
        this.world = world;
        this.setCam(new OrthographicCamera());
        this.getCam().setToOrtho(false, TPGlobals.gameWidth, TPGlobals.gameHeight + TPGlobals.statHeight);
        this.batcher = new SpriteBatch();
        this.shapeRenderer = new ShapeRenderer();
        this.batcher.setProjectionMatrix(this.getCam().combined);
        this.shapeRenderer.setProjectionMatrix(this.getCam().combined);
    }

    public void render(float delta) {
        Gdx.gl.glClearColor(0.4f, 0.7f, 0.8f, 1.0f);
        Gdx.gl.glClear(16384);
        this.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        this.drawStatZoneBg();
        this.shapeRenderer.end();
        this.batcher.begin();
        this.batcher.enableBlending();
        if (this.world.isRunning()) {
            if (TPGlobals.drawEcosistem) {
                this.drawEcosistem();
            }
            this.drawEcosistemStats();
            this.drawWindows();
        } else if (this.world.isMenu()) {
            this.drawMenuUI();
        }
        this.batcher.end();
    }

    public void drawStatZoneBg() {
        this.shapeRenderer.setColor(1.0f, 1.0f, 1.0f, 1.0f);
        this.shapeRenderer.rect(0.0f, TPGlobals.gameHeight, TPGlobals.gameWidth, TPGlobals.statHeight);
    }

    public void drawMenuUI() {
    }

    public OrthographicCamera getCam() {
        return this.cam;
    }

    public void setCam(OrthographicCamera cam) {
        this.cam = cam;
    }

    public void drawEcosistem() {
        for (Fire f : this.world.getFires()) {
            f.draw(this.batcher);
        }
        for (Creature c : this.world.getCreatures()) {
            c.draw(this.batcher);
        }
        this.drawEcosistemElements();
        if (TPGlobals.drawLineDirs) {
            this.drawCreatureLineDirs();
        }
    }

    public void drawCreatureLineDirs() {
        this.batcher.end();
        this.shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        for (Creature c : this.world.getCreatures()) {
            this.shapeRenderer.setColor(Color.BLACK);
            this.shapeRenderer.line(c.getPos(), c.getPos().cpy().add(c.getDir().cpy().scl(7.0f)));
        }
        this.shapeRenderer.end();
        this.batcher.begin();
    }

    public void drawEcosistemElements() {
        for (Food f : this.world.getFood()) {
            f.draw(this.batcher);
        }
    }

    public void drawEcosistemStats() {
        TPStats stats = this.world.getStats();
        BitmapFont font = AssetLoader.courierNewBlackFont;
        float margin = TPGlobals.gameWidth * 0.015f;
        float Y = TPGlobals.gameHeight + TPGlobals.statHeight - margin;
        float X = margin;
        float width = TPGlobals.gameWidth * 0.5f;
        float scale = 0.18f;
        if (TPGlobals.drawLongStats) {
            String statsStr = String.valueOf(String.format("Tiempo de ejecuci\u00f3n: %.1fs\n", Float.valueOf(stats.executionTime))) + String.format("Poblacion actual: %d\n", stats.currentPop) + String.format("Criaturas generadas: %d\n", stats.totalPop) + String.format("Comida actual: %d\n", stats.currentFood) + String.format("Maximos hijos: %d\n", stats.maxChildren) + String.format("Maxima generacion alcanzada: %d\n", stats.maxGen) + String.format("Maximo tiempo de vida: %.1f\n", Float.valueOf(stats.maxLifetime)) + String.format("Maximo tiempo de vida actual: %.1f\n", Float.valueOf(stats.currentMaxLifetime)) + String.format("Actual criatura con mejor desempeno: %d\n", stats.currentMaxLifetimeID) + String.format("Promedio de vida de las ultimas %d criaturas: %.2f\n", stats.q, Float.valueOf(stats.lifeTimeAvg)) + String.format("Promedio de comida de las ultimas %d criaturas: %.2f\n", stats.q, Float.valueOf(stats.foodQAvg));
            float prevScaleX = font.getScaleX();
            float prevScaleY = font.getScaleY();
            font.setScale(prevScaleX * scale, prevScaleY * scale);
            BitmapFont.TextBounds tb = font.getWrappedBounds(statsStr, width);
            Color c = this.batcher.getColor();
            this.batcher.setColor(1.0f, 1.0f, 1.0f, 0.7f);
            this.batcher.draw(AssetLoader.node, X - margin, Y - tb.height - margin, tb.width + 2.0f * margin, tb.height + 2.0f * margin);
            this.batcher.setColor(c);
            this.drawTextWrapped(this.batcher, statsStr, X, Y, width, font, 1.0f);
            font.setScale(prevScaleX, prevScaleY);
        } else {
            String statsStr = String.valueOf(String.format("Poblacion actual: %d\n", stats.currentPop)) + String.format("Criaturas generadas: %d\n", stats.totalPop) + String.format("Promedio de vida de las ultimas %d criaturas: %.2f\n", stats.q, Float.valueOf(stats.lifeTimeAvg)) + String.format("Promedio de comida de las ultimas %d criaturas: %.2f\n", stats.q, Float.valueOf(stats.foodQAvg)) + "Presione H para ventana de ayuda, S para ver mas parametros";
            this.drawTextWrapped(this.batcher, statsStr, X, Y, width, font, scale);
        }
    }

    public void drawWindows() {
        if (this.uih.isHelpWindow()) {
            this.uih.drawHelpWindow(this.batcher);
        }
    }

    public void drawTextWrapped(Batch batcher, String str, float X, float Y, float areaWidth, BitmapFont font, float scale) {
        float prevScaleX = font.getScaleX();
        float prevScaleY = font.getScaleY();
        font.setScale(prevScaleX * scale, prevScaleY * scale);
        font.drawWrapped(batcher, str, X, Y, areaWidth);
        font.setScale(prevScaleX, prevScaleY);
    }
}

