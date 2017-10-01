/*
 * Decompiled with CFR 0_122.
 */
package com.badlogic.gdx.graphics.g3d.model;

import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.model.MeshPart;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.utils.ArrayMap;

public class NodePart {
    public MeshPart meshPart;
    public Material material;
    public ArrayMap<Node, Matrix4> invBoneBindTransforms;
    public Matrix4[] bones;
    public boolean enabled = true;

    public NodePart() {
    }

    public NodePart(MeshPart meshPart, Material material) {
        this.meshPart = meshPart;
        this.material = material;
    }

    public Renderable setRenderable(Renderable out) {
        out.material = this.material;
        out.mesh = this.meshPart.mesh;
        out.meshPartOffset = this.meshPart.indexOffset;
        out.meshPartSize = this.meshPart.numVertices;
        out.primitiveType = this.meshPart.primitiveType;
        out.bones = this.bones;
        return out;
    }
}

