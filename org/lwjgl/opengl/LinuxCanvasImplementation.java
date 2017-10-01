/*
 * Decompiled with CFR 0_122.
 */
package org.lwjgl.opengl;

import java.awt.Canvas;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedExceptionAction;
import org.lwjgl.LWJGLException;
import org.lwjgl.LWJGLUtil;
import org.lwjgl.opengl.AWTCanvasImplementation;
import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.LinuxAWTGLCanvasPeerInfo;
import org.lwjgl.opengl.PeerInfo;
import org.lwjgl.opengl.PixelFormat;

final class LinuxCanvasImplementation
implements AWTCanvasImplementation {
    LinuxCanvasImplementation() {
    }

    static int getScreenFromDevice(final GraphicsDevice device) throws LWJGLException {
        try {
            Method getScreen_method = (Method)AccessController.doPrivileged(new PrivilegedExceptionAction<Method>(){

                @Override
                public Method run() throws Exception {
                    return device.getClass().getMethod("getScreen", new Class[0]);
                }
            });
            Integer screen = (Integer)getScreen_method.invoke(device, new Object[0]);
            return screen;
        }
        catch (Exception e) {
            throw new LWJGLException(e);
        }
    }

    private static int getVisualIDFromConfiguration(final GraphicsConfiguration configuration) throws LWJGLException {
        try {
            Method getVisual_method = (Method)AccessController.doPrivileged(new PrivilegedExceptionAction<Method>(){

                @Override
                public Method run() throws Exception {
                    return configuration.getClass().getMethod("getVisual", new Class[0]);
                }
            });
            Integer visual = (Integer)getVisual_method.invoke(configuration, new Object[0]);
            return visual;
        }
        catch (Exception e) {
            throw new LWJGLException(e);
        }
    }

    public PeerInfo createPeerInfo(Canvas component, PixelFormat pixel_format, ContextAttribs attribs) throws LWJGLException {
        return new LinuxAWTGLCanvasPeerInfo(component);
    }

    public GraphicsConfiguration findConfiguration(GraphicsDevice device, PixelFormat pixel_format) throws LWJGLException {
        try {
            GraphicsConfiguration[] configurations;
            int screen = LinuxCanvasImplementation.getScreenFromDevice(device);
            int visual_id_matching_format = LinuxCanvasImplementation.findVisualIDFromFormat(screen, pixel_format);
            for (GraphicsConfiguration configuration : configurations = device.getConfigurations()) {
                int visual_id = LinuxCanvasImplementation.getVisualIDFromConfiguration(configuration);
                if (visual_id != visual_id_matching_format) continue;
                return configuration;
            }
        }
        catch (LWJGLException e) {
            LWJGLUtil.log("Got exception while trying to determine configuration: " + e);
        }
        return null;
    }

    /*
     * Exception decompiling
     */
    private static int findVisualIDFromFormat(int screen, PixelFormat pixel_format) throws LWJGLException {
        // This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
        // org.benf.cfr.reader.util.ConfusedCFRException: Tried to end blocks [1[TRYBLOCK]], but top level block is 3[TRYBLOCK]
        // org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.processEndingBlocks(Op04StructuredStatement.java:397)
        // org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.buildNestedBlocks(Op04StructuredStatement.java:449)
        // org.benf.cfr.reader.bytecode.analysis.opgraph.Op03SimpleStatement.createInitialStructuredBlock(Op03SimpleStatement.java:2879)
        // org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:825)
        // org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisOrWrapFail(CodeAnalyser.java:217)
        // org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysis(CodeAnalyser.java:162)
        // org.benf.cfr.reader.entities.attributes.AttributeCode.analyse(AttributeCode.java:95)
        // org.benf.cfr.reader.entities.Method.analyse(Method.java:357)
        // org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:769)
        // org.benf.cfr.reader.entities.ClassFile.analyseTop(ClassFile.java:701)
        // org.benf.cfr.reader.Main.doJar(Main.java:134)
        // org.benf.cfr.reader.Main.main(Main.java:189)
        throw new IllegalStateException("Decompilation failed");
    }

    private static native int nFindVisualIDFromFormat(long var0, int var2, PixelFormat var3) throws LWJGLException;

}

