/*
 * Decompiled with CFR 0_122.
 */
package com.badlogic.gdx.scenes.scene2d.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.BitmapFontCache;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

public class Window
extends Table {
    private static final Vector2 tmpPosition = new Vector2();
    private static final Vector2 tmpSize = new Vector2();
    private static final int MOVE = 32;
    private WindowStyle style;
    private String title;
    private BitmapFontCache titleCache;
    boolean isMovable = true;
    boolean isModal;
    boolean isResizable;
    int resizeBorder = 8;
    boolean dragging;
    private int titleAlignment = 1;
    boolean keepWithinStage = true;
    Table buttonTable;

    public Window(String title, Skin skin) {
        this(title, skin.get(WindowStyle.class));
        this.setSkin(skin);
    }

    public Window(String title, Skin skin, String styleName) {
        this(title, skin.get(styleName, WindowStyle.class));
        this.setSkin(skin);
    }

    public Window(String title, WindowStyle style) {
        if (title == null) {
            throw new IllegalArgumentException("title cannot be null.");
        }
        this.title = title;
        this.setTouchable(Touchable.enabled);
        this.setClip(true);
        this.setStyle(style);
        this.setWidth(150.0f);
        this.setHeight(150.0f);
        this.setTitle(title);
        this.buttonTable = new Table();
        this.addActor(this.buttonTable);
        this.addCaptureListener(new InputListener(){

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                Window.this.toFront();
                return false;
            }
        });
        this.addListener(new InputListener(){
            int edge;
            float startX;
            float startY;
            float lastX;
            float lastY;

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if (button == 0) {
                    int border = Window.this.resizeBorder;
                    float width = Window.this.getWidth();
                    float height = Window.this.getHeight();
                    this.edge = 0;
                    if (Window.this.isResizable) {
                        if (x < (float)border) {
                            this.edge |= 8;
                        }
                        if (x > width - (float)border) {
                            this.edge |= 16;
                        }
                        if (y < (float)border) {
                            this.edge |= 4;
                        }
                        if (y > height - (float)border) {
                            this.edge |= 2;
                        }
                        if (this.edge != 0) {
                            border += 25;
                        }
                        if (x < (float)border) {
                            this.edge |= 8;
                        }
                        if (x > width - (float)border) {
                            this.edge |= 16;
                        }
                        if (y < (float)border) {
                            this.edge |= 4;
                        }
                        if (y > height - (float)border) {
                            this.edge |= 2;
                        }
                    }
                    if (Window.this.isMovable && this.edge == 0 && y <= height && y >= height - Window.this.getPadTop() && x >= 0.0f && x <= width) {
                        this.edge = 32;
                    }
                    Window.this.dragging = this.edge != 0;
                    this.startX = x;
                    this.startY = y;
                    this.lastX = x;
                    this.lastY = y;
                }
                return this.edge != 0 || Window.this.isModal;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                Window.this.dragging = false;
            }

            @Override
            public void touchDragged(InputEvent event, float x, float y, int pointer) {
                float amountX;
                boolean clampPosition;
                float amountY;
                if (!Window.this.dragging) {
                    return;
                }
                float width = Window.this.getWidth();
                float height = Window.this.getHeight();
                float windowX = Window.this.getX();
                float windowY = Window.this.getY();
                float minWidth = Window.this.getMinWidth();
                float maxWidth = Window.this.getMaxWidth();
                float minHeight = Window.this.getMinHeight();
                float maxHeight = Window.this.getMaxHeight();
                Stage stage = Window.this.getStage();
                boolean bl = clampPosition = Window.this.keepWithinStage && Window.this.getParent() == stage.getRoot();
                if ((this.edge & 32) != 0) {
                    amountX = x - this.startX;
                    float amountY2 = y - this.startY;
                    windowX += amountX;
                    windowY += amountY2;
                }
                if ((this.edge & 8) != 0) {
                    amountX = x - this.startX;
                    if (width - amountX < minWidth) {
                        amountX = - minWidth - width;
                    }
                    if (clampPosition && windowX + amountX < 0.0f) {
                        amountX = - windowX;
                    }
                    width -= amountX;
                    windowX += amountX;
                }
                if ((this.edge & 4) != 0) {
                    amountY = y - this.startY;
                    if (height - amountY < minHeight) {
                        amountY = - minHeight - height;
                    }
                    if (clampPosition && windowY + amountY < 0.0f) {
                        amountY = - windowY;
                    }
                    height -= amountY;
                    windowY += amountY;
                }
                if ((this.edge & 16) != 0) {
                    amountX = x - this.lastX;
                    if (width + amountX < minWidth) {
                        amountX = minWidth - width;
                    }
                    if (clampPosition && windowX + width + amountX > stage.getWidth()) {
                        amountX = stage.getWidth() - windowX - width;
                    }
                    width += amountX;
                }
                if ((this.edge & 2) != 0) {
                    amountY = y - this.lastY;
                    if (height + amountY < minHeight) {
                        amountY = minHeight - height;
                    }
                    if (clampPosition && windowY + height + amountY > stage.getHeight()) {
                        amountY = stage.getHeight() - windowY - height;
                    }
                    height += amountY;
                }
                this.lastX = x;
                this.lastY = y;
                Window.this.setBounds(Math.round(windowX), Math.round(windowY), Math.round(width), Math.round(height));
            }

            @Override
            public boolean mouseMoved(InputEvent event, float x, float y) {
                return Window.this.isModal;
            }

            @Override
            public boolean scrolled(InputEvent event, float x, float y, int amount) {
                return Window.this.isModal;
            }

            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                return Window.this.isModal;
            }

            @Override
            public boolean keyUp(InputEvent event, int keycode) {
                return Window.this.isModal;
            }

            @Override
            public boolean keyTyped(InputEvent event, char character) {
                return Window.this.isModal;
            }
        });
    }

    public void setStyle(WindowStyle style) {
        if (style == null) {
            throw new IllegalArgumentException("style cannot be null.");
        }
        this.style = style;
        this.setBackground(style.background);
        this.titleCache = new BitmapFontCache(style.titleFont);
        this.titleCache.setColor(style.titleFontColor);
        if (this.title != null) {
            this.setTitle(this.title);
        }
        this.invalidateHierarchy();
    }

    public WindowStyle getStyle() {
        return this.style;
    }

    void keepWithinStage() {
        if (!this.keepWithinStage) {
            return;
        }
        Stage stage = this.getStage();
        if (this.getParent() == stage.getRoot()) {
            float parentWidth = stage.getWidth();
            float parentHeight = stage.getHeight();
            if (this.getX() < 0.0f) {
                this.setX(0.0f);
            }
            if (this.getRight() > parentWidth) {
                this.setX(parentWidth - this.getWidth());
            }
            if (this.getY() < 0.0f) {
                this.setY(0.0f);
            }
            if (this.getTop() > parentHeight) {
                this.setY(parentHeight - this.getHeight());
            }
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        Stage stage = this.getStage();
        if (stage.getKeyboardFocus() == null) {
            stage.setKeyboardFocus(this);
        }
        this.keepWithinStage();
        if (this.style.stageBackground != null) {
            this.stageToLocalCoordinates(tmpPosition.set(0.0f, 0.0f));
            this.stageToLocalCoordinates(tmpSize.set(stage.getWidth(), stage.getHeight()));
            this.drawStageBackground(batch, parentAlpha, this.getX() + Window.tmpPosition.x, this.getY() + Window.tmpPosition.y, this.getX() + Window.tmpSize.x, this.getY() + Window.tmpSize.y);
        }
        super.draw(batch, parentAlpha);
    }

    protected void drawStageBackground(Batch batch, float parentAlpha, float x, float y, float width, float height) {
        Color color = this.getColor();
        batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);
        this.style.stageBackground.draw(batch, x, y, width, height);
    }

    @Override
    protected void drawBackground(Batch batch, float parentAlpha, float x, float y) {
        float width = this.getWidth();
        float height = this.getHeight();
        float padTop = this.getPadTop();
        super.drawBackground(batch, parentAlpha, x, y);
        this.buttonTable.getColor().a = this.getColor().a;
        this.buttonTable.pack();
        this.buttonTable.setPosition(width - this.buttonTable.getWidth(), Math.min(height - padTop, height - this.buttonTable.getHeight()));
        this.buttonTable.draw(batch, parentAlpha);
        y += height;
        BitmapFont.TextBounds bounds = this.titleCache.getBounds();
        x = (this.titleAlignment & 8) != 0 ? (x += this.getPadLeft()) : ((this.titleAlignment & 16) != 0 ? (x += width - bounds.width - this.getPadRight()) : (x += (width - bounds.width) / 2.0f));
        if ((this.titleAlignment & 2) == 0) {
            y = (this.titleAlignment & 4) != 0 ? (y -= padTop - bounds.height) : (y -= (padTop - bounds.height) / 2.0f);
        }
        this.titleCache.tint(Color.tmp.set(this.getColor()).mul(this.style.titleFontColor));
        this.titleCache.setPosition((int)x, (int)y);
        this.titleCache.draw(batch, parentAlpha);
    }

    @Override
    public Actor hit(float x, float y, boolean touchable) {
        Actor hit = super.hit(x, y, touchable);
        if (hit == null && this.isModal && (!touchable || this.getTouchable() == Touchable.enabled)) {
            return this;
        }
        float height = this.getHeight();
        if (hit == null || hit == this) {
            return hit;
        }
        if (y <= height && y >= height - this.getPadTop() && x >= 0.0f && x <= this.getWidth()) {
            Actor current = hit;
            while (current.getParent() != this) {
                current = current.getParent();
            }
            if (this.getCell(current) != null) {
                return this;
            }
        }
        return hit;
    }

    public void setTitle(String title) {
        this.title = title;
        this.titleCache.setMultiLineText(title, 0.0f, 0.0f);
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitleAlignment(int titleAlignment) {
        this.titleAlignment = titleAlignment;
    }

    public boolean isMovable() {
        return this.isMovable;
    }

    public void setMovable(boolean isMovable) {
        this.isMovable = isMovable;
    }

    public boolean isModal() {
        return this.isModal;
    }

    public void setModal(boolean isModal) {
        this.isModal = isModal;
    }

    public void setKeepWithinStage(boolean keepWithinStage) {
        this.keepWithinStage = keepWithinStage;
    }

    public boolean isResizable() {
        return this.isResizable;
    }

    public void setResizable(boolean isResizable) {
        this.isResizable = isResizable;
    }

    public void setResizeBorder(int resizeBorder) {
        this.resizeBorder = resizeBorder;
    }

    public boolean isDragging() {
        return this.dragging;
    }

    public float getTitleWidth() {
        return this.titleCache.getBounds().width;
    }

    @Override
    public float getPrefWidth() {
        return Math.max(super.getPrefWidth(), this.getTitleWidth() + this.getPadLeft() + this.getPadRight());
    }

    public Table getButtonTable() {
        return this.buttonTable;
    }

    public static class WindowStyle {
        public Drawable background;
        public BitmapFont titleFont;
        public Color titleFontColor = new Color(1.0f, 1.0f, 1.0f, 1.0f);
        public Drawable stageBackground;

        public WindowStyle() {
        }

        public WindowStyle(BitmapFont titleFont, Color titleFontColor, Drawable background) {
            this.background = background;
            this.titleFont = titleFont;
            this.titleFontColor.set(titleFontColor);
        }

        public WindowStyle(WindowStyle style) {
            this.background = style.background;
            this.titleFont = style.titleFont;
            this.titleFontColor = new Color(style.titleFontColor);
        }
    }

}

