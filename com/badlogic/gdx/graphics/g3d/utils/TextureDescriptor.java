/*
 * Decompiled with CFR 0_122.
 */
package com.badlogic.gdx.graphics.g3d.utils;

import com.badlogic.gdx.graphics.GLTexture;
import com.badlogic.gdx.graphics.Texture;

public class TextureDescriptor<T extends GLTexture> {
    public T texture = null;
    public Texture.TextureFilter minFilter;
    public Texture.TextureFilter magFilter;
    public Texture.TextureWrap uWrap;
    public Texture.TextureWrap vWrap;

    public TextureDescriptor(T texture, Texture.TextureFilter minFilter, Texture.TextureFilter magFilter, Texture.TextureWrap uWrap, Texture.TextureWrap vWrap) {
        this.set(texture, minFilter, magFilter, uWrap, vWrap);
    }

    public TextureDescriptor(T texture) {
        this(texture, null, null, null, null);
    }

    public TextureDescriptor() {
    }

    public void set(T texture, Texture.TextureFilter minFilter, Texture.TextureFilter magFilter, Texture.TextureWrap uWrap, Texture.TextureWrap vWrap) {
        this.texture = texture;
        this.minFilter = minFilter;
        this.magFilter = magFilter;
        this.uWrap = uWrap;
        this.vWrap = vWrap;
    }

    public <V extends T> void set(TextureDescriptor<V> other) {
        this.texture = other.texture;
        this.minFilter = other.minFilter;
        this.magFilter = other.magFilter;
        this.uWrap = other.uWrap;
        this.vWrap = other.vWrap;
    }

    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof TextureDescriptor)) {
            return false;
        }
        TextureDescriptor other = (TextureDescriptor)obj;
        return other.texture == this.texture && other.minFilter == this.minFilter && other.magFilter == this.magFilter && other.uWrap == this.uWrap && other.vWrap == this.vWrap;
    }
}

