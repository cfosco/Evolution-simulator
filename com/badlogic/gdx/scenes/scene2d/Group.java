/*
 * Decompiled with CFR 0_122.
 */
package com.badlogic.gdx.scenes.scene2d;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Affine2;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.utils.Cullable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.SnapshotArray;
import java.io.PrintStream;

public class Group
extends Actor
implements Cullable {
    private static final Vector2 tmp = new Vector2();
    final SnapshotArray<Actor> children = new SnapshotArray(true, 4, Actor.class);
    private final Affine2 worldTransform = new Affine2();
    private final Matrix4 computedTransform = new Matrix4();
    private final Matrix4 oldTransform = new Matrix4();
    boolean transform = true;
    private Rectangle cullingArea;

    @Override
    public void act(float delta) {
        super.act(delta);
        Actor[] actors = this.children.begin();
        int n = this.children.size;
        for (int i = 0; i < n; ++i) {
            actors[i].act(delta);
        }
        this.children.end();
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (this.transform) {
            this.applyTransform(batch, this.computeTransform());
        }
        this.drawChildren(batch, parentAlpha);
        if (this.transform) {
            this.resetTransform(batch);
        }
    }

    protected void drawChildren(Batch batch, float parentAlpha) {
        parentAlpha *= this.color.a;
        SnapshotArray<Actor> children = this.children;
        Actor[] actors = children.begin();
        Rectangle cullingArea = this.cullingArea;
        if (cullingArea != null) {
            float cullLeft = cullingArea.x;
            float cullRight = cullLeft + cullingArea.width;
            float cullBottom = cullingArea.y;
            float cullTop = cullBottom + cullingArea.height;
            if (this.transform) {
                int n = children.size;
                for (int i = 0; i < n; ++i) {
                    Actor child = actors[i];
                    if (!child.isVisible()) continue;
                    float cx = child.x;
                    float cy = child.y;
                    if (cx > cullRight || cy > cullTop || cx + child.width < cullLeft || cy + child.height < cullBottom) continue;
                    child.draw(batch, parentAlpha);
                }
            } else {
                float offsetX = this.x;
                float offsetY = this.y;
                this.x = 0.0f;
                this.y = 0.0f;
                int n = children.size;
                for (int i = 0; i < n; ++i) {
                    Actor child = actors[i];
                    if (!child.isVisible()) continue;
                    float cx = child.x;
                    float cy = child.y;
                    if (cx > cullRight || cy > cullTop || cx + child.width < cullLeft || cy + child.height < cullBottom) continue;
                    child.x = cx + offsetX;
                    child.y = cy + offsetY;
                    child.draw(batch, parentAlpha);
                    child.x = cx;
                    child.y = cy;
                }
                this.x = offsetX;
                this.y = offsetY;
            }
        } else if (this.transform) {
            int n = children.size;
            for (int i = 0; i < n; ++i) {
                Actor child = actors[i];
                if (!child.isVisible()) continue;
                child.draw(batch, parentAlpha);
            }
        } else {
            float offsetX = this.x;
            float offsetY = this.y;
            this.x = 0.0f;
            this.y = 0.0f;
            int n = children.size;
            for (int i = 0; i < n; ++i) {
                Actor child = actors[i];
                if (!child.isVisible()) continue;
                float cx = child.x;
                float cy = child.y;
                child.x = cx + offsetX;
                child.y = cy + offsetY;
                child.draw(batch, parentAlpha);
                child.x = cx;
                child.y = cy;
            }
            this.x = offsetX;
            this.y = offsetY;
        }
        children.end();
    }

    @Override
    public void drawDebug(ShapeRenderer shapes) {
        this.drawDebugBounds(shapes);
        if (this.transform) {
            this.applyTransform(shapes, this.computeTransform());
        }
        this.drawDebugChildren(shapes);
        if (this.transform) {
            this.resetTransform(shapes);
        }
    }

    protected void drawDebugChildren(ShapeRenderer shapes) {
        SnapshotArray<Actor> children = this.children;
        Actor[] actors = children.begin();
        if (this.transform) {
            int n = children.size;
            for (int i = 0; i < n; ++i) {
                Actor child = actors[i];
                if (!child.isVisible()) continue;
                child.drawDebug(shapes);
            }
            shapes.flush();
        } else {
            float offsetX = this.x;
            float offsetY = this.y;
            this.x = 0.0f;
            this.y = 0.0f;
            int n = children.size;
            for (int i = 0; i < n; ++i) {
                Actor child = actors[i];
                if (!child.isVisible()) continue;
                float cx = child.x;
                float cy = child.y;
                child.x = cx + offsetX;
                child.y = cy + offsetY;
                child.drawDebug(shapes);
                child.x = cx;
                child.y = cy;
            }
            this.x = offsetX;
            this.y = offsetY;
        }
        children.end();
    }

    protected Matrix4 computeTransform() {
        Affine2 worldTransform = this.worldTransform;
        float originX = this.originX;
        float originY = this.originY;
        float rotation = this.rotation;
        float scaleX = this.scaleX;
        float scaleY = this.scaleY;
        worldTransform.setToTrnRotScl(this.x + originX, this.y + originY, rotation, scaleX, scaleY);
        if (originX != 0.0f || originY != 0.0f) {
            worldTransform.translate(- originX, - originY);
        }
        Group parentGroup = this.parent;
        while (parentGroup != null && !parentGroup.transform) {
            parentGroup = parentGroup.parent;
        }
        if (parentGroup != null) {
            worldTransform.preMul(parentGroup.worldTransform);
        }
        this.computedTransform.set(worldTransform);
        return this.computedTransform;
    }

    protected void applyTransform(Batch batch, Matrix4 transform) {
        this.oldTransform.set(batch.getTransformMatrix());
        batch.setTransformMatrix(transform);
    }

    protected void resetTransform(Batch batch) {
        batch.setTransformMatrix(this.oldTransform);
    }

    protected void applyTransform(ShapeRenderer shapes, Matrix4 transform) {
        this.oldTransform.set(shapes.getTransformMatrix());
        shapes.setTransformMatrix(transform);
    }

    protected void resetTransform(ShapeRenderer shapes) {
        shapes.setTransformMatrix(this.oldTransform);
    }

    @Override
    public void setCullingArea(Rectangle cullingArea) {
        this.cullingArea = cullingArea;
    }

    @Override
    public Actor hit(float x, float y, boolean touchable) {
        if (touchable && this.getTouchable() == Touchable.disabled) {
            return null;
        }
        Vector2 point = tmp;
        Actor[] childrenArray = (Actor[])this.children.items;
        for (int i = this.children.size - 1; i >= 0; --i) {
            Actor child = childrenArray[i];
            if (!child.isVisible()) continue;
            child.parentToLocalCoordinates(point.set(x, y));
            Actor hit = child.hit(point.x, point.y, touchable);
            if (hit == null) continue;
            return hit;
        }
        return super.hit(x, y, touchable);
    }

    protected void childrenChanged() {
    }

    public void addActor(Actor actor) {
        actor.remove();
        this.children.add(actor);
        actor.setParent(this);
        actor.setStage(this.getStage());
        this.childrenChanged();
    }

    public void addActorAt(int index, Actor actor) {
        actor.remove();
        if (index >= this.children.size) {
            this.children.add(actor);
        } else {
            this.children.insert(index, actor);
        }
        actor.setParent(this);
        actor.setStage(this.getStage());
        this.childrenChanged();
    }

    public void addActorBefore(Actor actorBefore, Actor actor) {
        actor.remove();
        int index = this.children.indexOf(actorBefore, true);
        this.children.insert(index, actor);
        actor.setParent(this);
        actor.setStage(this.getStage());
        this.childrenChanged();
    }

    public void addActorAfter(Actor actorAfter, Actor actor) {
        actor.remove();
        int index = this.children.indexOf(actorAfter, true);
        if (index == this.children.size) {
            this.children.add(actor);
        } else {
            this.children.insert(index + 1, actor);
        }
        actor.setParent(this);
        actor.setStage(this.getStage());
        this.childrenChanged();
    }

    public boolean removeActor(Actor actor) {
        if (!this.children.removeValue(actor, true)) {
            return false;
        }
        Stage stage = this.getStage();
        if (stage != null) {
            stage.unfocus(actor);
        }
        actor.setParent(null);
        actor.setStage(null);
        this.childrenChanged();
        return true;
    }

    public void clearChildren() {
        Actor[] actors = this.children.begin();
        int n = this.children.size;
        for (int i = 0; i < n; ++i) {
            Actor child = actors[i];
            child.setStage(null);
            child.setParent(null);
        }
        this.children.end();
        this.children.clear();
        this.childrenChanged();
    }

    @Override
    public void clear() {
        super.clear();
        this.clearChildren();
    }

    public <T extends Actor> T findActor(String name) {
        int i;
        SnapshotArray<Actor> children = this.children;
        int n = children.size;
        for (i = 0; i < n; ++i) {
            if (!name.equals(children.get(i).getName())) continue;
            return (T)children.get(i);
        }
        n = children.size;
        for (i = 0; i < n; ++i) {
            T actor;
            Actor child = children.get(i);
            if (!(child instanceof Group) || (actor = ((Group)child).findActor(name)) == null) continue;
            return actor;
        }
        return null;
    }

    @Override
    protected void setStage(Stage stage) {
        super.setStage(stage);
        Actor[] childrenArray = (Actor[])this.children.items;
        int n = this.children.size;
        for (int i = 0; i < n; ++i) {
            childrenArray[i].setStage(stage);
        }
    }

    public boolean swapActor(int first, int second) {
        int maxIndex = this.children.size;
        if (first < 0 || first >= maxIndex) {
            return false;
        }
        if (second < 0 || second >= maxIndex) {
            return false;
        }
        this.children.swap(first, second);
        return true;
    }

    public boolean swapActor(Actor first, Actor second) {
        int firstIndex = this.children.indexOf(first, true);
        int secondIndex = this.children.indexOf(second, true);
        if (firstIndex == -1 || secondIndex == -1) {
            return false;
        }
        this.children.swap(firstIndex, secondIndex);
        return true;
    }

    public SnapshotArray<Actor> getChildren() {
        return this.children;
    }

    public boolean hasChildren() {
        return this.children.size > 0;
    }

    public void setTransform(boolean transform) {
        this.transform = transform;
    }

    public boolean isTransform() {
        return this.transform;
    }

    public Vector2 localToDescendantCoordinates(Actor descendant, Vector2 localCoords) {
        Group parent = descendant.parent;
        if (parent == null) {
            throw new IllegalArgumentException("Child is not a descendant: " + descendant);
        }
        if (parent != this) {
            this.localToDescendantCoordinates(parent, localCoords);
        }
        descendant.parentToLocalCoordinates(localCoords);
        return localCoords;
    }

    public void setDebug(boolean enabled, boolean recursively) {
        this.setDebug(enabled);
        if (recursively) {
            for (Actor child : this.children) {
                if (child instanceof Group) {
                    ((Group)child).setDebug(enabled, recursively);
                    continue;
                }
                child.setDebug(enabled);
            }
        }
    }

    public Group debugAll() {
        this.setDebug(true, true);
        return this;
    }

    public void print() {
        this.print("");
    }

    private void print(String indent) {
        Actor[] actors = this.children.begin();
        int n = this.children.size;
        for (int i = 0; i < n; ++i) {
            System.out.println(indent + actors[i]);
            if (!(actors[i] instanceof Group)) continue;
            ((Group)actors[i]).print(indent + "|  ");
        }
        this.children.end();
    }
}

