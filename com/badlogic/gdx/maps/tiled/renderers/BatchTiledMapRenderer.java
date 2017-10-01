/*
 * Decompiled with CFR 0_122.
 */
package com.badlogic.gdx.maps.tiled.renderers;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.tiles.AnimatedTiledMapTile;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Disposable;

public abstract class BatchTiledMapRenderer
implements TiledMapRenderer,
Disposable {
    protected TiledMap map;
    protected float unitScale;
    protected Batch batch;
    protected Rectangle viewBounds;
    protected boolean ownsBatch;
    protected float[] vertices = new float[20];

    public TiledMap getMap() {
        return this.map;
    }

    public void setMap(TiledMap map) {
        this.map = map;
    }

    public float getUnitScale() {
        return this.unitScale;
    }

    public Batch getBatch() {
        return this.batch;
    }

    public Rectangle getViewBounds() {
        return this.viewBounds;
    }

    public BatchTiledMapRenderer(TiledMap map) {
        this(map, 1.0f);
    }

    public BatchTiledMapRenderer(TiledMap map, float unitScale) {
        this.map = map;
        this.unitScale = unitScale;
        this.viewBounds = new Rectangle();
        this.batch = new SpriteBatch();
        this.ownsBatch = true;
    }

    public BatchTiledMapRenderer(TiledMap map, Batch batch) {
        this(map, 1.0f, batch);
    }

    public BatchTiledMapRenderer(TiledMap map, float unitScale, Batch batch) {
        this.map = map;
        this.unitScale = unitScale;
        this.viewBounds = new Rectangle();
        this.batch = batch;
        this.ownsBatch = false;
    }

    @Override
    public void setView(OrthographicCamera camera) {
        this.batch.setProjectionMatrix(camera.combined);
        float width = camera.viewportWidth * camera.zoom;
        float height = camera.viewportHeight * camera.zoom;
        this.viewBounds.set(camera.position.x - width / 2.0f, camera.position.y - height / 2.0f, width, height);
    }

    @Override
    public void setView(Matrix4 projection, float x, float y, float width, float height) {
        this.batch.setProjectionMatrix(projection);
        this.viewBounds.set(x, y, width, height);
    }

    @Override
    public void render() {
        this.beginRender();
        for (MapLayer layer : this.map.getLayers()) {
            if (!layer.isVisible()) continue;
            if (layer instanceof TiledMapTileLayer) {
                this.renderTileLayer((TiledMapTileLayer)layer);
                continue;
            }
            this.renderObjects(layer);
        }
        this.endRender();
    }

    @Override
    public void render(int[] layers) {
        this.beginRender();
        for (int layerIdx : layers) {
            MapLayer layer = this.map.getLayers().get(layerIdx);
            if (!layer.isVisible()) continue;
            if (layer instanceof TiledMapTileLayer) {
                this.renderTileLayer((TiledMapTileLayer)layer);
                continue;
            }
            this.renderObjects(layer);
        }
        this.endRender();
    }

    @Override
    public void renderObjects(MapLayer layer) {
        for (MapObject object : layer.getObjects()) {
            this.renderObject(object);
        }
    }

    @Override
    public void renderObject(MapObject object) {
    }

    protected void beginRender() {
        AnimatedTiledMapTile.updateAnimationBaseTime();
        this.batch.begin();
    }

    protected void endRender() {
        this.batch.end();
    }

    @Override
    public void dispose() {
        if (this.ownsBatch) {
            this.batch.dispose();
        }
    }
}

