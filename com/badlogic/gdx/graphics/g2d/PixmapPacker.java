/*
 * Decompiled with CFR 0_122.
 */
package com.badlogic.gdx.graphics.g2d;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.TextureData;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.PixmapTextureData;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.OrderedMap;

public class PixmapPacker
implements Disposable {
    final int pageWidth;
    final int pageHeight;
    final Pixmap.Format pageFormat;
    final int padding;
    final boolean duplicateBorder;
    final Array<Page> pages = new Array();
    Page currPage;
    boolean disposed;

    public PixmapPacker(int width, int height, Pixmap.Format format, int padding, boolean duplicateBorder) {
        this.pageWidth = width;
        this.pageHeight = height;
        this.pageFormat = format;
        this.padding = padding;
        this.duplicateBorder = duplicateBorder;
        this.newPage();
    }

    public synchronized Rectangle pack(String name, Pixmap image) {
        if (this.disposed) {
            return null;
        }
        if (this.getRect(name) != null) {
            throw new RuntimeException("Key with name '" + name + "' is already in map");
        }
        int borderPixels = this.padding + (this.duplicateBorder ? 1 : 0);
        Rectangle rect = new Rectangle(0.0f, 0.0f, image.getWidth() + borderPixels, image.getHeight() + (borderPixels <<= 1));
        if (rect.getWidth() > (float)this.pageWidth || rect.getHeight() > (float)this.pageHeight) {
            throw new GdxRuntimeException("page size for '" + name + "' to small");
        }
        Node node = this.insert(this.currPage.root, rect);
        if (node == null) {
            this.newPage();
            return this.pack(name, image);
        }
        node.leaveName = name;
        rect = new Rectangle(node.rect);
        rect.width -= (float)borderPixels;
        rect.height -= (float)borderPixels;
        rect.x += (float)(borderPixels >>= 1);
        rect.y += (float)borderPixels;
        this.currPage.rects.put(name, rect);
        Pixmap.Blending blending = Pixmap.getBlending();
        Pixmap.setBlending(Pixmap.Blending.None);
        this.currPage.image.drawPixmap(image, (int)rect.x, (int)rect.y);
        if (this.duplicateBorder) {
            int imageWidth = image.getWidth();
            int imageHeight = image.getHeight();
            this.currPage.image.drawPixmap(image, 0, 0, 1, 1, (int)rect.x - 1, (int)rect.y - 1, 1, 1);
            this.currPage.image.drawPixmap(image, imageWidth - 1, 0, 1, 1, (int)rect.x + (int)rect.width, (int)rect.y - 1, 1, 1);
            this.currPage.image.drawPixmap(image, 0, imageHeight - 1, 1, 1, (int)rect.x - 1, (int)rect.y + (int)rect.height, 1, 1);
            this.currPage.image.drawPixmap(image, imageWidth - 1, imageHeight - 1, 1, 1, (int)rect.x + (int)rect.width, (int)rect.y + (int)rect.height, 1, 1);
            this.currPage.image.drawPixmap(image, 0, 0, imageWidth, 1, (int)rect.x, (int)rect.y - 1, (int)rect.width, 1);
            this.currPage.image.drawPixmap(image, 0, imageHeight - 1, imageWidth, 1, (int)rect.x, (int)rect.y + (int)rect.height, (int)rect.width, 1);
            this.currPage.image.drawPixmap(image, 0, 0, 1, imageHeight, (int)rect.x - 1, (int)rect.y, 1, (int)rect.height);
            this.currPage.image.drawPixmap(image, imageWidth - 1, 0, 1, imageHeight, (int)rect.x + (int)rect.width, (int)rect.y, 1, (int)rect.height);
        }
        Pixmap.setBlending(blending);
        this.currPage.addedRects.add(name);
        return rect;
    }

    private void newPage() {
        Page page = new Page();
        page.image = new Pixmap(this.pageWidth, this.pageHeight, this.pageFormat);
        page.root = new Node(0, 0, this.pageWidth, this.pageHeight, null, null, null);
        page.rects = new OrderedMap();
        this.pages.add(page);
        this.currPage = page;
    }

    private Node insert(Node node, Rectangle rect) {
        if (node.leaveName == null && node.leftChild != null && node.rightChild != null) {
            Node newNode = null;
            newNode = this.insert(node.leftChild, rect);
            if (newNode == null) {
                newNode = this.insert(node.rightChild, rect);
            }
            return newNode;
        }
        if (node.leaveName != null) {
            return null;
        }
        if (node.rect.width == rect.width && node.rect.height == rect.height) {
            return node;
        }
        if (node.rect.width < rect.width || node.rect.height < rect.height) {
            return null;
        }
        node.leftChild = new Node();
        node.rightChild = new Node();
        int deltaWidth = (int)node.rect.width - (int)rect.width;
        int deltaHeight = (int)node.rect.height - (int)rect.height;
        if (deltaWidth > deltaHeight) {
            node.leftChild.rect.x = node.rect.x;
            node.leftChild.rect.y = node.rect.y;
            node.leftChild.rect.width = rect.width;
            node.leftChild.rect.height = node.rect.height;
            node.rightChild.rect.x = node.rect.x + rect.width;
            node.rightChild.rect.y = node.rect.y;
            node.rightChild.rect.width = node.rect.width - rect.width;
            node.rightChild.rect.height = node.rect.height;
        } else {
            node.leftChild.rect.x = node.rect.x;
            node.leftChild.rect.y = node.rect.y;
            node.leftChild.rect.width = node.rect.width;
            node.leftChild.rect.height = rect.height;
            node.rightChild.rect.x = node.rect.x;
            node.rightChild.rect.y = node.rect.y + rect.height;
            node.rightChild.rect.width = node.rect.width;
            node.rightChild.rect.height = node.rect.height - rect.height;
        }
        return this.insert(node.leftChild, rect);
    }

    public Array<Page> getPages() {
        return this.pages;
    }

    public synchronized Rectangle getRect(String name) {
        for (Page page : this.pages) {
            Rectangle rect = page.rects.get(name);
            if (rect == null) continue;
            return rect;
        }
        return null;
    }

    public synchronized Page getPage(String name) {
        for (Page page : this.pages) {
            Rectangle rect = page.rects.get(name);
            if (rect == null) continue;
            return page;
        }
        return null;
    }

    public synchronized int getPageIndex(String name) {
        for (int i = 0; i < this.pages.size; ++i) {
            Rectangle rect = this.pages.get((int)i).rects.get(name);
            if (rect == null) continue;
            return i;
        }
        return -1;
    }

    @Override
    public synchronized void dispose() {
        for (Page page : this.pages) {
            page.image.dispose();
        }
        this.disposed = true;
    }

    public synchronized TextureAtlas generateTextureAtlas(Texture.TextureFilter minFilter, Texture.TextureFilter magFilter, boolean useMipMaps) {
        TextureAtlas atlas = new TextureAtlas();
        for (Page page : this.pages) {
            if (page.rects.size == 0) continue;
            Texture texture = new Texture(new PixmapTextureData(page.image, page.image.getFormat(), useMipMaps, false, true)){

                @Override
                public void dispose() {
                    super.dispose();
                    this.getTextureData().consumePixmap().dispose();
                }
            };
            texture.setFilter(minFilter, magFilter);
            ObjectMap.Keys<String> names = page.rects.keys();
            for (String name : names) {
                Rectangle rect = page.rects.get(name);
                TextureRegion region = new TextureRegion(texture, (int)rect.x, (int)rect.y, (int)rect.width, (int)rect.height);
                atlas.addRegion(name, region);
            }
        }
        return atlas;
    }

    public synchronized void updateTextureAtlas(TextureAtlas atlas, Texture.TextureFilter minFilter, Texture.TextureFilter magFilter, boolean useMipMaps) {
        for (Page page : this.pages) {
            Rectangle rect;
            TextureRegion region;
            if (page.texture == null) {
                if (page.rects.size == 0 || page.addedRects.size <= 0) continue;
                page.texture = new Texture(new PixmapTextureData(page.image, page.image.getFormat(), useMipMaps, false, true)){

                    @Override
                    public void dispose() {
                        super.dispose();
                        this.getTextureData().consumePixmap().dispose();
                    }
                };
                page.texture.setFilter(minFilter, magFilter);
                for (String name : page.addedRects) {
                    rect = page.rects.get(name);
                    region = new TextureRegion(page.texture, (int)rect.x, (int)rect.y, (int)rect.width, (int)rect.height);
                    atlas.addRegion(name, region);
                }
                page.addedRects.clear();
                continue;
            }
            if (page.addedRects.size <= 0) continue;
            page.texture.load(page.texture.getTextureData());
            for (String name : page.addedRects) {
                rect = page.rects.get(name);
                region = new TextureRegion(page.texture, (int)rect.x, (int)rect.y, (int)rect.width, (int)rect.height);
                atlas.addRegion(name, region);
            }
            page.addedRects.clear();
            return;
        }
    }

    public int getPageWidth() {
        return this.pageWidth;
    }

    public int getPageHeight() {
        return this.pageHeight;
    }

    public int getPadding() {
        return this.padding;
    }

    public boolean duplicateBorder() {
        return this.duplicateBorder;
    }

    public class Page {
        Node root;
        OrderedMap<String, Rectangle> rects;
        Pixmap image;
        Texture texture;
        final Array<String> addedRects;

        public Page() {
            this.addedRects = new Array();
        }

        public Pixmap getPixmap() {
            return this.image;
        }

        public OrderedMap<String, Rectangle> getRects() {
            return this.rects;
        }
    }

    static final class Node {
        public Node leftChild;
        public Node rightChild;
        public Rectangle rect;
        public String leaveName;

        public Node(int x, int y, int width, int height, Node leftChild, Node rightChild, String leaveName) {
            this.rect = new Rectangle(x, y, width, height);
            this.leftChild = leftChild;
            this.rightChild = rightChild;
            this.leaveName = leaveName;
        }

        public Node() {
            this.rect = new Rectangle();
        }
    }

}

