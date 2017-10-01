/*
 * Decompiled with CFR 0_122.
 */
package javazoom.jl.decoder;

import java.io.IOException;
import java.io.InputStream;
import java.io.InvalidClassException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.lang.reflect.Array;
import javazoom.jl.decoder.OutputBuffer;

public final class SynthesisFilter {
    private float[] v1;
    private float[] v2;
    private float[] actual_v;
    private int actual_write_pos;
    private float[] samples;
    private int channel;
    private float scalefactor;
    private float[] _tmpOut = new float[32];
    private static final double MY_PI = 3.141592653589793;
    private static final float cos1_64 = (float)(1.0 / (2.0 * Math.cos(0.04908738521234052)));
    private static final float cos3_64 = (float)(1.0 / (2.0 * Math.cos(0.14726215563702155)));
    private static final float cos5_64 = (float)(1.0 / (2.0 * Math.cos(0.2454369260617026)));
    private static final float cos7_64 = (float)(1.0 / (2.0 * Math.cos(0.3436116964863836)));
    private static final float cos9_64 = (float)(1.0 / (2.0 * Math.cos(0.44178646691106466)));
    private static final float cos11_64 = (float)(1.0 / (2.0 * Math.cos(0.5399612373357456)));
    private static final float cos13_64 = (float)(1.0 / (2.0 * Math.cos(0.6381360077604268)));
    private static final float cos15_64 = (float)(1.0 / (2.0 * Math.cos(0.7363107781851077)));
    private static final float cos17_64 = (float)(1.0 / (2.0 * Math.cos(0.8344855486097889)));
    private static final float cos19_64 = (float)(1.0 / (2.0 * Math.cos(0.9326603190344698)));
    private static final float cos21_64 = (float)(1.0 / (2.0 * Math.cos(1.030835089459151)));
    private static final float cos23_64 = (float)(1.0 / (2.0 * Math.cos(1.1290098598838318)));
    private static final float cos25_64 = (float)(1.0 / (2.0 * Math.cos(1.227184630308513)));
    private static final float cos27_64 = (float)(1.0 / (2.0 * Math.cos(1.325359400733194)));
    private static final float cos29_64 = (float)(1.0 / (2.0 * Math.cos(1.423534171157875)));
    private static final float cos31_64 = (float)(1.0 / (2.0 * Math.cos(1.521708941582556)));
    private static final float cos1_32 = (float)(1.0 / (2.0 * Math.cos(0.09817477042468103)));
    private static final float cos3_32 = (float)(1.0 / (2.0 * Math.cos(0.2945243112740431)));
    private static final float cos5_32 = (float)(1.0 / (2.0 * Math.cos(0.4908738521234052)));
    private static final float cos7_32 = (float)(1.0 / (2.0 * Math.cos(0.6872233929727672)));
    private static final float cos9_32 = (float)(1.0 / (2.0 * Math.cos(0.8835729338221293)));
    private static final float cos11_32 = (float)(1.0 / (2.0 * Math.cos(1.0799224746714913)));
    private static final float cos13_32 = (float)(1.0 / (2.0 * Math.cos(1.2762720155208536)));
    private static final float cos15_32 = (float)(1.0 / (2.0 * Math.cos(1.4726215563702154)));
    private static final float cos1_16 = (float)(1.0 / (2.0 * Math.cos(0.19634954084936207)));
    private static final float cos3_16 = (float)(1.0 / (2.0 * Math.cos(0.5890486225480862)));
    private static final float cos5_16 = (float)(1.0 / (2.0 * Math.cos(0.9817477042468103)));
    private static final float cos7_16 = (float)(1.0 / (2.0 * Math.cos(1.3744467859455345)));
    private static final float cos1_8 = (float)(1.0 / (2.0 * Math.cos(0.39269908169872414)));
    private static final float cos3_8 = (float)(1.0 / (2.0 * Math.cos(1.1780972450961724)));
    private static final float cos1_4 = (float)(1.0 / (2.0 * Math.cos(0.7853981633974483)));
    private static float[] d = null;
    private static float[][] d16 = null;

    public SynthesisFilter(int channelnumber, float factor, float[] eq0) {
        if (d == null) {
            d = SynthesisFilter.load_d();
            d16 = SynthesisFilter.splitArray(d, 16);
        }
        this.v1 = new float[512];
        this.v2 = new float[512];
        this.samples = new float[32];
        this.channel = channelnumber;
        this.scalefactor = factor;
        this.reset();
    }

    public void reset() {
        for (int p = 0; p < 512; ++p) {
            this.v2[p] = 0.0f;
            this.v1[p] = 0.0f;
        }
        for (int p2 = 0; p2 < 32; ++p2) {
            this.samples[p2] = 0.0f;
        }
        this.actual_v = this.v1;
        this.actual_write_pos = 15;
    }

    public void input_sample(float sample, int subbandnumber) {
        this.samples[subbandnumber] = sample;
    }

    public void input_samples(float[] s) {
        for (int i = 31; i >= 0; --i) {
            this.samples[i] = s[i];
        }
    }

    private void compute_new_v() {
        float[] s = this.samples;
        float s0 = s[0];
        float s1 = s[1];
        float s2 = s[2];
        float s3 = s[3];
        float s4 = s[4];
        float s5 = s[5];
        float s6 = s[6];
        float s7 = s[7];
        float s8 = s[8];
        float s9 = s[9];
        float s10 = s[10];
        float s11 = s[11];
        float s12 = s[12];
        float s13 = s[13];
        float s14 = s[14];
        float s15 = s[15];
        float s16 = s[16];
        float s17 = s[17];
        float s18 = s[18];
        float s19 = s[19];
        float s20 = s[20];
        float s21 = s[21];
        float s22 = s[22];
        float s23 = s[23];
        float s24 = s[24];
        float s25 = s[25];
        float s26 = s[26];
        float s27 = s[27];
        float s28 = s[28];
        float s29 = s[29];
        float s30 = s[30];
        float s31 = s[31];
        float p0 = s0 + s31;
        float p1 = s1 + s30;
        float p2 = s2 + s29;
        float p3 = s3 + s28;
        float p4 = s4 + s27;
        float p5 = s5 + s26;
        float p6 = s6 + s25;
        float p7 = s7 + s24;
        float p8 = s8 + s23;
        float p9 = s9 + s22;
        float p10 = s10 + s21;
        float p11 = s11 + s20;
        float p12 = s12 + s19;
        float p13 = s13 + s18;
        float p14 = s14 + s17;
        float p15 = s15 + s16;
        float pp0 = p0 + p15;
        float pp1 = p1 + p14;
        float pp2 = p2 + p13;
        float pp3 = p3 + p12;
        float pp4 = p4 + p11;
        float pp5 = p5 + p10;
        float pp6 = p6 + p9;
        float pp7 = p7 + p8;
        float pp8 = (p0 - p15) * cos1_32;
        float pp9 = (p1 - p14) * cos3_32;
        float pp10 = (p2 - p13) * cos5_32;
        float pp11 = (p3 - p12) * cos7_32;
        float pp12 = (p4 - p11) * cos9_32;
        float pp13 = (p5 - p10) * cos11_32;
        float pp14 = (p6 - p9) * cos13_32;
        float pp15 = (p7 - p8) * cos15_32;
        p0 = pp0 + pp7;
        p1 = pp1 + pp6;
        p2 = pp2 + pp5;
        p3 = pp3 + pp4;
        p4 = (pp0 - pp7) * cos1_16;
        p5 = (pp1 - pp6) * cos3_16;
        p6 = (pp2 - pp5) * cos5_16;
        p7 = (pp3 - pp4) * cos7_16;
        p8 = pp8 + pp15;
        p9 = pp9 + pp14;
        p10 = pp10 + pp13;
        p11 = pp11 + pp12;
        p12 = (pp8 - pp15) * cos1_16;
        p13 = (pp9 - pp14) * cos3_16;
        p14 = (pp10 - pp13) * cos5_16;
        p15 = (pp11 - pp12) * cos7_16;
        pp0 = p0 + p3;
        pp1 = p1 + p2;
        pp2 = (p0 - p3) * cos1_8;
        pp3 = (p1 - p2) * cos3_8;
        pp4 = p4 + p7;
        pp5 = p5 + p6;
        pp6 = (p4 - p7) * cos1_8;
        pp7 = (p5 - p6) * cos3_8;
        pp8 = p8 + p11;
        pp9 = p9 + p10;
        pp10 = (p8 - p11) * cos1_8;
        pp11 = (p9 - p10) * cos3_8;
        pp12 = p12 + p15;
        pp13 = p13 + p14;
        pp14 = (p12 - p15) * cos1_8;
        pp15 = (p13 - p14) * cos3_8;
        p0 = pp0 + pp1;
        p1 = (pp0 - pp1) * cos1_4;
        p2 = pp2 + pp3;
        p3 = (pp2 - pp3) * cos1_4;
        p4 = pp4 + pp5;
        p5 = (pp4 - pp5) * cos1_4;
        p6 = pp6 + pp7;
        p7 = (pp6 - pp7) * cos1_4;
        p8 = pp8 + pp9;
        p9 = (pp8 - pp9) * cos1_4;
        p10 = pp10 + pp11;
        p11 = (pp10 - pp11) * cos1_4;
        p12 = pp12 + pp13;
        p13 = (pp12 - pp13) * cos1_4;
        p14 = pp14 + pp15;
        p15 = (pp14 - pp15) * cos1_4;
        float new_v12 = p7;
        float new_v4 = new_v12 + p5;
        float new_v19 = - new_v4 - p6;
        float new_v27 = - p6 - p7 - p4;
        float new_v14 = p15;
        float new_v10 = new_v14 + p11;
        float new_v6 = new_v10 + p13;
        float new_v2 = p15 + p13 + p9;
        float new_v17 = - new_v2 - p14;
        float tmp1 = - p14 - p15 - p10 - p11;
        float new_v21 = tmp1 - p13;
        float new_v29 = - p14 - p15 - p12 - p8;
        float new_v25 = tmp1 - p12;
        float new_v31 = - p0;
        float new_v0 = p1;
        float new_v8 = p3;
        float new_v23 = - new_v8 - p2;
        p0 = (s0 - s31) * cos1_64;
        p1 = (s1 - s30) * cos3_64;
        p2 = (s2 - s29) * cos5_64;
        p3 = (s3 - s28) * cos7_64;
        p4 = (s4 - s27) * cos9_64;
        p5 = (s5 - s26) * cos11_64;
        p6 = (s6 - s25) * cos13_64;
        p7 = (s7 - s24) * cos15_64;
        p8 = (s8 - s23) * cos17_64;
        p9 = (s9 - s22) * cos19_64;
        p10 = (s10 - s21) * cos21_64;
        p11 = (s11 - s20) * cos23_64;
        p12 = (s12 - s19) * cos25_64;
        p13 = (s13 - s18) * cos27_64;
        p14 = (s14 - s17) * cos29_64;
        p15 = (s15 - s16) * cos31_64;
        pp0 = p0 + p15;
        pp1 = p1 + p14;
        pp2 = p2 + p13;
        pp3 = p3 + p12;
        pp4 = p4 + p11;
        pp5 = p5 + p10;
        pp6 = p6 + p9;
        pp7 = p7 + p8;
        pp8 = (p0 - p15) * cos1_32;
        pp9 = (p1 - p14) * cos3_32;
        pp10 = (p2 - p13) * cos5_32;
        pp11 = (p3 - p12) * cos7_32;
        pp12 = (p4 - p11) * cos9_32;
        pp13 = (p5 - p10) * cos11_32;
        pp14 = (p6 - p9) * cos13_32;
        pp15 = (p7 - p8) * cos15_32;
        p0 = pp0 + pp7;
        p1 = pp1 + pp6;
        p2 = pp2 + pp5;
        p3 = pp3 + pp4;
        p4 = (pp0 - pp7) * cos1_16;
        p5 = (pp1 - pp6) * cos3_16;
        p6 = (pp2 - pp5) * cos5_16;
        p7 = (pp3 - pp4) * cos7_16;
        p8 = pp8 + pp15;
        p9 = pp9 + pp14;
        p10 = pp10 + pp13;
        p11 = pp11 + pp12;
        p12 = (pp8 - pp15) * cos1_16;
        p13 = (pp9 - pp14) * cos3_16;
        p14 = (pp10 - pp13) * cos5_16;
        p15 = (pp11 - pp12) * cos7_16;
        pp0 = p0 + p3;
        pp1 = p1 + p2;
        pp2 = (p0 - p3) * cos1_8;
        pp3 = (p1 - p2) * cos3_8;
        pp4 = p4 + p7;
        pp5 = p5 + p6;
        pp6 = (p4 - p7) * cos1_8;
        pp7 = (p5 - p6) * cos3_8;
        pp8 = p8 + p11;
        pp9 = p9 + p10;
        pp10 = (p8 - p11) * cos1_8;
        pp11 = (p9 - p10) * cos3_8;
        pp12 = p12 + p15;
        pp13 = p13 + p14;
        pp14 = (p12 - p15) * cos1_8;
        pp15 = (p13 - p14) * cos3_8;
        p0 = pp0 + pp1;
        p1 = (pp0 - pp1) * cos1_4;
        p2 = pp2 + pp3;
        p3 = (pp2 - pp3) * cos1_4;
        p4 = pp4 + pp5;
        p5 = (pp4 - pp5) * cos1_4;
        p6 = pp6 + pp7;
        p7 = (pp6 - pp7) * cos1_4;
        p8 = pp8 + pp9;
        p9 = (pp8 - pp9) * cos1_4;
        p10 = pp10 + pp11;
        p11 = (pp10 - pp11) * cos1_4;
        p12 = pp12 + pp13;
        p13 = (pp12 - pp13) * cos1_4;
        p14 = pp14 + pp15;
        float new_v15 = p15 = (pp14 - pp15) * cos1_4;
        float new_v13 = new_v15 + p7;
        float new_v11 = new_v13 + p11;
        float new_v5 = new_v11 + p5 + p13;
        float new_v9 = p15 + p11 + p3;
        float new_v7 = new_v9 + p13;
        tmp1 = p13 + p15 + p9;
        float new_v1 = tmp1 + p1;
        float new_v16 = - new_v1 - p14;
        float new_v3 = tmp1 + p5 + p7;
        float new_v18 = - new_v3 - p6 - p14;
        tmp1 = - p10 - p11 - p14 - p15;
        float new_v22 = tmp1 - p13 - p2 - p3;
        float new_v20 = tmp1 - p13 - p5 - p6 - p7;
        float new_v24 = tmp1 - p12 - p2 - p3;
        float tmp2 = p4 + p6 + p7;
        float new_v26 = tmp1 - p12 - tmp2;
        tmp1 = - p8 - p12 - p14 - p15;
        float new_v30 = tmp1 - p0;
        float new_v28 = tmp1 - tmp2;
        float[] dest = this.actual_v;
        int pos = this.actual_write_pos;
        dest[0 + pos] = new_v0;
        dest[16 + pos] = new_v1;
        dest[32 + pos] = new_v2;
        dest[48 + pos] = new_v3;
        dest[64 + pos] = new_v4;
        dest[80 + pos] = new_v5;
        dest[96 + pos] = new_v6;
        dest[112 + pos] = new_v7;
        dest[128 + pos] = new_v8;
        dest[144 + pos] = new_v9;
        dest[160 + pos] = new_v10;
        dest[176 + pos] = new_v11;
        dest[192 + pos] = new_v12;
        dest[208 + pos] = new_v13;
        dest[224 + pos] = new_v14;
        dest[240 + pos] = new_v15;
        dest[256 + pos] = 0.0f;
        dest[272 + pos] = - new_v15;
        dest[288 + pos] = - new_v14;
        dest[304 + pos] = - new_v13;
        dest[320 + pos] = - new_v12;
        dest[336 + pos] = - new_v11;
        dest[352 + pos] = - new_v10;
        dest[368 + pos] = - new_v9;
        dest[384 + pos] = - new_v8;
        dest[400 + pos] = - new_v7;
        dest[416 + pos] = - new_v6;
        dest[432 + pos] = - new_v5;
        dest[448 + pos] = - new_v4;
        dest[464 + pos] = - new_v3;
        dest[480 + pos] = - new_v2;
        dest[496 + pos] = - new_v1;
        dest = this.actual_v == this.v1 ? this.v2 : this.v1;
        dest[0 + pos] = - new_v0;
        dest[16 + pos] = new_v16;
        dest[32 + pos] = new_v17;
        dest[48 + pos] = new_v18;
        dest[64 + pos] = new_v19;
        dest[80 + pos] = new_v20;
        dest[96 + pos] = new_v21;
        dest[112 + pos] = new_v22;
        dest[128 + pos] = new_v23;
        dest[144 + pos] = new_v24;
        dest[160 + pos] = new_v25;
        dest[176 + pos] = new_v26;
        dest[192 + pos] = new_v27;
        dest[208 + pos] = new_v28;
        dest[224 + pos] = new_v29;
        dest[240 + pos] = new_v30;
        dest[256 + pos] = new_v31;
        dest[272 + pos] = new_v30;
        dest[288 + pos] = new_v29;
        dest[304 + pos] = new_v28;
        dest[320 + pos] = new_v27;
        dest[336 + pos] = new_v26;
        dest[352 + pos] = new_v25;
        dest[368 + pos] = new_v24;
        dest[384 + pos] = new_v23;
        dest[400 + pos] = new_v22;
        dest[416 + pos] = new_v21;
        dest[432 + pos] = new_v20;
        dest[448 + pos] = new_v19;
        dest[464 + pos] = new_v18;
        dest[480 + pos] = new_v17;
        dest[496 + pos] = new_v16;
    }

    private void compute_pcm_samples0(OutputBuffer buffer) {
        float[] vp = this.actual_v;
        float[] tmpOut = this._tmpOut;
        int dvp = 0;
        for (int i = 0; i < 32; ++i) {
            float pcm_sample;
            float[] dp = d16[i];
            tmpOut[i] = pcm_sample = (vp[0 + dvp] * dp[0] + vp[15 + dvp] * dp[1] + vp[14 + dvp] * dp[2] + vp[13 + dvp] * dp[3] + vp[12 + dvp] * dp[4] + vp[11 + dvp] * dp[5] + vp[10 + dvp] * dp[6] + vp[9 + dvp] * dp[7] + vp[8 + dvp] * dp[8] + vp[7 + dvp] * dp[9] + vp[6 + dvp] * dp[10] + vp[5 + dvp] * dp[11] + vp[4 + dvp] * dp[12] + vp[3 + dvp] * dp[13] + vp[2 + dvp] * dp[14] + vp[1 + dvp] * dp[15]) * this.scalefactor;
            dvp += 16;
        }
    }

    private void compute_pcm_samples1(OutputBuffer buffer) {
        float[] vp = this.actual_v;
        float[] tmpOut = this._tmpOut;
        int dvp = 0;
        for (int i = 0; i < 32; ++i) {
            float pcm_sample;
            float[] dp = d16[i];
            tmpOut[i] = pcm_sample = (vp[1 + dvp] * dp[0] + vp[0 + dvp] * dp[1] + vp[15 + dvp] * dp[2] + vp[14 + dvp] * dp[3] + vp[13 + dvp] * dp[4] + vp[12 + dvp] * dp[5] + vp[11 + dvp] * dp[6] + vp[10 + dvp] * dp[7] + vp[9 + dvp] * dp[8] + vp[8 + dvp] * dp[9] + vp[7 + dvp] * dp[10] + vp[6 + dvp] * dp[11] + vp[5 + dvp] * dp[12] + vp[4 + dvp] * dp[13] + vp[3 + dvp] * dp[14] + vp[2 + dvp] * dp[15]) * this.scalefactor;
            dvp += 16;
        }
    }

    private void compute_pcm_samples2(OutputBuffer buffer) {
        float[] vp = this.actual_v;
        float[] tmpOut = this._tmpOut;
        int dvp = 0;
        for (int i = 0; i < 32; ++i) {
            float pcm_sample;
            float[] dp = d16[i];
            tmpOut[i] = pcm_sample = (vp[2 + dvp] * dp[0] + vp[1 + dvp] * dp[1] + vp[0 + dvp] * dp[2] + vp[15 + dvp] * dp[3] + vp[14 + dvp] * dp[4] + vp[13 + dvp] * dp[5] + vp[12 + dvp] * dp[6] + vp[11 + dvp] * dp[7] + vp[10 + dvp] * dp[8] + vp[9 + dvp] * dp[9] + vp[8 + dvp] * dp[10] + vp[7 + dvp] * dp[11] + vp[6 + dvp] * dp[12] + vp[5 + dvp] * dp[13] + vp[4 + dvp] * dp[14] + vp[3 + dvp] * dp[15]) * this.scalefactor;
            dvp += 16;
        }
    }

    private void compute_pcm_samples3(OutputBuffer buffer) {
        float[] vp = this.actual_v;
        float[] tmpOut = this._tmpOut;
        int dvp = 0;
        for (int i = 0; i < 32; ++i) {
            float pcm_sample;
            float[] dp = d16[i];
            tmpOut[i] = pcm_sample = (vp[3 + dvp] * dp[0] + vp[2 + dvp] * dp[1] + vp[1 + dvp] * dp[2] + vp[0 + dvp] * dp[3] + vp[15 + dvp] * dp[4] + vp[14 + dvp] * dp[5] + vp[13 + dvp] * dp[6] + vp[12 + dvp] * dp[7] + vp[11 + dvp] * dp[8] + vp[10 + dvp] * dp[9] + vp[9 + dvp] * dp[10] + vp[8 + dvp] * dp[11] + vp[7 + dvp] * dp[12] + vp[6 + dvp] * dp[13] + vp[5 + dvp] * dp[14] + vp[4 + dvp] * dp[15]) * this.scalefactor;
            dvp += 16;
        }
    }

    private void compute_pcm_samples4(OutputBuffer buffer) {
        float[] vp = this.actual_v;
        float[] tmpOut = this._tmpOut;
        int dvp = 0;
        for (int i = 0; i < 32; ++i) {
            float pcm_sample;
            float[] dp = d16[i];
            tmpOut[i] = pcm_sample = (vp[4 + dvp] * dp[0] + vp[3 + dvp] * dp[1] + vp[2 + dvp] * dp[2] + vp[1 + dvp] * dp[3] + vp[0 + dvp] * dp[4] + vp[15 + dvp] * dp[5] + vp[14 + dvp] * dp[6] + vp[13 + dvp] * dp[7] + vp[12 + dvp] * dp[8] + vp[11 + dvp] * dp[9] + vp[10 + dvp] * dp[10] + vp[9 + dvp] * dp[11] + vp[8 + dvp] * dp[12] + vp[7 + dvp] * dp[13] + vp[6 + dvp] * dp[14] + vp[5 + dvp] * dp[15]) * this.scalefactor;
            dvp += 16;
        }
    }

    private void compute_pcm_samples5(OutputBuffer buffer) {
        float[] vp = this.actual_v;
        float[] tmpOut = this._tmpOut;
        int dvp = 0;
        for (int i = 0; i < 32; ++i) {
            float pcm_sample;
            float[] dp = d16[i];
            tmpOut[i] = pcm_sample = (vp[5 + dvp] * dp[0] + vp[4 + dvp] * dp[1] + vp[3 + dvp] * dp[2] + vp[2 + dvp] * dp[3] + vp[1 + dvp] * dp[4] + vp[0 + dvp] * dp[5] + vp[15 + dvp] * dp[6] + vp[14 + dvp] * dp[7] + vp[13 + dvp] * dp[8] + vp[12 + dvp] * dp[9] + vp[11 + dvp] * dp[10] + vp[10 + dvp] * dp[11] + vp[9 + dvp] * dp[12] + vp[8 + dvp] * dp[13] + vp[7 + dvp] * dp[14] + vp[6 + dvp] * dp[15]) * this.scalefactor;
            dvp += 16;
        }
    }

    private void compute_pcm_samples6(OutputBuffer buffer) {
        float[] vp = this.actual_v;
        float[] tmpOut = this._tmpOut;
        int dvp = 0;
        for (int i = 0; i < 32; ++i) {
            float pcm_sample;
            float[] dp = d16[i];
            tmpOut[i] = pcm_sample = (vp[6 + dvp] * dp[0] + vp[5 + dvp] * dp[1] + vp[4 + dvp] * dp[2] + vp[3 + dvp] * dp[3] + vp[2 + dvp] * dp[4] + vp[1 + dvp] * dp[5] + vp[0 + dvp] * dp[6] + vp[15 + dvp] * dp[7] + vp[14 + dvp] * dp[8] + vp[13 + dvp] * dp[9] + vp[12 + dvp] * dp[10] + vp[11 + dvp] * dp[11] + vp[10 + dvp] * dp[12] + vp[9 + dvp] * dp[13] + vp[8 + dvp] * dp[14] + vp[7 + dvp] * dp[15]) * this.scalefactor;
            dvp += 16;
        }
    }

    private void compute_pcm_samples7(OutputBuffer buffer) {
        float[] vp = this.actual_v;
        float[] tmpOut = this._tmpOut;
        int dvp = 0;
        for (int i = 0; i < 32; ++i) {
            float pcm_sample;
            float[] dp = d16[i];
            tmpOut[i] = pcm_sample = (vp[7 + dvp] * dp[0] + vp[6 + dvp] * dp[1] + vp[5 + dvp] * dp[2] + vp[4 + dvp] * dp[3] + vp[3 + dvp] * dp[4] + vp[2 + dvp] * dp[5] + vp[1 + dvp] * dp[6] + vp[0 + dvp] * dp[7] + vp[15 + dvp] * dp[8] + vp[14 + dvp] * dp[9] + vp[13 + dvp] * dp[10] + vp[12 + dvp] * dp[11] + vp[11 + dvp] * dp[12] + vp[10 + dvp] * dp[13] + vp[9 + dvp] * dp[14] + vp[8 + dvp] * dp[15]) * this.scalefactor;
            dvp += 16;
        }
    }

    private void compute_pcm_samples8(OutputBuffer buffer) {
        float[] vp = this.actual_v;
        float[] tmpOut = this._tmpOut;
        int dvp = 0;
        for (int i = 0; i < 32; ++i) {
            float pcm_sample;
            float[] dp = d16[i];
            tmpOut[i] = pcm_sample = (vp[8 + dvp] * dp[0] + vp[7 + dvp] * dp[1] + vp[6 + dvp] * dp[2] + vp[5 + dvp] * dp[3] + vp[4 + dvp] * dp[4] + vp[3 + dvp] * dp[5] + vp[2 + dvp] * dp[6] + vp[1 + dvp] * dp[7] + vp[0 + dvp] * dp[8] + vp[15 + dvp] * dp[9] + vp[14 + dvp] * dp[10] + vp[13 + dvp] * dp[11] + vp[12 + dvp] * dp[12] + vp[11 + dvp] * dp[13] + vp[10 + dvp] * dp[14] + vp[9 + dvp] * dp[15]) * this.scalefactor;
            dvp += 16;
        }
    }

    private void compute_pcm_samples9(OutputBuffer buffer) {
        float[] vp = this.actual_v;
        float[] tmpOut = this._tmpOut;
        int dvp = 0;
        for (int i = 0; i < 32; ++i) {
            float pcm_sample;
            float[] dp = d16[i];
            tmpOut[i] = pcm_sample = (vp[9 + dvp] * dp[0] + vp[8 + dvp] * dp[1] + vp[7 + dvp] * dp[2] + vp[6 + dvp] * dp[3] + vp[5 + dvp] * dp[4] + vp[4 + dvp] * dp[5] + vp[3 + dvp] * dp[6] + vp[2 + dvp] * dp[7] + vp[1 + dvp] * dp[8] + vp[0 + dvp] * dp[9] + vp[15 + dvp] * dp[10] + vp[14 + dvp] * dp[11] + vp[13 + dvp] * dp[12] + vp[12 + dvp] * dp[13] + vp[11 + dvp] * dp[14] + vp[10 + dvp] * dp[15]) * this.scalefactor;
            dvp += 16;
        }
    }

    private void compute_pcm_samples10(OutputBuffer buffer) {
        float[] vp = this.actual_v;
        float[] tmpOut = this._tmpOut;
        int dvp = 0;
        for (int i = 0; i < 32; ++i) {
            float pcm_sample;
            float[] dp = d16[i];
            tmpOut[i] = pcm_sample = (vp[10 + dvp] * dp[0] + vp[9 + dvp] * dp[1] + vp[8 + dvp] * dp[2] + vp[7 + dvp] * dp[3] + vp[6 + dvp] * dp[4] + vp[5 + dvp] * dp[5] + vp[4 + dvp] * dp[6] + vp[3 + dvp] * dp[7] + vp[2 + dvp] * dp[8] + vp[1 + dvp] * dp[9] + vp[0 + dvp] * dp[10] + vp[15 + dvp] * dp[11] + vp[14 + dvp] * dp[12] + vp[13 + dvp] * dp[13] + vp[12 + dvp] * dp[14] + vp[11 + dvp] * dp[15]) * this.scalefactor;
            dvp += 16;
        }
    }

    private void compute_pcm_samples11(OutputBuffer buffer) {
        float[] vp = this.actual_v;
        float[] tmpOut = this._tmpOut;
        int dvp = 0;
        for (int i = 0; i < 32; ++i) {
            float pcm_sample;
            float[] dp = d16[i];
            tmpOut[i] = pcm_sample = (vp[11 + dvp] * dp[0] + vp[10 + dvp] * dp[1] + vp[9 + dvp] * dp[2] + vp[8 + dvp] * dp[3] + vp[7 + dvp] * dp[4] + vp[6 + dvp] * dp[5] + vp[5 + dvp] * dp[6] + vp[4 + dvp] * dp[7] + vp[3 + dvp] * dp[8] + vp[2 + dvp] * dp[9] + vp[1 + dvp] * dp[10] + vp[0 + dvp] * dp[11] + vp[15 + dvp] * dp[12] + vp[14 + dvp] * dp[13] + vp[13 + dvp] * dp[14] + vp[12 + dvp] * dp[15]) * this.scalefactor;
            dvp += 16;
        }
    }

    private void compute_pcm_samples12(OutputBuffer buffer) {
        float[] vp = this.actual_v;
        float[] tmpOut = this._tmpOut;
        int dvp = 0;
        for (int i = 0; i < 32; ++i) {
            float pcm_sample;
            float[] dp = d16[i];
            tmpOut[i] = pcm_sample = (vp[12 + dvp] * dp[0] + vp[11 + dvp] * dp[1] + vp[10 + dvp] * dp[2] + vp[9 + dvp] * dp[3] + vp[8 + dvp] * dp[4] + vp[7 + dvp] * dp[5] + vp[6 + dvp] * dp[6] + vp[5 + dvp] * dp[7] + vp[4 + dvp] * dp[8] + vp[3 + dvp] * dp[9] + vp[2 + dvp] * dp[10] + vp[1 + dvp] * dp[11] + vp[0 + dvp] * dp[12] + vp[15 + dvp] * dp[13] + vp[14 + dvp] * dp[14] + vp[13 + dvp] * dp[15]) * this.scalefactor;
            dvp += 16;
        }
    }

    private void compute_pcm_samples13(OutputBuffer buffer) {
        float[] vp = this.actual_v;
        float[] tmpOut = this._tmpOut;
        int dvp = 0;
        for (int i = 0; i < 32; ++i) {
            float pcm_sample;
            float[] dp = d16[i];
            tmpOut[i] = pcm_sample = (vp[13 + dvp] * dp[0] + vp[12 + dvp] * dp[1] + vp[11 + dvp] * dp[2] + vp[10 + dvp] * dp[3] + vp[9 + dvp] * dp[4] + vp[8 + dvp] * dp[5] + vp[7 + dvp] * dp[6] + vp[6 + dvp] * dp[7] + vp[5 + dvp] * dp[8] + vp[4 + dvp] * dp[9] + vp[3 + dvp] * dp[10] + vp[2 + dvp] * dp[11] + vp[1 + dvp] * dp[12] + vp[0 + dvp] * dp[13] + vp[15 + dvp] * dp[14] + vp[14 + dvp] * dp[15]) * this.scalefactor;
            dvp += 16;
        }
    }

    private void compute_pcm_samples14(OutputBuffer buffer) {
        float[] vp = this.actual_v;
        float[] tmpOut = this._tmpOut;
        int dvp = 0;
        for (int i = 0; i < 32; ++i) {
            float pcm_sample;
            float[] dp = d16[i];
            tmpOut[i] = pcm_sample = (vp[14 + dvp] * dp[0] + vp[13 + dvp] * dp[1] + vp[12 + dvp] * dp[2] + vp[11 + dvp] * dp[3] + vp[10 + dvp] * dp[4] + vp[9 + dvp] * dp[5] + vp[8 + dvp] * dp[6] + vp[7 + dvp] * dp[7] + vp[6 + dvp] * dp[8] + vp[5 + dvp] * dp[9] + vp[4 + dvp] * dp[10] + vp[3 + dvp] * dp[11] + vp[2 + dvp] * dp[12] + vp[1 + dvp] * dp[13] + vp[0 + dvp] * dp[14] + vp[15 + dvp] * dp[15]) * this.scalefactor;
            dvp += 16;
        }
    }

    private void compute_pcm_samples15(OutputBuffer buffer) {
        float[] vp = this.actual_v;
        float[] tmpOut = this._tmpOut;
        int dvp = 0;
        for (int i = 0; i < 32; ++i) {
            float pcm_sample;
            float[] dp = d16[i];
            tmpOut[i] = pcm_sample = (vp[15 + dvp] * dp[0] + vp[14 + dvp] * dp[1] + vp[13 + dvp] * dp[2] + vp[12 + dvp] * dp[3] + vp[11 + dvp] * dp[4] + vp[10 + dvp] * dp[5] + vp[9 + dvp] * dp[6] + vp[8 + dvp] * dp[7] + vp[7 + dvp] * dp[8] + vp[6 + dvp] * dp[9] + vp[5 + dvp] * dp[10] + vp[4 + dvp] * dp[11] + vp[3 + dvp] * dp[12] + vp[2 + dvp] * dp[13] + vp[1 + dvp] * dp[14] + vp[0 + dvp] * dp[15]) * this.scalefactor;
            dvp += 16;
        }
    }

    private void compute_pcm_samples(OutputBuffer buffer) {
        switch (this.actual_write_pos) {
            case 0: {
                this.compute_pcm_samples0(buffer);
                break;
            }
            case 1: {
                this.compute_pcm_samples1(buffer);
                break;
            }
            case 2: {
                this.compute_pcm_samples2(buffer);
                break;
            }
            case 3: {
                this.compute_pcm_samples3(buffer);
                break;
            }
            case 4: {
                this.compute_pcm_samples4(buffer);
                break;
            }
            case 5: {
                this.compute_pcm_samples5(buffer);
                break;
            }
            case 6: {
                this.compute_pcm_samples6(buffer);
                break;
            }
            case 7: {
                this.compute_pcm_samples7(buffer);
                break;
            }
            case 8: {
                this.compute_pcm_samples8(buffer);
                break;
            }
            case 9: {
                this.compute_pcm_samples9(buffer);
                break;
            }
            case 10: {
                this.compute_pcm_samples10(buffer);
                break;
            }
            case 11: {
                this.compute_pcm_samples11(buffer);
                break;
            }
            case 12: {
                this.compute_pcm_samples12(buffer);
                break;
            }
            case 13: {
                this.compute_pcm_samples13(buffer);
                break;
            }
            case 14: {
                this.compute_pcm_samples14(buffer);
                break;
            }
            case 15: {
                this.compute_pcm_samples15(buffer);
            }
        }
        if (buffer != null) {
            buffer.appendSamples(this.channel, this._tmpOut);
        }
    }

    public void calculate_pcm_samples(OutputBuffer buffer) {
        this.compute_new_v();
        this.compute_pcm_samples(buffer);
        this.actual_write_pos = this.actual_write_pos + 1 & 15;
        this.actual_v = this.actual_v == this.v1 ? this.v2 : this.v1;
        for (int p = 0; p < 32; ++p) {
            this.samples[p] = 0.0f;
        }
    }

    private static float[] load_d() {
        try {
            Class<Float> elemType = Float.TYPE;
            Object o = SynthesisFilter.deserializeArray(SynthesisFilter.class.getResourceAsStream("/sfd.ser"), elemType, 512);
            return (float[])o;
        }
        catch (IOException ex) {
            throw new ExceptionInInitializerError(ex);
        }
    }

    private static Object deserializeArray(InputStream in, Class elemType, int length) throws IOException {
        int arrayLength;
        if (elemType == null) {
            throw new NullPointerException("elemType");
        }
        if (length < -1) {
            throw new IllegalArgumentException("length");
        }
        Object obj = SynthesisFilter.deserialize(in);
        Class cls = obj.getClass();
        if (!cls.isArray()) {
            throw new InvalidObjectException("object is not an array");
        }
        Class arrayElemType = cls.getComponentType();
        if (arrayElemType != elemType) {
            throw new InvalidObjectException("unexpected array component type");
        }
        if (length != -1 && (arrayLength = Array.getLength(obj)) != length) {
            throw new InvalidObjectException("array length mismatch");
        }
        return obj;
    }

    public static Object deserialize(InputStream in) throws IOException {
        Object obj;
        if (in == null) {
            throw new NullPointerException("in");
        }
        ObjectInputStream objIn = new ObjectInputStream(in);
        try {
            obj = objIn.readObject();
        }
        catch (ClassNotFoundException ex) {
            throw new InvalidClassException(ex.toString());
        }
        return obj;
    }

    private static float[][] splitArray(float[] array, int blockSize) {
        int size = array.length / blockSize;
        float[][] split = new float[size][];
        for (int i = 0; i < size; ++i) {
            split[i] = SynthesisFilter.subArray(array, i * blockSize, blockSize);
        }
        return split;
    }

    private static float[] subArray(float[] array, int offs, int len) {
        if (offs + len > array.length) {
            len = array.length - offs;
        }
        if (len < 0) {
            len = 0;
        }
        float[] subarray = new float[len];
        for (int i = 0; i < len; ++i) {
            subarray[i] = array[offs + i];
        }
        return subarray;
    }
}

