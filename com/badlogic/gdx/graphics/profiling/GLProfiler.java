/*
 * Decompiled with CFR 0_122.
 */
package com.badlogic.gdx.graphics.profiling;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.profiling.GL20Profiler;
import com.badlogic.gdx.graphics.profiling.GL30Profiler;
import com.badlogic.gdx.math.FloatCounter;

public abstract class GLProfiler {
    public static int calls;
    public static int textureBindings;
    public static int drawCalls;
    public static int shaderSwitches;
    public static FloatCounter vertexCount;

    public static void enable() {
        Gdx.gl30 = Gdx.gl30 == null ? null : new GL30Profiler(Gdx.gl30);
        Gdx.gl = Gdx.gl20 = Gdx.gl30 != null ? Gdx.gl30 : new GL20Profiler(Gdx.gl20);
    }

    public static void disable() {
        if (Gdx.gl30 != null && Gdx.gl30 instanceof GL30Profiler) {
            Gdx.gl30 = ((GL30Profiler)Gdx.gl30).gl30;
        }
        if (Gdx.gl20 != null && Gdx.gl20 instanceof GL20Profiler) {
            Gdx.gl20 = ((GL20Profiler)Gdx.gl).gl20;
        }
        if (Gdx.gl != null && Gdx.gl instanceof GL20Profiler) {
            Gdx.gl = ((GL20Profiler)Gdx.gl).gl20;
        }
    }

    public static void reset() {
        calls = 0;
        textureBindings = 0;
        drawCalls = 0;
        shaderSwitches = 0;
        vertexCount.reset();
    }

    static {
        vertexCount = new FloatCounter(0);
    }
}

