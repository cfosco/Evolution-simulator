/*
 * Decompiled with CFR 0_122.
 */
package org.lwjgl.opencl;

public class OpenCLException
extends RuntimeException {
    private static final long serialVersionUID = 1;

    public OpenCLException() {
    }

    public OpenCLException(String message) {
        super(message);
    }

    public OpenCLException(String message, Throwable cause) {
        super(message, cause);
    }

    public OpenCLException(Throwable cause) {
        super(cause);
    }
}

