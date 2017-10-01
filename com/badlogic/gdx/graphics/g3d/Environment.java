/*
 * Decompiled with CFR 0_122.
 */
package com.badlogic.gdx.graphics.g3d;

import com.badlogic.gdx.graphics.g3d.Attributes;
import com.badlogic.gdx.graphics.g3d.environment.BaseLight;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.environment.PointLight;
import com.badlogic.gdx.graphics.g3d.environment.ShadowMap;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;

public class Environment
extends Attributes {
    public ShadowMap shadowMap;
    public final Array<DirectionalLight> directionalLights = new Array();
    public final Array<PointLight> pointLights = new Array();

    public /* varargs */ Environment add(BaseLight ... lights) {
        for (BaseLight light : lights) {
            this.add(light);
        }
        return this;
    }

    public Environment add(Array<BaseLight> lights) {
        for (BaseLight light : lights) {
            this.add(light);
        }
        return this;
    }

    public Environment add(BaseLight light) {
        if (light instanceof DirectionalLight) {
            this.directionalLights.add((DirectionalLight)light);
        } else if (light instanceof PointLight) {
            this.pointLights.add((PointLight)light);
        } else {
            throw new GdxRuntimeException("Unknown light type");
        }
        return this;
    }

    public /* varargs */ Environment remove(BaseLight ... lights) {
        for (BaseLight light : lights) {
            this.remove(light);
        }
        return this;
    }

    public Environment remove(Array<BaseLight> lights) {
        for (BaseLight light : lights) {
            this.remove(light);
        }
        return this;
    }

    public Environment remove(BaseLight light) {
        if (light instanceof DirectionalLight) {
            this.directionalLights.removeValue((DirectionalLight)light, false);
        } else if (light instanceof PointLight) {
            this.pointLights.removeValue((PointLight)light, false);
        } else {
            throw new GdxRuntimeException("Unknown light type");
        }
        return this;
    }

    @Override
    public void clear() {
        super.clear();
        this.directionalLights.clear();
        this.pointLights.clear();
    }
}

