/*
 * Decompiled with CFR 0_122.
 */
package com.badlogic.gdx.utils;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringWriter;

public final class StreamUtils {
    public static final int DEFAULT_BUFFER_SIZE = 8192;
    public static final byte[] EMPTY_BYTES = new byte[0];

    public static void copyStream(InputStream input, OutputStream output) throws IOException {
        StreamUtils.copyStream(input, output, 8192);
    }

    public static void copyStream(InputStream input, OutputStream output, int bufferSize) throws IOException {
        int bytesRead;
        byte[] buffer = new byte[bufferSize];
        while ((bytesRead = input.read(buffer)) != -1) {
            output.write(buffer, 0, bytesRead);
        }
    }

    public static byte[] copyStreamToByteArray(InputStream input) throws IOException {
        return StreamUtils.copyStreamToByteArray(input, input.available());
    }

    public static byte[] copyStreamToByteArray(InputStream input, int estimatedSize) throws IOException {
        OptimizedByteArrayOutputStream baos = new OptimizedByteArrayOutputStream(Math.max(0, estimatedSize));
        StreamUtils.copyStream(input, baos);
        return baos.toByteArray();
    }

    public static String copyStreamToString(InputStream input) throws IOException {
        return StreamUtils.copyStreamToString(input, input.available());
    }

    public static String copyStreamToString(InputStream input, int approxStringLength) throws IOException {
        int charsRead;
        BufferedReader reader = new BufferedReader(new InputStreamReader(input));
        StringWriter w = new StringWriter(Math.max(0, approxStringLength));
        char[] buffer = new char[8192];
        while ((charsRead = reader.read(buffer)) != -1) {
            w.write(buffer, 0, charsRead);
        }
        return w.toString();
    }

    public static void closeQuietly(Closeable c) {
        if (c != null) {
            try {
                c.close();
            }
            catch (Exception e) {
                // empty catch block
            }
        }
    }

    public static class OptimizedByteArrayOutputStream
    extends ByteArrayOutputStream {
        public OptimizedByteArrayOutputStream(int initialSize) {
            super(initialSize);
        }

        @Override
        public synchronized byte[] toByteArray() {
            if (this.count == this.buf.length) {
                return this.buf;
            }
            return super.toByteArray();
        }

        public byte[] getBuffer() {
            return this.buf;
        }
    }

}

