/*
 * Decompiled with CFR 0_122.
 */
package com.badlogic.gdx.math;

public class GridPoint3 {
    public int x;
    public int y;
    public int z;

    public GridPoint3() {
    }

    public GridPoint3(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public GridPoint3(GridPoint3 point) {
        this.x = point.x;
        this.y = point.y;
        this.z = point.z;
    }

    public GridPoint3 set(GridPoint3 point) {
        this.x = point.x;
        this.y = point.y;
        this.z = point.z;
        return this;
    }

    public GridPoint3 set(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
        return this;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || o.getClass() != this.getClass()) {
            return false;
        }
        GridPoint3 g = (GridPoint3)o;
        return this.x == g.x && this.y == g.y && this.z == g.z;
    }

    public int hashCode() {
        int prime = 17;
        int result = 1;
        result = 17 * result + this.x;
        result = 17 * result + this.y;
        result = 17 * result + this.z;
        return result;
    }
}

