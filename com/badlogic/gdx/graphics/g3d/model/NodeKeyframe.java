/*
 * Decompiled with CFR 0_122.
 */
package com.badlogic.gdx.graphics.g3d.model;

import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;

public class NodeKeyframe {
    public float keytime;
    public final Vector3 translation = new Vector3();
    public final Vector3 scale = new Vector3(1.0f, 1.0f, 1.0f);
    public final Quaternion rotation = new Quaternion();
}

