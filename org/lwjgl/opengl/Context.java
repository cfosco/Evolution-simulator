/*
 * Decompiled with CFR 0_122.
 */
package org.lwjgl.opengl;

import org.lwjgl.LWJGLException;

interface Context {
    public boolean isCurrent() throws LWJGLException;

    public void makeCurrent() throws LWJGLException;

    public void releaseCurrent() throws LWJGLException;

    public void releaseDrawable() throws LWJGLException;
}

