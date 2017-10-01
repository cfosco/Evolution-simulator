/*
 * Decompiled with CFR 0_122.
 */
package com.badlogic.gdx.backends.lwjgl;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Audio;
import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.LifecycleListener;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.backends.lwjgl.LwjglAWTInput;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.backends.lwjgl.LwjglClipboard;
import com.badlogic.gdx.backends.lwjgl.LwjglFiles;
import com.badlogic.gdx.backends.lwjgl.LwjglGraphics;
import com.badlogic.gdx.backends.lwjgl.LwjglNativesLoader;
import com.badlogic.gdx.backends.lwjgl.LwjglNet;
import com.badlogic.gdx.backends.lwjgl.LwjglPreferences;
import com.badlogic.gdx.backends.lwjgl.audio.OpenALAudio;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Clipboard;
import com.badlogic.gdx.utils.GdxRuntimeException;
import java.awt.Canvas;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.AWTGLCanvas;
import org.lwjgl.opengl.Drawable;
import org.lwjgl.opengl.PixelFormat;

public class LwjglAWTCanvas
implements Application {
    final LwjglGraphics graphics;
    final OpenALAudio audio;
    final LwjglFiles files;
    final LwjglAWTInput input;
    final LwjglNet net;
    final ApplicationListener listener;
    final AWTGLCanvas canvas;
    final List<Runnable> runnables = new ArrayList<Runnable>();
    final List<Runnable> executedRunnables = new ArrayList<Runnable>();
    final Array<LifecycleListener> lifecycleListeners = new Array();
    boolean running = true;
    int lastWidth;
    int lastHeight;
    int logLevel = 2;
    final String logTag = "LwjglAWTCanvas";
    private Cursor cursor;
    Map<String, Preferences> preferences = new HashMap<String, Preferences>();

    public LwjglAWTCanvas(ApplicationListener listener) {
        this(listener, null);
    }

    public LwjglAWTCanvas(ApplicationListener listener, LwjglAWTCanvas sharedContextCanvas) {
        LwjglNativesLoader.load();
        AWTGLCanvas sharedDrawable = sharedContextCanvas != null ? sharedContextCanvas.canvas : null;
        try {
            this.canvas = new AWTGLCanvas(GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice(), new PixelFormat(), sharedDrawable){
                private final Dimension minSize;

                @Override
                public Dimension getMinimumSize() {
                    return this.minSize;
                }

                @Override
                public void initGL() {
                    LwjglAWTCanvas.this.create();
                }

                @Override
                public void paintGL() {
                    try {
                        LwjglAWTCanvas.this.render();
                        this.swapBuffers();
                        this.repaint();
                    }
                    catch (LWJGLException ex) {
                        throw new GdxRuntimeException(ex);
                    }
                }
            };
        }
        catch (LWJGLException ex) {
            throw new GdxRuntimeException(ex);
        }
        this.graphics = new LwjglGraphics(this.canvas){

            @Override
            public void setTitle(String title) {
                super.setTitle(title);
                LwjglAWTCanvas.this.setTitle(title);
            }

            @Override
            public boolean setDisplayMode(int width, int height, boolean fullscreen) {
                if (!super.setDisplayMode(width, height, fullscreen)) {
                    return false;
                }
                if (!fullscreen) {
                    LwjglAWTCanvas.this.setDisplayMode(width, height);
                }
                return true;
            }

            @Override
            public boolean setDisplayMode(Graphics.DisplayMode displayMode) {
                if (!super.setDisplayMode(displayMode)) {
                    return false;
                }
                LwjglAWTCanvas.this.setDisplayMode(displayMode.width, displayMode.height);
                return true;
            }
        };
        if (!LwjglApplicationConfiguration.disableAudio && Gdx.audio == null) {
            this.audio = new OpenALAudio();
            Gdx.audio = this.audio;
        } else {
            this.audio = null;
        }
        if (Gdx.files == null) {
            this.files = new LwjglFiles();
            Gdx.files = this.files;
        } else {
            this.files = null;
        }
        if (Gdx.net == null) {
            this.net = new LwjglNet();
            Gdx.net = this.net;
        } else {
            this.net = null;
        }
        this.input = new LwjglAWTInput(this.canvas);
        this.listener = listener;
        this.setGlobals();
    }

    protected void setDisplayMode(int width, int height) {
    }

    protected void setTitle(String title) {
    }

    @Override
    public ApplicationListener getApplicationListener() {
        return this.listener;
    }

    public Canvas getCanvas() {
        return this.canvas;
    }

    @Override
    public Audio getAudio() {
        return Gdx.audio;
    }

    @Override
    public Files getFiles() {
        return this.files;
    }

    @Override
    public Graphics getGraphics() {
        return this.graphics;
    }

    @Override
    public Input getInput() {
        return this.input;
    }

    @Override
    public Net getNet() {
        return this.net;
    }

    @Override
    public Application.ApplicationType getType() {
        return Application.ApplicationType.Desktop;
    }

    @Override
    public int getVersion() {
        return 0;
    }

    void setGlobals() {
        Gdx.app = this;
        if (this.audio != null) {
            Gdx.audio = this.audio;
        }
        if (this.files != null) {
            Gdx.files = this.files;
        }
        if (this.net != null) {
            Gdx.net = this.net;
        }
        Gdx.graphics = this.graphics;
        Gdx.input = this.input;
    }

    void create() {
        try {
            this.setGlobals();
            this.graphics.initiateGLInstances();
            this.listener.create();
            this.lastWidth = Math.max(1, this.graphics.getWidth());
            this.lastHeight = Math.max(1, this.graphics.getHeight());
            this.listener.resize(this.lastWidth, this.lastHeight);
            this.start();
        }
        catch (Exception ex) {
            this.stopped();
            throw new GdxRuntimeException(ex);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    void render() {
        if (!this.running) {
            return;
        }
        this.setGlobals();
        this.canvas.setCursor(this.cursor);
        this.graphics.updateTime();
        int width = Math.max(1, this.graphics.getWidth());
        int height = Math.max(1, this.graphics.getHeight());
        if (this.lastWidth != width || this.lastHeight != height) {
            this.lastWidth = width;
            this.lastHeight = height;
            Gdx.gl.glViewport(0, 0, this.lastWidth, this.lastHeight);
            this.resize(width, height);
            this.listener.resize(width, height);
        }
        List<Runnable> list = this.runnables;
        synchronized (list) {
            this.executedRunnables.clear();
            this.executedRunnables.addAll(this.runnables);
            this.runnables.clear();
            for (int i = 0; i < this.executedRunnables.size(); ++i) {
                try {
                    this.executedRunnables.get(i).run();
                    continue;
                }
                catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        }
        this.input.processEvents();
        if (this.running) {
            ++this.graphics.frameId;
            this.listener.render();
            if (this.audio != null) {
                this.audio.update();
            }
        }
    }

    protected void start() {
    }

    protected void resize(int width, int height) {
    }

    protected void stopped() {
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void stop() {
        if (!this.running) {
            return;
        }
        this.running = false;
        this.setGlobals();
        Array<LifecycleListener> listeners = this.lifecycleListeners;
        if (this.canvas.isDisplayable()) {
            this.makeCurrent();
        } else {
            this.error("LwjglAWTCanvas", "OpenGL context destroyed before application listener has had a chance to dispose of textures.");
        }
        Array<LifecycleListener> array = listeners;
        synchronized (array) {
            for (LifecycleListener listener : listeners) {
                listener.pause();
                listener.dispose();
            }
        }
        this.listener.pause();
        this.listener.dispose();
        Gdx.app = null;
        Gdx.graphics = null;
        if (this.audio != null) {
            this.audio.dispose();
            Gdx.audio = null;
        }
        if (this.files != null) {
            Gdx.files = null;
        }
        if (this.net != null) {
            Gdx.net = null;
        }
        this.stopped();
    }

    @Override
    public long getJavaHeap() {
        return Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
    }

    @Override
    public long getNativeHeap() {
        return this.getJavaHeap();
    }

    @Override
    public Preferences getPreferences(String name) {
        if (this.preferences.containsKey(name)) {
            return this.preferences.get(name);
        }
        LwjglPreferences prefs = new LwjglPreferences(name, ".prefs/");
        this.preferences.put(name, prefs);
        return prefs;
    }

    @Override
    public Clipboard getClipboard() {
        return new LwjglClipboard();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void postRunnable(Runnable runnable) {
        List<Runnable> list = this.runnables;
        synchronized (list) {
            this.runnables.add(runnable);
        }
    }

    @Override
    public void debug(String tag, String message) {
        if (this.logLevel >= 3) {
            System.out.println(tag + ": " + message);
        }
    }

    @Override
    public void debug(String tag, String message, Throwable exception) {
        if (this.logLevel >= 3) {
            System.out.println(tag + ": " + message);
            exception.printStackTrace(System.out);
        }
    }

    @Override
    public void log(String tag, String message) {
        if (this.logLevel >= 2) {
            System.out.println(tag + ": " + message);
        }
    }

    @Override
    public void log(String tag, String message, Throwable exception) {
        if (this.logLevel >= 2) {
            System.out.println(tag + ": " + message);
            exception.printStackTrace(System.out);
        }
    }

    @Override
    public void error(String tag, String message) {
        if (this.logLevel >= 1) {
            System.err.println(tag + ": " + message);
        }
    }

    @Override
    public void error(String tag, String message, Throwable exception) {
        if (this.logLevel >= 1) {
            System.err.println(tag + ": " + message);
            exception.printStackTrace(System.err);
        }
    }

    @Override
    public void setLogLevel(int logLevel) {
        this.logLevel = logLevel;
    }

    @Override
    public int getLogLevel() {
        return this.logLevel;
    }

    @Override
    public void exit() {
        this.postRunnable(new Runnable(){

            @Override
            public void run() {
                LwjglAWTCanvas.this.stop();
                System.exit(-1);
            }
        });
    }

    public void makeCurrent() {
        try {
            this.canvas.makeCurrent();
            this.setGlobals();
        }
        catch (LWJGLException ex) {
            throw new GdxRuntimeException(ex);
        }
    }

    public boolean isCurrent() {
        try {
            return this.canvas.isCurrent();
        }
        catch (LWJGLException ex) {
            throw new GdxRuntimeException(ex);
        }
    }

    public void setCursor(Cursor cursor) {
        this.cursor = cursor;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void addLifecycleListener(LifecycleListener listener) {
        Array<LifecycleListener> array = this.lifecycleListeners;
        synchronized (array) {
            this.lifecycleListeners.add(listener);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeLifecycleListener(LifecycleListener listener) {
        Array<LifecycleListener> array = this.lifecycleListeners;
        synchronized (array) {
            this.lifecycleListeners.removeValue(listener, true);
        }
    }

}

