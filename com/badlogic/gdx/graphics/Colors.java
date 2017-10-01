/*
 * Decompiled with CFR 0_122.
 */
package com.badlogic.gdx.graphics;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.ObjectMap;

public final class Colors {
    private static final ObjectMap<String, Color> map = new ObjectMap();

    public static ObjectMap<String, Color> getColors() {
        return map;
    }

    public static Color get(String name) {
        return map.get(name);
    }

    public static Color put(String name, Color color) {
        return map.put(name, color);
    }

    public static void reset() {
        map.clear();
        map.put("CLEAR", Color.CLEAR);
        map.put("WHITE", Color.WHITE);
        map.put("BLACK", Color.BLACK);
        map.put("RED", Color.RED);
        map.put("GREEN", Color.GREEN);
        map.put("BLUE", Color.BLUE);
        map.put("LIGHT_GRAY", Color.LIGHT_GRAY);
        map.put("GRAY", Color.GRAY);
        map.put("DARK_GRAY", Color.DARK_GRAY);
        map.put("PINK", Color.PINK);
        map.put("ORANGE", Color.ORANGE);
        map.put("YELLOW", Color.YELLOW);
        map.put("MAGENTA", Color.MAGENTA);
        map.put("CYAN", Color.CYAN);
        map.put("OLIVE", Color.OLIVE);
        map.put("PURPLE", Color.PURPLE);
        map.put("MAROON", Color.MAROON);
        map.put("TEAL", Color.TEAL);
        map.put("NAVY", Color.NAVY);
    }

    private Colors() {
    }

    static {
        Colors.reset();
    }
}

