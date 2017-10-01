/*
 * Decompiled with CFR 0_122.
 */
package javazoom.jl.decoder;

public class OutputBuffer {
    public static final int BUFFERSIZE = 2304;
    private static final int MAXCHANNELS = 2;
    private Float replayGainScale;
    private int channels;
    private byte[] buffer;
    private int[] channelPointer;
    private boolean isBigEndian;

    public OutputBuffer(int channels, boolean isBigEndian) {
        this.channels = channels;
        this.isBigEndian = isBigEndian;
        this.buffer = new byte[2304 * channels];
        this.channelPointer = new int[channels];
        this.reset();
    }

    private void append(int channel, short value) {
        byte secondByte;
        byte firstByte;
        if (this.isBigEndian) {
            firstByte = (byte)(value >>> 8 & 255);
            secondByte = (byte)(value & 255);
        } else {
            firstByte = (byte)(value & 255);
            secondByte = (byte)(value >>> 8 & 255);
        }
        this.buffer[this.channelPointer[channel]] = firstByte;
        this.buffer[this.channelPointer[channel] + 1] = secondByte;
        int[] arrn = this.channelPointer;
        int n = channel;
        arrn[n] = arrn[n] + this.channels * 2;
    }

    public void appendSamples(int channel, float[] f) {
        if (this.replayGainScale != null) {
            int i = 0;
            while (i < 32) {
                short s = this.clip(f[i++] * this.replayGainScale.floatValue());
                this.append(channel, s);
            }
        } else {
            int i = 0;
            while (i < 32) {
                short s = this.clip(f[i++]);
                this.append(channel, s);
            }
        }
    }

    public byte[] getBuffer() {
        return this.buffer;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public int reset() {
        int n;
        try {
            int index = this.channels - 1;
            n = this.channelPointer[index] - index * 2;
        }
        catch (Throwable throwable) {
            for (int i = 0; i < this.channels; ++i) {
                this.channelPointer[i] = i * 2;
            }
            throw throwable;
        }
        for (int i = 0; i < this.channels; ++i) {
            this.channelPointer[i] = i * 2;
        }
        return n;
    }

    public void setReplayGainScale(Float replayGainScale) {
        this.replayGainScale = replayGainScale;
    }

    public boolean isStereo() {
        return this.channelPointer[1] == 2;
    }

    private final short clip(float sample) {
        return (short)(sample > 32767.0f ? 32767 : (short)(sample < -32768.0f ? -32768 : (short)sample));
    }
}

