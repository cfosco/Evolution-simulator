/*
 * Decompiled with CFR 0_122.
 */
package com.badlogic.gdx.math;

public class GridPoint2 {
    public int x;
    public int y;

    public GridPoint2() {
    }

    public GridPoint2(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public GridPoint2(GridPoint2 point) {
        this.x = point.x;
        this.y = point.y;
    }

    public GridPoint2 set(GridPoint2 point) {
        this.x = point.x;
        this.y = point.y;
        return this;
    }

    public GridPoint2 set(int x, int y) {
        this.x = x;
        this.y = y;
        return this;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || o.getClass() != this.getClass()) {
            return false;
        }
        GridPoint2 g = (GridPoint2)o;
        return this.x == g.x && this.y == g.y;
    }

    public int hashCode() {
        int prime = 53;
        int result = 1;
        result = 53 * result + this.x;
        result = 53 * result + this.y;
        return result;
    }
}

