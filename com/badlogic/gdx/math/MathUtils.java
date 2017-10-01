/*
 * Decompiled with CFR 0_122.
 */
package com.badlogic.gdx.math;

import com.badlogic.gdx.math.RandomXS128;
import java.util.Random;

public final class MathUtils {
    public static final float nanoToSec = 1.0E-9f;
    public static final float FLOAT_ROUNDING_ERROR = 1.0E-6f;
    public static final float PI = 3.1415927f;
    public static final float PI2 = 6.2831855f;
    public static final float E = 2.7182817f;
    private static final int SIN_BITS = 14;
    private static final int SIN_MASK = 16383;
    private static final int SIN_COUNT = 16384;
    private static final float radFull = 6.2831855f;
    private static final float degFull = 360.0f;
    private static final float radToIndex = 2607.5945f;
    private static final float degToIndex = 45.511112f;
    public static final float radiansToDegrees = 57.295776f;
    public static final float radDeg = 57.295776f;
    public static final float degreesToRadians = 0.017453292f;
    public static final float degRad = 0.017453292f;
    private static final int ATAN2_BITS = 7;
    private static final int ATAN2_BITS2 = 14;
    private static final int ATAN2_MASK = 16383;
    private static final int ATAN2_COUNT = 16384;
    static final int ATAN2_DIM = (int)Math.sqrt(16384.0);
    private static final float INV_ATAN2_DIM_MINUS_1 = 1.0f / (float)(ATAN2_DIM - 1);
    public static Random random = new RandomXS128();
    private static final int BIG_ENOUGH_INT = 16384;
    private static final double BIG_ENOUGH_FLOOR = 16384.0;
    private static final double CEIL = 0.9999999;
    private static final double BIG_ENOUGH_CEIL = 16384.999999999996;
    private static final double BIG_ENOUGH_ROUND = 16384.5;

    public static float sin(float radians) {
        return Sin.table[(int)(radians * 2607.5945f) & 16383];
    }

    public static float cos(float radians) {
        return Sin.table[(int)((radians + 1.5707964f) * 2607.5945f) & 16383];
    }

    public static float sinDeg(float degrees) {
        return Sin.table[(int)(degrees * 45.511112f) & 16383];
    }

    public static float cosDeg(float degrees) {
        return Sin.table[(int)((degrees + 90.0f) * 45.511112f) & 16383];
    }

    public static float atan2(float y, float x) {
        float mul;
        float add;
        if (x < 0.0f) {
            if (y < 0.0f) {
                y = - y;
                mul = 1.0f;
            } else {
                mul = -1.0f;
            }
            x = - x;
            add = -3.1415927f;
        } else {
            if (y < 0.0f) {
                y = - y;
                mul = -1.0f;
            } else {
                mul = 1.0f;
            }
            add = 0.0f;
        }
        float invDiv = 1.0f / ((x < y ? y : x) * INV_ATAN2_DIM_MINUS_1);
        if (invDiv == Float.POSITIVE_INFINITY) {
            return ((float)Math.atan2(y, x) + add) * mul;
        }
        int xi = (int)(x * invDiv);
        int yi = (int)(y * invDiv);
        return (Atan2.table[yi * ATAN2_DIM + xi] + add) * mul;
    }

    public static int random(int range) {
        return random.nextInt(range + 1);
    }

    public static int random(int start, int end) {
        return start + random.nextInt(end - start + 1);
    }

    public static long random(long range) {
        return (long)(random.nextDouble() * (double)range);
    }

    public static long random(long start, long end) {
        return start + (long)(random.nextDouble() * (double)(end - start));
    }

    public static boolean randomBoolean() {
        return random.nextBoolean();
    }

    public static boolean randomBoolean(float chance) {
        return MathUtils.random() < chance;
    }

    public static float random() {
        return random.nextFloat();
    }

    public static float random(float range) {
        return random.nextFloat() * range;
    }

    public static float random(float start, float end) {
        return start + random.nextFloat() * (end - start);
    }

    public static int randomSign() {
        return 1 | random.nextInt() >> 31;
    }

    public static float randomTriangular() {
        return random.nextFloat() - random.nextFloat();
    }

    public static float randomTriangular(float max) {
        return (random.nextFloat() - random.nextFloat()) * max;
    }

    public static float randomTriangular(float min, float max) {
        return MathUtils.randomTriangular(min, max, min + (max - min) * 0.5f);
    }

    public static float randomTriangular(float min, float max, float mode) {
        float d;
        float u = random.nextFloat();
        if (u <= (mode - min) / (d = max - min)) {
            return min + (float)Math.sqrt(u * d * (mode - min));
        }
        return max - (float)Math.sqrt((1.0f - u) * d * (max - mode));
    }

    public static int nextPowerOfTwo(int value) {
        if (value == 0) {
            return 1;
        }
        --value;
        value |= value >> 1;
        value |= value >> 2;
        value |= value >> 4;
        value |= value >> 8;
        value |= value >> 16;
        return value + 1;
    }

    public static boolean isPowerOfTwo(int value) {
        return value != 0 && (value & value - 1) == 0;
    }

    public static short clamp(short value, short min, short max) {
        if (value < min) {
            return min;
        }
        if (value > max) {
            return max;
        }
        return value;
    }

    public static int clamp(int value, int min, int max) {
        if (value < min) {
            return min;
        }
        if (value > max) {
            return max;
        }
        return value;
    }

    public static long clamp(long value, long min, long max) {
        if (value < min) {
            return min;
        }
        if (value > max) {
            return max;
        }
        return value;
    }

    public static float clamp(float value, float min, float max) {
        if (value < min) {
            return min;
        }
        if (value > max) {
            return max;
        }
        return value;
    }

    public static double clamp(double value, double min, double max) {
        if (value < min) {
            return min;
        }
        if (value > max) {
            return max;
        }
        return value;
    }

    public static float lerp(float fromValue, float toValue, float progress) {
        return fromValue + (toValue - fromValue) * progress;
    }

    public static int floor(float x) {
        return (int)((double)x + 16384.0) - 16384;
    }

    public static int floorPositive(float x) {
        return (int)x;
    }

    public static int ceil(float x) {
        return (int)((double)x + 16384.999999999996) - 16384;
    }

    public static int ceilPositive(float x) {
        return (int)((double)x + 0.9999999);
    }

    public static int round(float x) {
        return (int)((double)x + 16384.5) - 16384;
    }

    public static int roundPositive(float x) {
        return (int)(x + 0.5f);
    }

    public static boolean isZero(float value) {
        return Math.abs(value) <= 1.0E-6f;
    }

    public static boolean isZero(float value, float tolerance) {
        return Math.abs(value) <= tolerance;
    }

    public static boolean isEqual(float a, float b) {
        return Math.abs(a - b) <= 1.0E-6f;
    }

    public static boolean isEqual(float a, float b, float tolerance) {
        return Math.abs(a - b) <= tolerance;
    }

    public static float log(float a, float x) {
        return (float)(Math.log(x) / Math.log(a));
    }

    public static float log2(float x) {
        return MathUtils.log(2.0f, x);
    }

    private static class Atan2 {
        static final float[] table = new float[16384];

        private Atan2() {
        }

        static {
            for (int i = 0; i < MathUtils.ATAN2_DIM; ++i) {
                for (int j = 0; j < MathUtils.ATAN2_DIM; ++j) {
                    float x0 = (float)i / (float)MathUtils.ATAN2_DIM;
                    float y0 = (float)j / (float)MathUtils.ATAN2_DIM;
                    Atan2.table[j * MathUtils.ATAN2_DIM + i] = (float)Math.atan2(y0, x0);
                }
            }
        }
    }

    private static class Sin {
        static final float[] table;

        private Sin() {
        }

        static {
            int i;
            table = new float[16384];
            for (i = 0; i < 16384; ++i) {
                Sin.table[i] = (float)Math.sin(((float)i + 0.5f) / 16384.0f * 6.2831855f);
            }
            for (i = 0; i < 360; i += 90) {
                Sin.table[(int)((float)i * 45.511112f) & 16383] = (float)Math.sin((float)i * 0.017453292f);
            }
        }
    }

}

