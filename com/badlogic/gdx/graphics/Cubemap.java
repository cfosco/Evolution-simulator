/*
 * Decompiled with CFR 0_122.
 */
package com.badlogic.gdx.graphics;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.CubemapLoader;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.CubemapData;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.GLTexture;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.TextureData;
import com.badlogic.gdx.graphics.glutils.FacedCubemapData;
import com.badlogic.gdx.graphics.glutils.PixmapTextureData;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Cubemap
extends GLTexture {
    private static AssetManager assetManager;
    static final Map<Application, Array<Cubemap>> managedCubemaps;
    protected CubemapData data;

    public Cubemap(CubemapData data) {
        super(34067);
        this.data = data;
        this.load(data);
    }

    public Cubemap(FileHandle positiveX, FileHandle negativeX, FileHandle positiveY, FileHandle negativeY, FileHandle positiveZ, FileHandle negativeZ) {
        this(positiveX, negativeX, positiveY, negativeY, positiveZ, negativeZ, false);
    }

    public Cubemap(FileHandle positiveX, FileHandle negativeX, FileHandle positiveY, FileHandle negativeY, FileHandle positiveZ, FileHandle negativeZ, boolean useMipMaps) {
        this(Cubemap.createTextureData(positiveX, useMipMaps), Cubemap.createTextureData(negativeX, useMipMaps), Cubemap.createTextureData(positiveY, useMipMaps), Cubemap.createTextureData(negativeY, useMipMaps), Cubemap.createTextureData(positiveZ, useMipMaps), Cubemap.createTextureData(negativeZ, useMipMaps));
    }

    public Cubemap(Pixmap positiveX, Pixmap negativeX, Pixmap positiveY, Pixmap negativeY, Pixmap positiveZ, Pixmap negativeZ) {
        this(positiveX, negativeX, positiveY, negativeY, positiveZ, negativeZ, false);
    }

    public Cubemap(Pixmap positiveX, Pixmap negativeX, Pixmap positiveY, Pixmap negativeY, Pixmap positiveZ, Pixmap negativeZ, boolean useMipMaps) {
        this(positiveX == null ? null : new PixmapTextureData(positiveX, null, useMipMaps, false), negativeX == null ? null : new PixmapTextureData(negativeX, null, useMipMaps, false), positiveY == null ? null : new PixmapTextureData(positiveY, null, useMipMaps, false), negativeY == null ? null : new PixmapTextureData(negativeY, null, useMipMaps, false), positiveZ == null ? null : new PixmapTextureData(positiveZ, null, useMipMaps, false), negativeZ == null ? null : new PixmapTextureData(negativeZ, null, useMipMaps, false));
    }

    public Cubemap(int width, int height, int depth, Pixmap.Format format) {
        this(new PixmapTextureData(new Pixmap(depth, height, format), null, false, true), new PixmapTextureData(new Pixmap(depth, height, format), null, false, true), new PixmapTextureData(new Pixmap(width, depth, format), null, false, true), new PixmapTextureData(new Pixmap(width, depth, format), null, false, true), new PixmapTextureData(new Pixmap(width, height, format), null, false, true), new PixmapTextureData(new Pixmap(width, height, format), null, false, true));
    }

    public Cubemap(TextureData positiveX, TextureData negativeX, TextureData positiveY, TextureData negativeY, TextureData positiveZ, TextureData negativeZ) {
        super(34067);
        this.minFilter = Texture.TextureFilter.Nearest;
        this.magFilter = Texture.TextureFilter.Nearest;
        this.uWrap = Texture.TextureWrap.ClampToEdge;
        this.vWrap = Texture.TextureWrap.ClampToEdge;
        this.data = new FacedCubemapData(positiveX, negativeX, positiveY, negativeY, positiveZ, negativeZ);
        this.load(this.data);
    }

    public void load(CubemapData data) {
        if (!data.isPrepared()) {
            data.prepare();
        }
        this.bind();
        this.unsafeSetFilter(this.minFilter, this.magFilter, true);
        this.unsafeSetWrap(this.uWrap, this.vWrap, true);
        data.consumeCubemapData();
        Gdx.gl.glBindTexture(this.glTarget, 0);
    }

    public CubemapData getCubemapData() {
        return this.data;
    }

    @Override
    public boolean isManaged() {
        return this.data.isManaged();
    }

    @Override
    protected void reload() {
        if (!this.isManaged()) {
            throw new GdxRuntimeException("Tried to reload an unmanaged Cubemap");
        }
        this.glHandle = Cubemap.createGLHandle();
        this.load(this.data);
    }

    @Override
    public int getWidth() {
        return this.data.getWidth();
    }

    @Override
    public int getHeight() {
        return this.data.getHeight();
    }

    @Override
    public int getDepth() {
        return 0;
    }

    @Override
    public void dispose() {
        if (this.glHandle == 0) {
            return;
        }
        this.delete();
        if (this.data.isManaged() && managedCubemaps.get(Gdx.app) != null) {
            managedCubemaps.get(Gdx.app).removeValue(this, true);
        }
    }

    private static void addManagedCubemap(Application app, Cubemap cubemap) {
        Array managedCubemapArray = managedCubemaps.get(app);
        if (managedCubemapArray == null) {
            managedCubemapArray = new Array();
        }
        managedCubemapArray.add(cubemap);
        managedCubemaps.put(app, managedCubemapArray);
    }

    public static void clearAllCubemaps(Application app) {
        managedCubemaps.remove(app);
    }

    public static void invalidateAllCubemaps(Application app) {
        Array<Cubemap> managedCubemapArray = managedCubemaps.get(app);
        if (managedCubemapArray == null) {
            return;
        }
        if (assetManager == null) {
            for (int i = 0; i < managedCubemapArray.size; ++i) {
                Cubemap cubemap = managedCubemapArray.get(i);
                cubemap.reload();
            }
        } else {
            assetManager.finishLoading();
            Array<Cubemap> cubemaps = new Array<Cubemap>(managedCubemapArray);
            for (Cubemap cubemap : cubemaps) {
                String fileName = assetManager.getAssetFileName(cubemap);
                if (fileName == null) {
                    cubemap.reload();
                    continue;
                }
                final int refCount = assetManager.getReferenceCount(fileName);
                assetManager.setReferenceCount(fileName, 0);
                cubemap.glHandle = 0;
                CubemapLoader.CubemapParameter params = new CubemapLoader.CubemapParameter();
                params.cubemapData = cubemap.getCubemapData();
                params.minFilter = cubemap.getMinFilter();
                params.magFilter = cubemap.getMagFilter();
                params.wrapU = cubemap.getUWrap();
                params.wrapV = cubemap.getVWrap();
                params.cubemap = cubemap;
                params.loadedCallback = new AssetLoaderParameters.LoadedCallback(){

                    @Override
                    public void finishedLoading(AssetManager assetManager, String fileName, Class type) {
                        assetManager.setReferenceCount(fileName, refCount);
                    }
                };
                assetManager.unload(fileName);
                cubemap.glHandle = GLTexture.createGLHandle();
                assetManager.load(fileName, Cubemap.class, params);
            }
            managedCubemapArray.clear();
            managedCubemapArray.addAll(cubemaps);
        }
    }

    public static void setAssetManager(AssetManager manager) {
        assetManager = manager;
    }

    public static String getManagedStatus() {
        StringBuilder builder = new StringBuilder();
        builder.append("Managed cubemap/app: { ");
        for (Application app : managedCubemaps.keySet()) {
            builder.append(Cubemap.managedCubemaps.get((Object)app).size);
            builder.append(" ");
        }
        builder.append("}");
        return builder.toString();
    }

    public static int getNumManagedCubemaps() {
        return Cubemap.managedCubemaps.get((Object)Gdx.app).size;
    }

    static {
        managedCubemaps = new HashMap<Application, Array<Cubemap>>();
    }

    public static enum CubemapSide {
        PositiveX(0, 34069),
        NegativeX(1, 34070),
        PositiveY(2, 34071),
        NegativeY(3, 34072),
        PositiveZ(4, 34073),
        NegativeZ(5, 34074);
        
        public final int index;
        public final int glEnum;

        private CubemapSide(int index, int glEnum) {
            this.index = index;
            this.glEnum = glEnum;
        }

        public int getGLEnum() {
            return this.glEnum;
        }
    }

}

