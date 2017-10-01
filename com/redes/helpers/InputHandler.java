/*
 * Decompiled with CFR 0_122.
 */
package com.redes.helpers;

import com.badlogic.gdx.InputProcessor;
import com.redes.creature.Creature;
import com.redes.helpers.TPGlobals;
import com.redes.ui.UIHandler;
import com.redes.world.TPWorld;

public class InputHandler
implements InputProcessor {
    TPWorld world;
    UIHandler uih;
    float scaleFactorX;
    float scaleFactorY;
    float gameHeight;

    public InputHandler(TPWorld world, UIHandler uih, float scaleFactX, float scaleFactY, float gameHeight) {
        this.world = world;
        this.uih = uih;
        this.scaleFactorX = scaleFactX;
        this.scaleFactorY = scaleFactY;
        this.gameHeight = gameHeight;
    }

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == 36) {
            this.toggleHelp();
        }
        if (keycode == 47) {
            this.toggleStats();
        }
        if (keycode == 22) {
            this.selectNextCreature();
        }
        if (keycode == 21) {
            this.selectPreviousCreature();
        }
        if (keycode == 19) {
            this.selectLastCreature();
        }
        if (keycode == 20) {
            this.selectFirstCreature();
        }
        if (keycode == 62) {
            this.selectBestCreature();
        }
        if (keycode == 46) {
            this.randomCreatures();
        }
        if (keycode == 44) {
            this.showParameterWindow();
        }
        return false;
    }

    public void toggleHelp() {
        this.uih.toggleHelpWindow();
    }

    public void toggleStats() {
        this.uih.toggleStats();
    }

    public void showParameterWindow() {
        this.uih.showParameterWindow();
    }

    public void selectNextCreature() {
        this.world.selectCreature("next");
    }

    public void selectPreviousCreature() {
        this.world.selectCreature("prev");
    }

    public void selectFirstCreature() {
        this.world.selectCreature("first");
    }

    public void selectLastCreature() {
        this.world.selectCreature("last");
    }

    public void selectBestCreature() {
        this.world.selectCreature("best");
    }

    public void randomCreatures() {
        int i = 0;
        while (i < 10) {
            this.world.addRandomCreature();
            ++i;
        }
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        float y;
        float x = this.scaleX(screenX);
        Creature c = this.world.getTouchedCreature(x, y = this.scaleY(screenY));
        if (c == null) {
            return true;
        }
        TPGlobals.currentCreature = c.getID();
        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }

    private float scaleX(int screenX) {
        return (float)screenX / this.scaleFactorX;
    }

    private float scaleY(int screenY) {
        return - (float)screenY / this.scaleFactorY + this.gameHeight;
    }

    public void setFactors(float sfx, float sfy) {
        this.scaleFactorX = sfx;
        this.scaleFactorY = sfy;
    }
}

