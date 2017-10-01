/*
 * Decompiled with CFR 0_122.
 */
package com.redes.helpers;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class AssetLoader {
    public static Texture creaTex;
    public static Texture nodeTex;
    public static Texture highlighterTex;
    public static TextureRegion windowBg;
    public static TextureRegion creatureSprite1;
    public static TextureRegion node;
    public static TextureRegion maxLifeSprite;
    public static TextureRegion secondMaxLifeSprite;
    public static TextureRegion maxFoodSprite;
    public static TextureRegion maxChildSprite;
    public static TextureRegion mutatedSprite;
    public static TextureRegion crossoverSprite;
    public static TextureRegion secondMaxFoodSprite;
    public static TextureRegion circleHl;
    public static TextureRegion glowHl;
    public static BitmapFont whiteFont;
    public static BitmapFont courierNewBlackFont;
    public static FileHandle statsFile;

    public static void load() {
        creaTex = new Texture(Gdx.files.internal("data/creatureTextures.png"));
        highlighterTex = new Texture(Gdx.files.internal("data/selectorTextures.png"));
        nodeTex = new Texture(Gdx.files.internal("data/Node.png"));
        windowBg = AssetLoader.node = new TextureRegion(nodeTex);
        circleHl = new TextureRegion(highlighterTex, 0, 0, 250, 250);
        glowHl = new TextureRegion(highlighterTex, 250, 0, 250, 250);
        creatureSprite1 = new TextureRegion(creaTex, 0, 0, 250, 250);
        maxLifeSprite = new TextureRegion(creaTex, 250, 0, 250, 250);
        secondMaxLifeSprite = new TextureRegion(creaTex, 500, 0, 250, 250);
        maxFoodSprite = new TextureRegion(creaTex, 750, 0, 250, 250);
        maxChildSprite = new TextureRegion(creaTex, 0, 250, 250, 250);
        mutatedSprite = new TextureRegion(creaTex, 250, 250, 250, 250);
        crossoverSprite = new TextureRegion(creaTex, 500, 250, 250, 250);
        secondMaxFoodSprite = new TextureRegion(creaTex, 750, 250, 250, 250);
        AssetLoader.loadFonts();
        AssetLoader.loadFiles();
    }

    private static void loadFonts() {
        whiteFont = new BitmapFont(Gdx.files.internal("data/Fonts/RetroComputerOutlined.fnt"));
        courierNewBlackFont = new BitmapFont(Gdx.files.internal("data/Fonts/CourierNewBlack.fnt"));
        whiteFont.setScale(0.2f);
        courierNewBlackFont.setScale(1.3f);
    }

    public static void dispose() {
        creaTex.dispose();
        nodeTex.dispose();
    }

    private static void loadFiles() {
        statsFile = Gdx.files.local("data/Files/statsFile2.txt");
        statsFile.writeString("", false);
    }
}

