/*
 * Decompiled with CFR 0_122.
 */
package com.badlogic.gdx.graphics.g3d.utils;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;

public interface TextureProvider {
    public Texture load(String var1);

    public static class AssetTextureProvider
    implements TextureProvider {
        public final AssetManager assetManager;

        public AssetTextureProvider(AssetManager assetManager) {
            this.assetManager = assetManager;
        }

        @Override
        public Texture load(String fileName) {
            return this.assetManager.get(fileName, Texture.class);
        }
    }

    public static class FileTextureProvider
    implements TextureProvider {
        @Override
        public Texture load(String fileName) {
            Texture result = new Texture(Gdx.files.internal(fileName));
            result.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
            result.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
            return result;
        }
    }

}

