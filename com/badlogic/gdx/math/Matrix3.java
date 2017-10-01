/*
 * Decompiled with CFR 0_122.
 */
package com.badlogic.gdx.math;

import com.badlogic.gdx.math.Affine2;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.GdxRuntimeException;
import java.io.Serializable;

public class Matrix3
implements Serializable {
    private static final long serialVersionUID = 7907569533774959788L;
    public static final int M00 = 0;
    public static final int M01 = 3;
    public static final int M02 = 6;
    public static final int M10 = 1;
    public static final int M11 = 4;
    public static final int M12 = 7;
    public static final int M20 = 2;
    public static final int M21 = 5;
    public static final int M22 = 8;
    public float[] val = new float[9];
    private float[] tmp = new float[9];

    public Matrix3() {
        this.idt();
    }

    public Matrix3(Matrix3 matrix) {
        this.set(matrix);
    }

    public Matrix3(float[] values) {
        this.set(values);
    }

    public Matrix3 idt() {
        this.val[0] = 1.0f;
        this.val[1] = 0.0f;
        this.val[2] = 0.0f;
        this.val[3] = 0.0f;
        this.val[4] = 1.0f;
        this.val[5] = 0.0f;
        this.val[6] = 0.0f;
        this.val[7] = 0.0f;
        this.val[8] = 1.0f;
        return this;
    }

    public Matrix3 mul(Matrix3 m) {
        float v00 = this.val[0] * m.val[0] + this.val[3] * m.val[1] + this.val[6] * m.val[2];
        float v01 = this.val[0] * m.val[3] + this.val[3] * m.val[4] + this.val[6] * m.val[5];
        float v02 = this.val[0] * m.val[6] + this.val[3] * m.val[7] + this.val[6] * m.val[8];
        float v10 = this.val[1] * m.val[0] + this.val[4] * m.val[1] + this.val[7] * m.val[2];
        float v11 = this.val[1] * m.val[3] + this.val[4] * m.val[4] + this.val[7] * m.val[5];
        float v12 = this.val[1] * m.val[6] + this.val[4] * m.val[7] + this.val[7] * m.val[8];
        float v20 = this.val[2] * m.val[0] + this.val[5] * m.val[1] + this.val[8] * m.val[2];
        float v21 = this.val[2] * m.val[3] + this.val[5] * m.val[4] + this.val[8] * m.val[5];
        float v22 = this.val[2] * m.val[6] + this.val[5] * m.val[7] + this.val[8] * m.val[8];
        this.val[0] = v00;
        this.val[1] = v10;
        this.val[2] = v20;
        this.val[3] = v01;
        this.val[4] = v11;
        this.val[5] = v21;
        this.val[6] = v02;
        this.val[7] = v12;
        this.val[8] = v22;
        return this;
    }

    public Matrix3 mulLeft(Matrix3 m) {
        float v00 = m.val[0] * this.val[0] + m.val[3] * this.val[1] + m.val[6] * this.val[2];
        float v01 = m.val[0] * this.val[3] + m.val[3] * this.val[4] + m.val[6] * this.val[5];
        float v02 = m.val[0] * this.val[6] + m.val[3] * this.val[7] + m.val[6] * this.val[8];
        float v10 = m.val[1] * this.val[0] + m.val[4] * this.val[1] + m.val[7] * this.val[2];
        float v11 = m.val[1] * this.val[3] + m.val[4] * this.val[4] + m.val[7] * this.val[5];
        float v12 = m.val[1] * this.val[6] + m.val[4] * this.val[7] + m.val[7] * this.val[8];
        float v20 = m.val[2] * this.val[0] + m.val[5] * this.val[1] + m.val[8] * this.val[2];
        float v21 = m.val[2] * this.val[3] + m.val[5] * this.val[4] + m.val[8] * this.val[5];
        float v22 = m.val[2] * this.val[6] + m.val[5] * this.val[7] + m.val[8] * this.val[8];
        this.val[0] = v00;
        this.val[1] = v10;
        this.val[2] = v20;
        this.val[3] = v01;
        this.val[4] = v11;
        this.val[5] = v21;
        this.val[6] = v02;
        this.val[7] = v12;
        this.val[8] = v22;
        return this;
    }

    public Matrix3 setToRotation(float degrees) {
        return this.setToRotationRad(0.017453292f * degrees);
    }

    public Matrix3 setToRotationRad(float radians) {
        float cos = (float)Math.cos(radians);
        float sin = (float)Math.sin(radians);
        this.val[0] = cos;
        this.val[1] = sin;
        this.val[2] = 0.0f;
        this.val[3] = - sin;
        this.val[4] = cos;
        this.val[5] = 0.0f;
        this.val[6] = 0.0f;
        this.val[7] = 0.0f;
        this.val[8] = 1.0f;
        return this;
    }

    public Matrix3 setToRotation(Vector3 axis, float degrees) {
        return this.setToRotation(axis, MathUtils.cosDeg(degrees), MathUtils.sinDeg(degrees));
    }

    public Matrix3 setToRotation(Vector3 axis, float cos, float sin) {
        float oc = 1.0f - cos;
        this.val[0] = oc * axis.x * axis.x + cos;
        this.val[1] = oc * axis.x * axis.y - axis.z * sin;
        this.val[2] = oc * axis.z * axis.x + axis.y * sin;
        this.val[3] = oc * axis.x * axis.y + axis.z * sin;
        this.val[4] = oc * axis.y * axis.y + cos;
        this.val[5] = oc * axis.y * axis.z - axis.x * sin;
        this.val[6] = oc * axis.z * axis.x - axis.y * sin;
        this.val[7] = oc * axis.y * axis.z + axis.x * sin;
        this.val[8] = oc * axis.z * axis.z + cos;
        return this;
    }

    public Matrix3 setToTranslation(float x, float y) {
        this.val[0] = 1.0f;
        this.val[1] = 0.0f;
        this.val[2] = 0.0f;
        this.val[3] = 0.0f;
        this.val[4] = 1.0f;
        this.val[5] = 0.0f;
        this.val[6] = x;
        this.val[7] = y;
        this.val[8] = 1.0f;
        return this;
    }

    public Matrix3 setToTranslation(Vector2 translation) {
        this.val[0] = 1.0f;
        this.val[1] = 0.0f;
        this.val[2] = 0.0f;
        this.val[3] = 0.0f;
        this.val[4] = 1.0f;
        this.val[5] = 0.0f;
        this.val[6] = translation.x;
        this.val[7] = translation.y;
        this.val[8] = 1.0f;
        return this;
    }

    public Matrix3 setToScaling(float scaleX, float scaleY) {
        this.val[0] = scaleX;
        this.val[1] = 0.0f;
        this.val[2] = 0.0f;
        this.val[3] = 0.0f;
        this.val[4] = scaleY;
        this.val[5] = 0.0f;
        this.val[6] = 0.0f;
        this.val[7] = 0.0f;
        this.val[8] = 1.0f;
        return this;
    }

    public Matrix3 setToScaling(Vector2 scale) {
        this.val[0] = scale.x;
        this.val[1] = 0.0f;
        this.val[2] = 0.0f;
        this.val[3] = 0.0f;
        this.val[4] = scale.y;
        this.val[5] = 0.0f;
        this.val[6] = 0.0f;
        this.val[7] = 0.0f;
        this.val[8] = 1.0f;
        return this;
    }

    public String toString() {
        return "[" + this.val[0] + "|" + this.val[3] + "|" + this.val[6] + "]\n" + "[" + this.val[1] + "|" + this.val[4] + "|" + this.val[7] + "]\n" + "[" + this.val[2] + "|" + this.val[5] + "|" + this.val[8] + "]";
    }

    public float det() {
        return this.val[0] * this.val[4] * this.val[8] + this.val[3] * this.val[7] * this.val[2] + this.val[6] * this.val[1] * this.val[5] - this.val[0] * this.val[7] * this.val[5] - this.val[3] * this.val[1] * this.val[8] - this.val[6] * this.val[4] * this.val[2];
    }

    public Matrix3 inv() {
        float det = this.det();
        if (det == 0.0f) {
            throw new GdxRuntimeException("Can't invert a singular matrix");
        }
        float inv_det = 1.0f / det;
        this.tmp[0] = this.val[4] * this.val[8] - this.val[5] * this.val[7];
        this.tmp[1] = this.val[2] * this.val[7] - this.val[1] * this.val[8];
        this.tmp[2] = this.val[1] * this.val[5] - this.val[2] * this.val[4];
        this.tmp[3] = this.val[5] * this.val[6] - this.val[3] * this.val[8];
        this.tmp[4] = this.val[0] * this.val[8] - this.val[2] * this.val[6];
        this.tmp[5] = this.val[2] * this.val[3] - this.val[0] * this.val[5];
        this.tmp[6] = this.val[3] * this.val[7] - this.val[4] * this.val[6];
        this.tmp[7] = this.val[1] * this.val[6] - this.val[0] * this.val[7];
        this.tmp[8] = this.val[0] * this.val[4] - this.val[1] * this.val[3];
        this.val[0] = inv_det * this.tmp[0];
        this.val[1] = inv_det * this.tmp[1];
        this.val[2] = inv_det * this.tmp[2];
        this.val[3] = inv_det * this.tmp[3];
        this.val[4] = inv_det * this.tmp[4];
        this.val[5] = inv_det * this.tmp[5];
        this.val[6] = inv_det * this.tmp[6];
        this.val[7] = inv_det * this.tmp[7];
        this.val[8] = inv_det * this.tmp[8];
        return this;
    }

    public Matrix3 set(Matrix3 mat) {
        System.arraycopy(mat.val, 0, this.val, 0, this.val.length);
        return this;
    }

    public Matrix3 set(Affine2 affine) {
        this.val[0] = affine.m00;
        this.val[1] = affine.m10;
        this.val[2] = 0.0f;
        this.val[3] = affine.m01;
        this.val[4] = affine.m11;
        this.val[5] = 0.0f;
        this.val[6] = affine.m02;
        this.val[7] = affine.m12;
        this.val[8] = 1.0f;
        return this;
    }

    public Matrix3 set(Matrix4 mat) {
        this.val[0] = mat.val[0];
        this.val[1] = mat.val[1];
        this.val[2] = mat.val[2];
        this.val[3] = mat.val[4];
        this.val[4] = mat.val[5];
        this.val[5] = mat.val[6];
        this.val[6] = mat.val[8];
        this.val[7] = mat.val[9];
        this.val[8] = mat.val[10];
        return this;
    }

    public Matrix3 set(float[] values) {
        System.arraycopy(values, 0, this.val, 0, this.val.length);
        return this;
    }

    public Matrix3 trn(Vector2 vector) {
        float[] arrf = this.val;
        arrf[6] = arrf[6] + vector.x;
        float[] arrf2 = this.val;
        arrf2[7] = arrf2[7] + vector.y;
        return this;
    }

    public Matrix3 trn(float x, float y) {
        float[] arrf = this.val;
        arrf[6] = arrf[6] + x;
        float[] arrf2 = this.val;
        arrf2[7] = arrf2[7] + y;
        return this;
    }

    public Matrix3 trn(Vector3 vector) {
        float[] arrf = this.val;
        arrf[6] = arrf[6] + vector.x;
        float[] arrf2 = this.val;
        arrf2[7] = arrf2[7] + vector.y;
        return this;
    }

    public Matrix3 translate(float x, float y) {
        this.tmp[0] = 1.0f;
        this.tmp[1] = 0.0f;
        this.tmp[2] = 0.0f;
        this.tmp[3] = 0.0f;
        this.tmp[4] = 1.0f;
        this.tmp[5] = 0.0f;
        this.tmp[6] = x;
        this.tmp[7] = y;
        this.tmp[8] = 1.0f;
        Matrix3.mul(this.val, this.tmp);
        return this;
    }

    public Matrix3 translate(Vector2 translation) {
        this.tmp[0] = 1.0f;
        this.tmp[1] = 0.0f;
        this.tmp[2] = 0.0f;
        this.tmp[3] = 0.0f;
        this.tmp[4] = 1.0f;
        this.tmp[5] = 0.0f;
        this.tmp[6] = translation.x;
        this.tmp[7] = translation.y;
        this.tmp[8] = 1.0f;
        Matrix3.mul(this.val, this.tmp);
        return this;
    }

    public Matrix3 rotate(float degrees) {
        return this.rotateRad(0.017453292f * degrees);
    }

    public Matrix3 rotateRad(float radians) {
        if (radians == 0.0f) {
            return this;
        }
        float cos = (float)Math.cos(radians);
        float sin = (float)Math.sin(radians);
        this.tmp[0] = cos;
        this.tmp[1] = sin;
        this.tmp[2] = 0.0f;
        this.tmp[3] = - sin;
        this.tmp[4] = cos;
        this.tmp[5] = 0.0f;
        this.tmp[6] = 0.0f;
        this.tmp[7] = 0.0f;
        this.tmp[8] = 1.0f;
        Matrix3.mul(this.val, this.tmp);
        return this;
    }

    public Matrix3 scale(float scaleX, float scaleY) {
        this.tmp[0] = scaleX;
        this.tmp[1] = 0.0f;
        this.tmp[2] = 0.0f;
        this.tmp[3] = 0.0f;
        this.tmp[4] = scaleY;
        this.tmp[5] = 0.0f;
        this.tmp[6] = 0.0f;
        this.tmp[7] = 0.0f;
        this.tmp[8] = 1.0f;
        Matrix3.mul(this.val, this.tmp);
        return this;
    }

    public Matrix3 scale(Vector2 scale) {
        this.tmp[0] = scale.x;
        this.tmp[1] = 0.0f;
        this.tmp[2] = 0.0f;
        this.tmp[3] = 0.0f;
        this.tmp[4] = scale.y;
        this.tmp[5] = 0.0f;
        this.tmp[6] = 0.0f;
        this.tmp[7] = 0.0f;
        this.tmp[8] = 1.0f;
        Matrix3.mul(this.val, this.tmp);
        return this;
    }

    public float[] getValues() {
        return this.val;
    }

    public Vector2 getTranslation(Vector2 position) {
        position.x = this.val[6];
        position.y = this.val[7];
        return position;
    }

    public Vector2 getScale(Vector2 scale) {
        scale.x = (float)Math.sqrt(this.val[0] * this.val[0] + this.val[3] * this.val[3]);
        scale.y = (float)Math.sqrt(this.val[1] * this.val[1] + this.val[4] * this.val[4]);
        return scale;
    }

    public float getRotation() {
        return 57.295776f * (float)Math.atan2(this.val[1], this.val[0]);
    }

    public float getRotationRad() {
        return (float)Math.atan2(this.val[1], this.val[0]);
    }

    public Matrix3 scl(float scale) {
        float[] arrf = this.val;
        arrf[0] = arrf[0] * scale;
        float[] arrf2 = this.val;
        arrf2[4] = arrf2[4] * scale;
        return this;
    }

    public Matrix3 scl(Vector2 scale) {
        float[] arrf = this.val;
        arrf[0] = arrf[0] * scale.x;
        float[] arrf2 = this.val;
        arrf2[4] = arrf2[4] * scale.y;
        return this;
    }

    public Matrix3 scl(Vector3 scale) {
        float[] arrf = this.val;
        arrf[0] = arrf[0] * scale.x;
        float[] arrf2 = this.val;
        arrf2[4] = arrf2[4] * scale.y;
        return this;
    }

    public Matrix3 transpose() {
        float v01 = this.val[1];
        float v02 = this.val[2];
        float v10 = this.val[3];
        float v12 = this.val[5];
        float v20 = this.val[6];
        float v21 = this.val[7];
        this.val[3] = v01;
        this.val[6] = v02;
        this.val[1] = v10;
        this.val[7] = v12;
        this.val[2] = v20;
        this.val[5] = v21;
        return this;
    }

    private static void mul(float[] mata, float[] matb) {
        float v00 = mata[0] * matb[0] + mata[3] * matb[1] + mata[6] * matb[2];
        float v01 = mata[0] * matb[3] + mata[3] * matb[4] + mata[6] * matb[5];
        float v02 = mata[0] * matb[6] + mata[3] * matb[7] + mata[6] * matb[8];
        float v10 = mata[1] * matb[0] + mata[4] * matb[1] + mata[7] * matb[2];
        float v11 = mata[1] * matb[3] + mata[4] * matb[4] + mata[7] * matb[5];
        float v12 = mata[1] * matb[6] + mata[4] * matb[7] + mata[7] * matb[8];
        float v20 = mata[2] * matb[0] + mata[5] * matb[1] + mata[8] * matb[2];
        float v21 = mata[2] * matb[3] + mata[5] * matb[4] + mata[8] * matb[5];
        float v22 = mata[2] * matb[6] + mata[5] * matb[7] + mata[8] * matb[8];
        mata[0] = v00;
        mata[1] = v10;
        mata[2] = v20;
        mata[3] = v01;
        mata[4] = v11;
        mata[5] = v21;
        mata[6] = v02;
        mata[7] = v12;
        mata[8] = v22;
    }
}

