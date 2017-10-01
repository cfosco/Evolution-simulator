/*
 * Decompiled with CFR 0_122.
 */
package javazoom.jl.decoder;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.io.UnsupportedEncodingException;
import javazoom.jl.decoder.BitstreamException;
import javazoom.jl.decoder.Crc16;
import javazoom.jl.decoder.Header;

public final class Bitstream {
    static byte INITIAL_SYNC = 0;
    static byte STRICT_SYNC = 1;
    private static final int BUFFER_INT_SIZE = 433;
    private final int[] framebuffer = new int[433];
    private int framesize;
    private byte[] frame_bytes = new byte[1732];
    private int wordpointer;
    private int bitindex;
    private int syncword;
    private int header_pos = 0;
    private Float replayGainScale;
    private boolean single_ch_mode;
    private final int[] bitmask = new int[]{0, 1, 3, 7, 15, 31, 63, 127, 255, 511, 1023, 2047, 4095, 8191, 16383, 32767, 65535, 131071};
    private final PushbackInputStream source;
    private final Header header = new Header();
    private final byte[] syncbuf = new byte[4];
    private Crc16[] crc = new Crc16[1];
    private byte[] rawid3v2 = null;
    private boolean firstframe = true;
    public static final int BITSTREAM_ERROR = 256;
    public static final int UNKNOWN_ERROR = 256;
    public static final int UNKNOWN_SAMPLE_RATE = 257;
    public static final int STREAM_ERROR = 258;
    public static final int UNEXPECTED_EOF = 259;
    public static final int STREAM_EOF = 260;
    public static final int INVALIDFRAME = 261;
    public static final int BITSTREAM_LAST = 511;

    public Bitstream(InputStream in) {
        if (in == null) {
            throw new NullPointerException("in");
        }
        in = new BufferedInputStream(in);
        this.loadID3v2(in);
        this.firstframe = true;
        this.source = new PushbackInputStream(in, 1732);
        this.closeFrame();
    }

    public int header_pos() {
        return this.header_pos;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Unable to fully structure code
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     * Lifted jumps to return sites
     */
    private void loadID3v2(InputStream in) {
        size = -1;
        in.mark(10);
        this.header_pos = size = this.readID3v2Header(in);
        try {
            in.reset();
        }
        catch (IOException e) {}
        ** GOTO lbl23
        catch (IOException e) {
            try {
                in.reset();
            }
            catch (IOException e) {}
            catch (Throwable var4_7) {
                try {
                    in.reset();
                    throw var4_7;
                }
                catch (IOException e) {
                    // empty catch block
                }
                throw var4_7;
            }
        }
lbl23: // 4 sources:
        try {
            if (size <= 0) return;
            this.rawid3v2 = new byte[size];
            in.read(this.rawid3v2, 0, this.rawid3v2.length);
            this.parseID3v2Frames(this.rawid3v2);
            return;
        }
        catch (IOException e) {
            // empty catch block
        }
    }

    private int readID3v2Header(InputStream in) throws IOException {
        byte[] id3header = new byte[4];
        int size = -10;
        in.read(id3header, 0, 3);
        if (id3header[0] == 73 && id3header[1] == 68 && id3header[2] == 51) {
            in.read(id3header, 0, 3);
            in.read(id3header, 0, 4);
            size = (id3header[0] << 21) + (id3header[1] << 14) + (id3header[2] << 7) + id3header[3];
        }
        return size + 10;
    }

    public InputStream getRawID3v2() {
        if (this.rawid3v2 == null) {
            return null;
        }
        ByteArrayInputStream bain = new ByteArrayInputStream(this.rawid3v2);
        return bain;
    }

    private void parseID3v2Frames(byte[] bframes) {
        if (bframes == null) {
            return;
        }
        if (!"ID3".equals(new String(bframes, 0, 3))) {
            return;
        }
        int v2version = bframes[3] & 255;
        if (v2version < 2 || v2version > 4) {
            return;
        }
        try {
            int size;
            Float replayGain = null;
            Float replayGainPeak = null;
            String value = null;
            for (int i = 10; i < bframes.length && bframes[i] > 0; i += size) {
                String name;
                String[] values;
                if (v2version == 3 || v2version == 4) {
                    String code = new String(bframes, i, 4);
                    size = bframes[i + 4] << 24 & -16777216 | bframes[i + 5] << 16 & 16711680 | bframes[i + 6] << 8 & 65280 | bframes[i + 7] & 255;
                    if (!code.equals("TXXX") || (values = (value = this.parseText(bframes, i += 10, size, 1)).split("\u0000")).length != 2) continue;
                    name = values[0];
                    value = values[1];
                    if (name.equals("replaygain_track_peak")) {
                        replayGainPeak = Float.valueOf(Float.parseFloat(value));
                        if (replayGain == null) continue;
                        break;
                    }
                    if (!name.equals("replaygain_track_gain")) continue;
                    replayGain = Float.valueOf(Float.parseFloat(value.replace(" dB", "")) + 3.0f);
                    if (replayGainPeak == null) continue;
                    break;
                }
                String scode = new String(bframes, i, 3);
                size = 0 + (bframes[i + 3] << 16) + (bframes[i + 4] << 8) + bframes[i + 5];
                if (!scode.equals("TXXX") || (values = (value = this.parseText(bframes, i += 6, size, 1)).split("\u0000")).length != 2) continue;
                name = values[0];
                value = values[1];
                if (name.equals("replaygain_track_peak")) {
                    replayGainPeak = Float.valueOf(Float.parseFloat(value));
                    if (replayGain == null) continue;
                    break;
                }
                if (!name.equals("replaygain_track_gain")) continue;
                replayGain = Float.valueOf(Float.parseFloat(value.replace(" dB", "")) + 3.0f);
                if (replayGainPeak != null) break;
            }
            if (replayGain != null && replayGainPeak != null) {
                this.replayGainScale = Float.valueOf((float)Math.pow(10.0, replayGain.floatValue() / 20.0f));
                this.replayGainScale = Float.valueOf(Math.min(1.0f / replayGainPeak.floatValue(), this.replayGainScale.floatValue()));
            }
        }
        catch (RuntimeException ignored) {
            // empty catch block
        }
    }

    private String parseText(byte[] bframes, int offset, int size, int skip) {
        String value = null;
        try {
            String[] ENC_TYPES = new String[]{"ISO-8859-1", "UTF16", "UTF-16BE", "UTF-8"};
            value = new String(bframes, offset + skip, size - skip, ENC_TYPES[bframes[offset]]);
        }
        catch (UnsupportedEncodingException e) {
            // empty catch block
        }
        return value;
    }

    public Float getReplayGainScale() {
        return this.replayGainScale;
    }

    public void close() throws BitstreamException {
        try {
            this.source.close();
        }
        catch (IOException ex) {
            throw this.newBitstreamException(258, ex);
        }
    }

    public Header readFrame() throws BitstreamException {
        Header result;
        block7 : {
            result = null;
            try {
                result = this.readNextFrame();
                if (this.firstframe) {
                    result.parseVBR(this.frame_bytes);
                    this.firstframe = false;
                }
            }
            catch (BitstreamException ex) {
                if (ex.getErrorCode() == 261) {
                    try {
                        this.closeFrame();
                        result = this.readNextFrame();
                    }
                    catch (BitstreamException e) {
                        if (e.getErrorCode() != 260) {
                            throw this.newBitstreamException(e.getErrorCode(), e);
                        }
                        break block7;
                    }
                }
                if (ex.getErrorCode() == 260) break block7;
                throw this.newBitstreamException(ex.getErrorCode(), ex);
            }
        }
        return result;
    }

    private Header readNextFrame() throws BitstreamException {
        if (this.framesize == -1) {
            this.nextFrame();
        }
        return this.header;
    }

    private void nextFrame() throws BitstreamException {
        this.header.read_header(this, this.crc);
    }

    public void unreadFrame() throws BitstreamException {
        if (this.wordpointer == -1 && this.bitindex == -1 && this.framesize > 0) {
            try {
                this.source.unread(this.frame_bytes, 0, this.framesize);
            }
            catch (IOException ex) {
                throw this.newBitstreamException(258);
            }
        }
    }

    public void closeFrame() {
        this.framesize = -1;
        this.wordpointer = -1;
        this.bitindex = -1;
    }

    public boolean isSyncCurrentPosition(int syncmode) throws BitstreamException {
        int read = this.readBytes(this.syncbuf, 0, 4);
        int headerstring = this.syncbuf[0] << 24 & -16777216 | this.syncbuf[1] << 16 & 16711680 | this.syncbuf[2] << 8 & 65280 | this.syncbuf[3] << 0 & 255;
        try {
            this.source.unread(this.syncbuf, 0, read);
        }
        catch (IOException ex) {
            // empty catch block
        }
        boolean sync = false;
        switch (read) {
            case 0: {
                sync = true;
                break;
            }
            case 4: {
                sync = this.isSyncMark(headerstring, syncmode, this.syncword);
            }
        }
        return sync;
    }

    public int readBits(int n) {
        return this.get_bits(n);
    }

    public int readCheckedBits(int n) {
        return this.get_bits(n);
    }

    protected BitstreamException newBitstreamException(int errorcode) {
        return new BitstreamException(errorcode, null);
    }

    protected BitstreamException newBitstreamException(int errorcode, Throwable throwable) {
        return new BitstreamException(errorcode, throwable);
    }

    int syncHeader(byte syncmode) throws BitstreamException {
        boolean sync;
        int bytesRead = this.readBytes(this.syncbuf, 0, 3);
        if (bytesRead != 3) {
            throw this.newBitstreamException(260, null);
        }
        int headerstring = this.syncbuf[0] << 16 & 16711680 | this.syncbuf[1] << 8 & 65280 | this.syncbuf[2] << 0 & 255;
        do {
            headerstring <<= 8;
            if (this.readBytes(this.syncbuf, 3, 1) == 1) continue;
            throw this.newBitstreamException(260, null);
        } while (!(sync = this.isSyncMark(headerstring |= this.syncbuf[3] & 255, syncmode, this.syncword)));
        return headerstring;
    }

    public boolean isSyncMark(int headerstring, int syncmode, int word) {
        boolean sync = false;
        if (syncmode == INITIAL_SYNC) {
            sync = (headerstring & -2097152) == -2097152;
        } else {
            boolean bl = (headerstring & -521216) == word && (headerstring & 192) == 192 == this.single_ch_mode ? true : (sync = false);
        }
        if (sync) {
            boolean bl = sync = (headerstring >>> 10 & 3) != 3;
        }
        if (sync) {
            boolean bl = sync = (headerstring >>> 17 & 3) != 0;
        }
        if (sync) {
            sync = (headerstring >>> 19 & 3) != 1;
        }
        return sync;
    }

    int read_frame_data(int bytesize) throws BitstreamException {
        int numread = 0;
        numread = this.readFully(this.frame_bytes, 0, bytesize);
        this.framesize = bytesize;
        this.wordpointer = -1;
        this.bitindex = -1;
        return numread;
    }

    void parse_frame() throws BitstreamException {
        int b = 0;
        byte[] byteread = this.frame_bytes;
        int bytesize = this.framesize;
        for (int k = 0; k < bytesize; k += 4) {
            byte b0 = 0;
            byte b1 = 0;
            byte b2 = 0;
            byte b3 = 0;
            b0 = byteread[k];
            if (k + 1 < bytesize) {
                b1 = byteread[k + 1];
            }
            if (k + 2 < bytesize) {
                b2 = byteread[k + 2];
            }
            if (k + 3 < bytesize) {
                b3 = byteread[k + 3];
            }
            this.framebuffer[b++] = b0 << 24 & -16777216 | b1 << 16 & 16711680 | b2 << 8 & 65280 | b3 & 255;
        }
        this.wordpointer = 0;
        this.bitindex = 0;
    }

    public int get_bits(int number_of_bits) {
        int returnvalue = 0;
        int sum = this.bitindex + number_of_bits;
        if (this.wordpointer < 0) {
            this.wordpointer = 0;
        }
        if (sum <= 32) {
            returnvalue = this.framebuffer[this.wordpointer] >>> 32 - sum & this.bitmask[number_of_bits];
            if ((this.bitindex += number_of_bits) == 32) {
                this.bitindex = 0;
                ++this.wordpointer;
            }
            return returnvalue;
        }
        int Right = this.framebuffer[this.wordpointer] & 65535;
        ++this.wordpointer;
        int Left = this.framebuffer[this.wordpointer] & -65536;
        returnvalue = Right << 16 & -65536 | Left >>> 16 & 65535;
        returnvalue >>>= 48 - sum;
        this.bitindex = sum - 32;
        return returnvalue &= this.bitmask[number_of_bits];
    }

    void set_syncword(int syncword0) {
        this.syncword = syncword0 & -193;
        this.single_ch_mode = (syncword0 & 192) == 192;
    }

    private int readFully(byte[] b, int offs, int len) throws BitstreamException {
        int nRead;
        nRead = 0;
        try {
            while (len > 0) {
                int bytesread = this.source.read(b, offs, len);
                if (bytesread == -1) {
                    while (len-- > 0) {
                        b[offs++] = 0;
                    }
                    break;
                }
                nRead += bytesread;
                offs += bytesread;
                len -= bytesread;
            }
        }
        catch (IOException ex) {
            throw this.newBitstreamException(258, ex);
        }
        return nRead;
    }

    private int readBytes(byte[] b, int offs, int len) throws BitstreamException {
        int totalBytesRead = 0;
        try {
            int bytesread;
            while (len > 0 && (bytesread = this.source.read(b, offs, len)) != -1) {
                totalBytesRead += bytesread;
                offs += bytesread;
                len -= bytesread;
            }
        }
        catch (IOException ex) {
            throw this.newBitstreamException(258, ex);
        }
        return totalBytesRead;
    }
}

