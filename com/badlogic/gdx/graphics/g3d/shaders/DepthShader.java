/*
 * Decompiled with CFR 0_122.
 */
package com.badlogic.gdx.graphics.g3d.shaders;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Attribute;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.FloatAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix4;

public class DepthShader
extends DefaultShader {
    private static String defaultVertexShader = null;
    private static String defaultFragmentShader = null;
    public final int numBones;
    public final int weights;
    private final FloatAttribute alphaTestAttribute;

    public static final String getDefaultVertexShader() {
        if (defaultVertexShader == null) {
            defaultVertexShader = Gdx.files.classpath("com/badlogic/gdx/graphics/g3d/shaders/depth.vertex.glsl").readString();
        }
        return defaultVertexShader;
    }

    public static final String getDefaultFragmentShader() {
        if (defaultFragmentShader == null) {
            defaultFragmentShader = Gdx.files.classpath("com/badlogic/gdx/graphics/g3d/shaders/depth.fragment.glsl").readString();
        }
        return defaultFragmentShader;
    }

    public static String createPrefix(Renderable renderable, Config config) {
        String prefix = DefaultShader.createPrefix(renderable, config);
        if (!config.depthBufferOnly) {
            prefix = prefix + "#define PackedDepthFlag\n";
        }
        return prefix;
    }

    public DepthShader(Renderable renderable) {
        this(renderable, new Config());
    }

    public DepthShader(Renderable renderable, Config config) {
        this(renderable, config, DepthShader.createPrefix(renderable, config));
    }

    public DepthShader(Renderable renderable, Config config, String prefix) {
        this(renderable, config, prefix, config.vertexShader != null ? config.vertexShader : DepthShader.getDefaultVertexShader(), config.fragmentShader != null ? config.fragmentShader : DepthShader.getDefaultFragmentShader());
    }

    public DepthShader(Renderable renderable, Config config, String prefix, String vertexShader, String fragmentShader) {
        this(renderable, config, new ShaderProgram(prefix + vertexShader, prefix + fragmentShader));
    }

    public DepthShader(Renderable renderable, Config config, ShaderProgram shaderProgram) {
        super(renderable, (DefaultShader.Config)config, shaderProgram);
        this.numBones = renderable.bones == null ? 0 : config.numBones;
        int w = 0;
        int n = renderable.mesh.getVertexAttributes().size();
        for (int i = 0; i < n; ++i) {
            VertexAttribute attr = renderable.mesh.getVertexAttributes().get(i);
            if (attr.usage != 64) continue;
            w |= 1 << attr.unit;
        }
        this.weights = w;
        this.alphaTestAttribute = new FloatAttribute(FloatAttribute.AlphaTest, config.defaultAlphaTest);
    }

    @Override
    public void begin(Camera camera, RenderContext context) {
        super.begin(camera, context);
    }

    @Override
    public void end() {
        super.end();
    }

    @Override
    public boolean canRender(Renderable renderable) {
        boolean skinned;
        if (renderable.material.has(BlendingAttribute.Type)) {
            if ((this.materialMask & BlendingAttribute.Type) != BlendingAttribute.Type) {
                return false;
            }
            if (renderable.material.has(TextureAttribute.Diffuse) != ((this.materialMask & TextureAttribute.Diffuse) == TextureAttribute.Diffuse)) {
                return false;
            }
        }
        if ((skinned = (renderable.mesh.getVertexAttributes().getMask() & 64) == 64) != this.numBones > 0) {
            return false;
        }
        if (!skinned) {
            return true;
        }
        int w = 0;
        int n = renderable.mesh.getVertexAttributes().size();
        for (int i = 0; i < n; ++i) {
            VertexAttribute attr = renderable.mesh.getVertexAttributes().get(i);
            if (attr.usage != 64) continue;
            w |= 1 << attr.unit;
        }
        return w == this.weights;
    }

    @Override
    public void render(Renderable renderable) {
        if (renderable.material.has(BlendingAttribute.Type)) {
            BlendingAttribute blending = (BlendingAttribute)renderable.material.get(BlendingAttribute.Type);
            renderable.material.remove(BlendingAttribute.Type);
            boolean hasAlphaTest = renderable.material.has(FloatAttribute.AlphaTest);
            if (!hasAlphaTest) {
                renderable.material.set((Attribute)this.alphaTestAttribute);
            }
            if (blending.opacity >= ((FloatAttribute)renderable.material.get((long)FloatAttribute.AlphaTest)).value) {
                super.render(renderable);
            }
            if (!hasAlphaTest) {
                renderable.material.remove(FloatAttribute.AlphaTest);
            }
            renderable.material.set((Attribute)blending);
        } else {
            super.render(renderable);
        }
    }

    public static class Config
    extends DefaultShader.Config {
        public boolean depthBufferOnly = false;
        public float defaultAlphaTest = 0.5f;

        public Config() {
            this.defaultCullFace = 1028;
        }

        public Config(String vertexShader, String fragmentShader) {
            super(vertexShader, fragmentShader);
        }
    }

}

