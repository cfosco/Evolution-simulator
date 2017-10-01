/*
 * Decompiled with CFR 0_122.
 */
package com.badlogic.gdx.backends.lwjgl;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.lwjgl.LwjglAWTCanvas;
import java.awt.Canvas;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import javax.swing.JFrame;

public class LwjglAWTFrame
extends JFrame {
    final LwjglAWTCanvas lwjglAWTCanvas;

    public LwjglAWTFrame(ApplicationListener listener, String title, int width, int height) {
        super(title);
        this.lwjglAWTCanvas = new LwjglAWTCanvas(listener){

            @Override
            protected void stopped() {
                LwjglAWTFrame.this.dispose();
            }

            @Override
            protected void setTitle(String title) {
                LwjglAWTFrame.this.setTitle(title);
            }

            @Override
            protected void setDisplayMode(int width, int height) {
                LwjglAWTFrame.this.getContentPane().setPreferredSize(new Dimension(width, height));
                LwjglAWTFrame.this.getContentPane().invalidate();
                LwjglAWTFrame.this.pack();
                LwjglAWTFrame.this.setLocationRelativeTo(null);
                LwjglAWTFrame.this.updateSize(width, height);
            }

            @Override
            protected void resize(int width, int height) {
                LwjglAWTFrame.this.updateSize(width, height);
            }

            @Override
            protected void start() {
                LwjglAWTFrame.this.start();
            }
        };
        this.getContentPane().add(this.lwjglAWTCanvas.getCanvas());
        Runtime.getRuntime().addShutdownHook(new Thread(){

            @Override
            public void run() {
                Runtime.getRuntime().halt(0);
            }
        });
        this.setDefaultCloseOperation(3);
        this.getContentPane().setPreferredSize(new Dimension(width, height));
        this.initialize();
        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);
        this.lwjglAWTCanvas.getCanvas().requestFocus();
    }

    protected void initialize() {
    }

    protected void start() {
    }

    public void updateSize(int width, int height) {
    }

    public LwjglAWTCanvas getLwjglAWTCanvas() {
        return this.lwjglAWTCanvas;
    }

}

