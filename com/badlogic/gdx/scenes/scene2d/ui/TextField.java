/*
 * Decompiled with CFR 0_122.
 */
package com.badlogic.gdx.scenes.scene2d.ui;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Disableable;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.UIUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Clipboard;
import com.badlogic.gdx.utils.FloatArray;
import com.badlogic.gdx.utils.SnapshotArray;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.Timer;

public class TextField
extends Widget
implements Disableable {
    private static final char BACKSPACE = '\b';
    protected static final char ENTER_DESKTOP = '\r';
    protected static final char ENTER_ANDROID = '\n';
    private static final char TAB = '\t';
    private static final char DELETE = '';
    private static final char BULLET = '\u0095';
    private static final Vector2 tmp1 = new Vector2();
    private static final Vector2 tmp2 = new Vector2();
    private static final Vector2 tmp3 = new Vector2();
    public static float keyRepeatInitialTime = 0.4f;
    public static float keyRepeatTime = 0.1f;
    protected String text;
    protected int cursor;
    protected int selectionStart;
    protected boolean hasSelection;
    protected boolean writeEnters;
    protected final FloatArray glyphAdvances = new FloatArray();
    protected final FloatArray glyphPositions = new FloatArray();
    TextFieldStyle style;
    private String messageText;
    protected CharSequence displayText;
    private Clipboard clipboard;
    InputListener inputListener;
    TextFieldListener listener;
    TextFieldFilter filter;
    OnscreenKeyboard keyboard = new DefaultOnscreenKeyboard();
    boolean focusTraversal = true;
    boolean onlyFontChars = true;
    boolean disabled;
    private int textHAlign = 8;
    private float selectionX;
    private float selectionWidth;
    boolean passwordMode;
    private StringBuilder passwordBuffer;
    private char passwordCharacter = 149;
    protected float textHeight;
    protected float textOffset;
    float renderOffset;
    private int visibleTextStart;
    private int visibleTextEnd;
    private int maxLength = 0;
    private float blinkTime = 0.32f;
    boolean cursorOn = true;
    long lastBlink;
    KeyRepeatTask keyRepeatTask;

    public TextField(String text, Skin skin) {
        this(text, skin.get(TextFieldStyle.class));
    }

    public TextField(String text, Skin skin, String styleName) {
        this(text, skin.get(styleName, TextFieldStyle.class));
    }

    public TextField(String text, TextFieldStyle style) {
        this.keyRepeatTask = new KeyRepeatTask();
        this.setStyle(style);
        this.clipboard = Gdx.app.getClipboard();
        this.initialize();
        this.setText(text);
        this.setSize(this.getPrefWidth(), this.getPrefHeight());
    }

    protected void initialize() {
        this.writeEnters = false;
        this.inputListener = this.createInputListener();
        this.addListener(this.inputListener);
    }

    protected InputListener createInputListener() {
        return new TextFieldClickListener();
    }

    protected int letterUnderCursor(float x) {
        x -= this.renderOffset + this.textOffset;
        int index = this.glyphPositions.size - 1;
        float[] glyphPositions = this.glyphPositions.items;
        int n = this.glyphPositions.size;
        for (int i = 0; i < n; ++i) {
            if (glyphPositions[i] <= x) continue;
            index = i - 1;
            break;
        }
        return Math.max(0, index);
    }

    protected boolean isWordCharacter(char c) {
        return c >= 'A' && c <= 'Z' || c >= 'a' && c <= 'z' || c >= '0' && c <= '9';
    }

    protected int[] wordUnderCursor(int at) {
        int index;
        String text = this.text;
        int start = at;
        int right = text.length();
        int left = 0;
        for (index = start; index < right; ++index) {
            if (this.isWordCharacter(text.charAt(index))) continue;
            right = index;
            break;
        }
        for (index = start - 1; index > -1; --index) {
            if (this.isWordCharacter(text.charAt(index))) continue;
            left = index + 1;
            break;
        }
        return new int[]{left, right};
    }

    int[] wordUnderCursor(float x) {
        return this.wordUnderCursor(this.letterUnderCursor(x));
    }

    boolean withinMaxLength(int size) {
        return this.maxLength <= 0 || size < this.maxLength;
    }

    public void setMaxLength(int maxLength) {
        this.maxLength = maxLength;
    }

    public int getMaxLength() {
        return this.maxLength;
    }

    public void setOnlyFontChars(boolean onlyFontChars) {
        this.onlyFontChars = onlyFontChars;
    }

    public void setStyle(TextFieldStyle style) {
        if (style == null) {
            throw new IllegalArgumentException("style cannot be null.");
        }
        this.style = style;
        this.textHeight = style.font.getCapHeight() - style.font.getDescent() * 2.0f;
        this.invalidateHierarchy();
    }

    public TextFieldStyle getStyle() {
        return this.style;
    }

    protected void calculateOffsets() {
        float position;
        float distance;
        float visibleWidth = this.getWidth();
        if (this.style.background != null) {
            visibleWidth -= this.style.background.getLeftWidth() + this.style.background.getRightWidth();
        }
        if ((distance = (position = this.glyphPositions.get(this.cursor)) - Math.abs(this.renderOffset)) <= 0.0f) {
            this.renderOffset = this.cursor > 0 ? - this.glyphPositions.get(this.cursor - 1) : 0.0f;
        } else if (distance > visibleWidth) {
            this.renderOffset -= distance - visibleWidth;
        }
        this.visibleTextStart = 0;
        this.textOffset = 0.0f;
        float start = Math.abs(this.renderOffset);
        int glyphCount = this.glyphPositions.size;
        float[] glyphPositions = this.glyphPositions.items;
        float startPos = 0.0f;
        for (int i = 0; i < glyphCount; ++i) {
            if (glyphPositions[i] < start) continue;
            this.visibleTextStart = i;
            startPos = glyphPositions[i];
            this.textOffset = startPos - start;
            break;
        }
        this.visibleTextEnd = Math.min(this.displayText.length(), this.cursor + 1);
        while (this.visibleTextEnd <= this.displayText.length() && glyphPositions[this.visibleTextEnd] - startPos <= visibleWidth) {
            ++this.visibleTextEnd;
        }
        this.visibleTextEnd = Math.max(0, this.visibleTextEnd - 1);
        if (this.hasSelection) {
            int minIndex = Math.min(this.cursor, this.selectionStart);
            int maxIndex = Math.max(this.cursor, this.selectionStart);
            float minX = Math.max(glyphPositions[minIndex], startPos);
            float maxX = Math.min(glyphPositions[maxIndex], glyphPositions[this.visibleTextEnd]);
            this.selectionX = minX;
            this.selectionWidth = maxX - minX;
        }
        if (this.textHAlign == 1 || this.textHAlign == 16) {
            this.textOffset = visibleWidth - (glyphPositions[this.visibleTextEnd] - startPos);
            if (this.textHAlign == 1) {
                this.textOffset = Math.round(this.textOffset * 0.5f);
            }
            if (this.hasSelection) {
                this.selectionX += this.textOffset;
            }
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        float yOffset;
        Stage stage = this.getStage();
        boolean focused = stage != null && stage.getKeyboardFocus() == this;
        BitmapFont font = this.style.font;
        Color fontColor = this.disabled && this.style.disabledFontColor != null ? this.style.disabledFontColor : (focused && this.style.focusedFontColor != null ? this.style.focusedFontColor : this.style.fontColor);
        Drawable selection = this.style.selection;
        Drawable cursorPatch = this.style.cursor;
        Drawable background = this.disabled && this.style.disabledBackground != null ? this.style.disabledBackground : (focused && this.style.focusedBackground != null ? this.style.focusedBackground : this.style.background);
        Color color = this.getColor();
        float x = this.getX();
        float y = this.getY();
        float width = this.getWidth();
        float height = this.getHeight();
        batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);
        float bgLeftWidth = 0.0f;
        if (background != null) {
            background.draw(batch, x, y, width, height);
            bgLeftWidth = background.getLeftWidth();
        }
        float textY = this.getTextY(font, background);
        this.calculateOffsets();
        if (focused && this.hasSelection && selection != null) {
            this.drawSelection(selection, batch, font, x + bgLeftWidth, y + textY);
        }
        float f = yOffset = font.isFlipped() ? - this.textHeight : 0.0f;
        if (this.displayText.length() == 0) {
            if (!focused && this.messageText != null) {
                if (this.style.messageFontColor != null) {
                    font.setColor(this.style.messageFontColor.r, this.style.messageFontColor.g, this.style.messageFontColor.b, this.style.messageFontColor.a * parentAlpha);
                } else {
                    font.setColor(0.7f, 0.7f, 0.7f, parentAlpha);
                }
                BitmapFont messageFont = this.style.messageFont != null ? this.style.messageFont : font;
                messageFont.draw(batch, this.messageText, x + bgLeftWidth, y + textY + yOffset);
            }
        } else {
            font.setColor(fontColor.r, fontColor.g, fontColor.b, fontColor.a * parentAlpha);
            this.drawText(batch, font, x + bgLeftWidth, y + textY + yOffset);
        }
        if (focused && !this.disabled) {
            this.blink();
            if (this.cursorOn && cursorPatch != null) {
                this.drawCursor(cursorPatch, batch, font, x + bgLeftWidth, y + textY);
            }
        }
    }

    protected float getTextY(BitmapFont font, Drawable background) {
        float height = this.getHeight();
        float textY = this.textHeight / 2.0f + font.getDescent();
        if (background != null) {
            float bottom = background.getBottomHeight();
            textY = (int)(textY + (height - background.getTopHeight() - bottom) / 2.0f + bottom);
        } else {
            textY = (int)(textY + height / 2.0f);
        }
        return textY;
    }

    protected void drawSelection(Drawable selection, Batch batch, BitmapFont font, float x, float y) {
        selection.draw(batch, x + this.selectionX + this.renderOffset, y - this.textHeight - font.getDescent(), this.selectionWidth, this.textHeight + font.getDescent() / 2.0f);
    }

    protected void drawText(Batch batch, BitmapFont font, float x, float y) {
        font.draw(batch, this.displayText, x + this.textOffset, y, this.visibleTextStart, this.visibleTextEnd);
    }

    protected void drawCursor(Drawable cursorPatch, Batch batch, BitmapFont font, float x, float y) {
        cursorPatch.draw(batch, x + this.textOffset + this.glyphPositions.get(this.cursor) - this.glyphPositions.items[this.visibleTextStart] - 1.0f, y - this.textHeight - font.getDescent(), cursorPatch.getMinWidth(), this.textHeight + font.getDescent() / 2.0f);
    }

    void updateDisplayText() {
        BitmapFont font = this.style.font;
        String text = this.text;
        int textLength = text.length();
        StringBuilder buffer = new StringBuilder();
        for (int i = 0; i < textLength; ++i) {
            char c = text.charAt(i);
            buffer.append(font.containsCharacter(c) ? c : ' ');
        }
        String newDisplayText = buffer.toString();
        if (this.passwordMode && font.containsCharacter(this.passwordCharacter)) {
            if (this.passwordBuffer == null) {
                this.passwordBuffer = new StringBuilder(newDisplayText.length());
            }
            if (this.passwordBuffer.length() > textLength) {
                this.passwordBuffer.setLength(textLength);
            } else {
                for (int i = this.passwordBuffer.length(); i < textLength; ++i) {
                    this.passwordBuffer.append(this.passwordCharacter);
                }
            }
            this.displayText = this.passwordBuffer;
        } else {
            this.displayText = newDisplayText;
        }
        font.computeGlyphAdvancesAndPositions(this.displayText, this.glyphAdvances, this.glyphPositions);
        if (this.selectionStart > newDisplayText.length()) {
            this.selectionStart = textLength;
        }
    }

    private void blink() {
        if (!Gdx.graphics.isContinuousRendering()) {
            this.cursorOn = true;
            return;
        }
        long time = TimeUtils.nanoTime();
        if ((float)(time - this.lastBlink) / 1.0E9f > this.blinkTime) {
            this.cursorOn = !this.cursorOn;
            this.lastBlink = time;
        }
    }

    public void copy() {
        if (this.hasSelection && !this.passwordMode) {
            this.clipboard.setContents(this.text.substring(Math.min(this.cursor, this.selectionStart), Math.max(this.cursor, this.selectionStart)));
        }
    }

    public void cut() {
        if (this.hasSelection && !this.passwordMode) {
            this.copy();
            this.cursor = this.delete();
        }
    }

    void paste() {
        this.paste(this.clipboard.getContents(), true);
    }

    void paste(String content, boolean onlyFontChars) {
        if (content == null) {
            return;
        }
        StringBuilder buffer = new StringBuilder();
        int textLength = this.text.length();
        int n = content.length();
        for (int i = 0; i < n && this.withinMaxLength(textLength + buffer.length()); ++i) {
            char c = content.charAt(i);
            if ((!this.writeEnters || c != '\n' && c != '\r') && (onlyFontChars && !this.style.font.containsCharacter(c) || this.filter != null && !this.filter.acceptChar(this, c))) continue;
            buffer.append(c);
        }
        content = buffer.toString();
        if (this.hasSelection) {
            this.cursor = this.delete(false);
        }
        this.text = this.insert(this.cursor, content, this.text);
        this.updateDisplayText();
        this.cursor += content.length();
    }

    String insert(int position, CharSequence text, String to) {
        if (to.length() == 0) {
            return text.toString();
        }
        return to.substring(0, position) + text + to.substring(position, to.length());
    }

    int delete() {
        return this.delete(true);
    }

    int delete(boolean updateText) {
        return this.delete(this.selectionStart, this.cursor, updateText);
    }

    int delete(int from, int to, boolean updateText) {
        int minIndex = Math.min(from, to);
        int maxIndex = Math.max(from, to);
        this.text = (minIndex > 0 ? this.text.substring(0, minIndex) : "") + (maxIndex < this.text.length() ? this.text.substring(maxIndex, this.text.length()) : "");
        if (updateText) {
            this.updateDisplayText();
        }
        this.clearSelection();
        return minIndex;
    }

    public void next(boolean up) {
        Stage stage = this.getStage();
        if (stage == null) {
            return;
        }
        this.getParent().localToStageCoordinates(tmp1.set(this.getX(), this.getY()));
        TextField textField = this.findNextTextField(stage.getActors(), null, tmp2, tmp1, up);
        if (textField == null) {
            if (up) {
                tmp1.set(Float.MIN_VALUE, Float.MIN_VALUE);
            } else {
                tmp1.set(Float.MAX_VALUE, Float.MAX_VALUE);
            }
            textField = this.findNextTextField(this.getStage().getActors(), null, tmp2, tmp1, up);
        }
        if (textField != null) {
            stage.setKeyboardFocus(textField);
        } else {
            Gdx.input.setOnscreenKeyboardVisible(false);
        }
    }

    private TextField findNextTextField(Array<Actor> actors, TextField best, Vector2 bestCoords, Vector2 currentCoords, boolean up) {
        int n = actors.size;
        for (int i = 0; i < n; ++i) {
            Actor actor = actors.get(i);
            if (actor == this) continue;
            if (actor instanceof TextField) {
                TextField textField = (TextField)actor;
                if (textField.isDisabled() || !textField.focusTraversal) continue;
                Vector2 actorCoords = actor.getParent().localToStageCoordinates(tmp3.set(actor.getX(), actor.getY()));
                if (!((actorCoords.y < currentCoords.y || actorCoords.y == currentCoords.y && actorCoords.x > currentCoords.x) ^ up) || best != null && !((actorCoords.y > bestCoords.y || actorCoords.y == bestCoords.y && actorCoords.x < bestCoords.x) ^ up)) continue;
                best = (TextField)actor;
                bestCoords.set(actorCoords);
                continue;
            }
            if (!(actor instanceof Group)) continue;
            best = this.findNextTextField(((Group)actor).getChildren(), best, bestCoords, currentCoords, up);
        }
        return best;
    }

    public InputListener getDefaultInputListener() {
        return this.inputListener;
    }

    public void setTextFieldListener(TextFieldListener listener) {
        this.listener = listener;
    }

    public void setTextFieldFilter(TextFieldFilter filter) {
        this.filter = filter;
    }

    public TextFieldFilter getTextFieldFilter() {
        return this.filter;
    }

    public void setFocusTraversal(boolean focusTraversal) {
        this.focusTraversal = focusTraversal;
    }

    public String getMessageText() {
        return this.messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public void appendText(String str) {
        if (str == null) {
            throw new IllegalArgumentException("text cannot be null.");
        }
        this.clearSelection();
        this.cursor = this.text.length();
        this.paste(str, this.onlyFontChars);
    }

    public void setText(String str) {
        if (str == null) {
            throw new IllegalArgumentException("text cannot be null.");
        }
        if (str.equals(this.text)) {
            return;
        }
        this.clearSelection();
        this.text = "";
        this.paste(str, this.onlyFontChars);
        this.cursor = 0;
    }

    public String getText() {
        return this.text;
    }

    public int getSelectionStart() {
        return this.selectionStart;
    }

    public String getSelection() {
        return this.hasSelection ? this.text.substring(Math.min(this.selectionStart, this.cursor), Math.max(this.selectionStart, this.cursor)) : "";
    }

    public void setSelection(int selectionStart, int selectionEnd) {
        if (selectionStart < 0) {
            throw new IllegalArgumentException("selectionStart must be >= 0");
        }
        if (selectionEnd < 0) {
            throw new IllegalArgumentException("selectionEnd must be >= 0");
        }
        selectionStart = Math.min(this.text.length(), selectionStart);
        selectionEnd = Math.min(this.text.length(), selectionEnd);
        if (selectionEnd == selectionStart) {
            this.clearSelection();
            return;
        }
        if (selectionEnd < selectionStart) {
            int temp = selectionEnd;
            selectionEnd = selectionStart;
            selectionStart = temp;
        }
        this.hasSelection = true;
        this.selectionStart = selectionStart;
        this.cursor = selectionEnd;
    }

    public void selectAll() {
        this.setSelection(0, this.text.length());
    }

    public void clearSelection() {
        this.hasSelection = false;
    }

    public void setCursorPosition(int cursorPosition) {
        if (cursorPosition < 0) {
            throw new IllegalArgumentException("cursorPosition must be >= 0");
        }
        this.clearSelection();
        this.cursor = Math.min(cursorPosition, this.text.length());
    }

    public int getCursorPosition() {
        return this.cursor;
    }

    public OnscreenKeyboard getOnscreenKeyboard() {
        return this.keyboard;
    }

    public void setOnscreenKeyboard(OnscreenKeyboard keyboard) {
        this.keyboard = keyboard;
    }

    public void setClipboard(Clipboard clipboard) {
        this.clipboard = clipboard;
    }

    @Override
    public float getPrefWidth() {
        return 150.0f;
    }

    @Override
    public float getPrefHeight() {
        float prefHeight = this.textHeight;
        if (this.style.background != null) {
            prefHeight = Math.max(prefHeight + this.style.background.getBottomHeight() + this.style.background.getTopHeight(), this.style.background.getMinHeight());
        }
        return prefHeight;
    }

    public void setAlignment(int alignment) {
        if (alignment == 8 || alignment == 1 || alignment == 16) {
            this.textHAlign = alignment;
        }
    }

    public void setPasswordMode(boolean passwordMode) {
        this.passwordMode = passwordMode;
        this.updateDisplayText();
    }

    public boolean isPasswordMode() {
        return this.passwordMode;
    }

    public void setPasswordCharacter(char passwordCharacter) {
        this.passwordCharacter = passwordCharacter;
        if (this.passwordMode) {
            this.updateDisplayText();
        }
    }

    public void setBlinkTime(float blinkTime) {
        this.blinkTime = blinkTime;
    }

    @Override
    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public boolean isDisabled() {
        return this.disabled;
    }

    protected void moveCursor(boolean forward, boolean jump) {
        int charOffset;
        int limit = forward ? this.text.length() : 0;
        int n = charOffset = forward ? 0 : -1;
        while ((forward ? ++this.cursor < limit : --this.cursor > limit) && jump && this.continueCursor(this.cursor, charOffset)) {
        }
    }

    protected boolean continueCursor(int index, int offset) {
        char c = this.text.charAt(index + offset);
        return this.isWordCharacter(c);
    }

    public static class TextFieldStyle {
        public BitmapFont font;
        public Color fontColor;
        public Color focusedFontColor;
        public Color disabledFontColor;
        public Drawable background;
        public Drawable focusedBackground;
        public Drawable disabledBackground;
        public Drawable cursor;
        public Drawable selection;
        public BitmapFont messageFont;
        public Color messageFontColor;

        public TextFieldStyle() {
        }

        public TextFieldStyle(BitmapFont font, Color fontColor, Drawable cursor, Drawable selection, Drawable background) {
            this.background = background;
            this.cursor = cursor;
            this.font = font;
            this.fontColor = fontColor;
            this.selection = selection;
        }

        public TextFieldStyle(TextFieldStyle style) {
            this.messageFont = style.messageFont;
            if (style.messageFontColor != null) {
                this.messageFontColor = new Color(style.messageFontColor);
            }
            this.background = style.background;
            this.focusedBackground = style.focusedBackground;
            this.disabledBackground = style.disabledBackground;
            this.cursor = style.cursor;
            this.font = style.font;
            if (style.fontColor != null) {
                this.fontColor = new Color(style.fontColor);
            }
            if (style.focusedFontColor != null) {
                this.focusedFontColor = new Color(style.focusedFontColor);
            }
            if (style.disabledFontColor != null) {
                this.disabledFontColor = new Color(style.disabledFontColor);
            }
            this.selection = style.selection;
        }
    }

    public class TextFieldClickListener
    extends ClickListener {
        @Override
        public void clicked(InputEvent event, float x, float y) {
            int count = this.getTapCount() % 4;
            if (count == 0) {
                TextField.this.clearSelection();
            }
            if (count == 2) {
                int[] array = TextField.this.wordUnderCursor(x);
                TextField.this.setSelection(array[0], array[1]);
            }
            if (count == 3) {
                TextField.this.selectAll();
            }
        }

        @Override
        public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
            if (!super.touchDown(event, x, y, pointer, button)) {
                return false;
            }
            if (pointer == 0 && button != 0) {
                return false;
            }
            if (TextField.this.disabled) {
                return true;
            }
            this.setCursorPosition(x, y);
            TextField.this.selectionStart = TextField.this.cursor;
            Stage stage = TextField.this.getStage();
            if (stage != null) {
                stage.setKeyboardFocus(TextField.this);
            }
            TextField.this.keyboard.show(true);
            TextField.this.hasSelection = true;
            return true;
        }

        @Override
        public void touchDragged(InputEvent event, float x, float y, int pointer) {
            super.touchDragged(event, x, y, pointer);
            this.setCursorPosition(x, y);
        }

        @Override
        public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
            if (TextField.this.selectionStart == TextField.this.cursor) {
                TextField.this.hasSelection = false;
            }
            super.touchUp(event, x, y, pointer, button);
        }

        protected void setCursorPosition(float x, float y) {
            TextField.this.lastBlink = 0;
            TextField.this.cursorOn = false;
            TextField.this.cursor = TextField.this.letterUnderCursor(x);
        }

        protected void goHome(boolean jump) {
            TextField.this.cursor = 0;
        }

        protected void goEnd(boolean jump) {
            TextField.this.cursor = TextField.this.text.length();
        }

        /*
         * Unable to fully structure code
         * Enabled aggressive block sorting
         * Lifted jumps to return sites
         */
        @Override
        public boolean keyDown(InputEvent event, int keycode) {
            if (TextField.this.disabled) {
                return false;
            }
            TextField.this.lastBlink = 0;
            TextField.this.cursorOn = false;
            stage = TextField.this.getStage();
            if (stage == null) return false;
            if (stage.getKeyboardFocus() != TextField.this) {
                return false;
            }
            repeat = false;
            ctrl = UIUtils.ctrl();
            v0 = jump = ctrl != false && TextField.this.passwordMode == false;
            if (ctrl) {
                if (keycode == 50) {
                    TextField.this.paste();
                    repeat = true;
                }
                if (keycode == 31 || keycode == 133) {
                    TextField.this.copy();
                    return true;
                }
                if (keycode == 52 || keycode == 67) {
                    TextField.this.cut();
                    return true;
                }
                if (keycode == 29) {
                    TextField.this.selectAll();
                    return true;
                }
            }
            if (!UIUtils.shift()) ** GOTO lbl49
            if (keycode == 133) {
                TextField.this.paste();
            }
            if (keycode == 112 && TextField.this.hasSelection) {
                TextField.this.copy();
                TextField.this.delete();
            }
            temp = TextField.this.cursor;
            if (keycode != 21) ** GOTO lbl36
            TextField.this.moveCursor(false, jump);
            repeat = true;
            ** GOTO lbl45
lbl36: // 1 sources:
            if (keycode != 22) ** GOTO lbl40
            TextField.this.moveCursor(true, jump);
            repeat = true;
            ** GOTO lbl45
lbl40: // 1 sources:
            if (keycode != 3) ** GOTO lbl43
            this.goHome(jump);
            ** GOTO lbl45
lbl43: // 1 sources:
            if (keycode == 132) {
                this.goEnd(jump);
lbl45: // 4 sources:
                if (!TextField.this.hasSelection) {
                    TextField.this.selectionStart = temp;
                    TextField.this.hasSelection = true;
                }
            }
            ** GOTO lbl63
lbl49: // 1 sources:
            if (keycode == 21) {
                TextField.this.moveCursor(false, jump);
                TextField.this.clearSelection();
                repeat = true;
            }
            if (keycode == 22) {
                TextField.this.moveCursor(true, jump);
                TextField.this.clearSelection();
                repeat = true;
            }
            if (keycode == 3) {
                this.goHome(jump);
                TextField.this.clearSelection();
            }
            if (keycode == 132) {
                this.goEnd(jump);
                TextField.this.clearSelection();
            }
lbl63: // 4 sources:
            TextField.this.cursor = MathUtils.clamp(TextField.this.cursor, 0, TextField.this.text.length());
            if (repeat == false) return true;
            this.scheduleKeyRepeatTask(keycode);
            return true;
        }

        protected void scheduleKeyRepeatTask(int keycode) {
            if (!TextField.this.keyRepeatTask.isScheduled() || TextField.this.keyRepeatTask.keycode != keycode) {
                TextField.this.keyRepeatTask.keycode = keycode;
                TextField.this.keyRepeatTask.cancel();
                Timer.schedule(TextField.this.keyRepeatTask, TextField.keyRepeatInitialTime, TextField.keyRepeatTime);
            }
        }

        @Override
        public boolean keyUp(InputEvent event, int keycode) {
            if (TextField.this.disabled) {
                return false;
            }
            TextField.this.keyRepeatTask.cancel();
            return true;
        }

        @Override
        public boolean keyTyped(InputEvent event, char character) {
            if (TextField.this.disabled) {
                return false;
            }
            if (character == '\u0000') {
                return false;
            }
            Stage stage = TextField.this.getStage();
            if (stage == null || stage.getKeyboardFocus() != TextField.this) {
                return false;
            }
            if ((character == '\t' || character == '\n') && TextField.this.focusTraversal) {
                TextField.this.next(UIUtils.shift());
            } else {
                boolean remove;
                boolean delete = character == '';
                boolean backspace = character == '\b';
                boolean add = TextField.this.style.font.containsCharacter(character) || TextField.this.writeEnters && (character == '\n' || character == '\r');
                boolean bl = remove = backspace || delete;
                if (add || remove) {
                    if (TextField.this.hasSelection) {
                        TextField.this.cursor = TextField.this.delete(false);
                    } else {
                        if (backspace && TextField.this.cursor > 0) {
                            TextField.this.text = TextField.this.text.substring(0, TextField.this.cursor - 1) + TextField.this.text.substring(TextField.this.cursor--);
                            TextField.this.renderOffset = 0.0f;
                        }
                        if (delete && TextField.this.cursor < TextField.this.text.length()) {
                            TextField.this.text = TextField.this.text.substring(0, TextField.this.cursor) + TextField.this.text.substring(TextField.this.cursor + 1);
                        }
                    }
                    if (add && !remove) {
                        boolean isEnter;
                        boolean bl2 = isEnter = character == '\r' || character == '\n';
                        if (!isEnter && TextField.this.filter != null && !TextField.this.filter.acceptChar(TextField.this, character)) {
                            return true;
                        }
                        if (!TextField.this.withinMaxLength(TextField.this.text.length())) {
                            return true;
                        }
                        String insertion = isEnter ? "\n" : String.valueOf(character);
                        TextField.this.text = TextField.this.insert(TextField.this.cursor++, insertion, TextField.this.text);
                    }
                    TextField.this.updateDisplayText();
                }
            }
            if (TextField.this.listener != null) {
                TextField.this.listener.keyTyped(TextField.this, character);
            }
            return true;
        }
    }

    public static class DefaultOnscreenKeyboard
    implements OnscreenKeyboard {
        @Override
        public void show(boolean visible) {
            Gdx.input.setOnscreenKeyboardVisible(visible);
        }
    }

    public static interface OnscreenKeyboard {
        public void show(boolean var1);
    }

    public static interface TextFieldFilter {
        public boolean acceptChar(TextField var1, char var2);

        public static class DigitsOnlyFilter
        implements TextFieldFilter {
            @Override
            public boolean acceptChar(TextField textField, char c) {
                return Character.isDigit(c);
            }
        }

    }

    public static interface TextFieldListener {
        public void keyTyped(TextField var1, char var2);
    }

    class KeyRepeatTask
    extends Timer.Task {
        int keycode;

        KeyRepeatTask() {
        }

        @Override
        public void run() {
            TextField.this.inputListener.keyDown(null, this.keycode);
        }
    }

}

