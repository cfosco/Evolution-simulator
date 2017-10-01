/*
 * Decompiled with CFR 0_122.
 */
package org.lwjgl.opencl;

import org.lwjgl.opencl.CL;
import org.lwjgl.opencl.CLDevice;
import org.lwjgl.opencl.CLDeviceCapabilities;
import org.lwjgl.opencl.CLPlatform;
import org.lwjgl.opencl.CLPlatformCapabilities;

public final class CLCapabilities {
    static final boolean CL_APPLE_ContextLoggingFunctions;
    static final long clLogMessagesToSystemLogAPPLE;
    static final long clLogMessagesToStdoutAPPLE;
    static final long clLogMessagesToStderrAPPLE;
    static final boolean CL_APPLE_SetMemObjectDestructor;
    static final long clSetMemObjectDestructorAPPLE;
    static final boolean CL_APPLE_gl_sharing;
    static final long clGetGLContextInfoAPPLE;
    static final boolean OpenCL10;
    static final long clGetPlatformIDs;
    static final long clGetPlatformInfo;
    static final long clGetDeviceIDs;
    static final long clGetDeviceInfo;
    static final long clCreateContext;
    static final long clCreateContextFromType;
    static final long clRetainContext;
    static final long clReleaseContext;
    static final long clGetContextInfo;
    static final long clCreateCommandQueue;
    static final long clRetainCommandQueue;
    static final long clReleaseCommandQueue;
    static final long clGetCommandQueueInfo;
    static final long clCreateBuffer;
    static final long clEnqueueReadBuffer;
    static final long clEnqueueWriteBuffer;
    static final long clEnqueueCopyBuffer;
    static final long clEnqueueMapBuffer;
    static final long clCreateImage2D;
    static final long clCreateImage3D;
    static final long clGetSupportedImageFormats;
    static final long clEnqueueReadImage;
    static final long clEnqueueWriteImage;
    static final long clEnqueueCopyImage;
    static final long clEnqueueCopyImageToBuffer;
    static final long clEnqueueCopyBufferToImage;
    static final long clEnqueueMapImage;
    static final long clGetImageInfo;
    static final long clRetainMemObject;
    static final long clReleaseMemObject;
    static final long clEnqueueUnmapMemObject;
    static final long clGetMemObjectInfo;
    static final long clCreateSampler;
    static final long clRetainSampler;
    static final long clReleaseSampler;
    static final long clGetSamplerInfo;
    static final long clCreateProgramWithSource;
    static final long clCreateProgramWithBinary;
    static final long clRetainProgram;
    static final long clReleaseProgram;
    static final long clBuildProgram;
    static final long clUnloadCompiler;
    static final long clGetProgramInfo;
    static final long clGetProgramBuildInfo;
    static final long clCreateKernel;
    static final long clCreateKernelsInProgram;
    static final long clRetainKernel;
    static final long clReleaseKernel;
    static final long clSetKernelArg;
    static final long clGetKernelInfo;
    static final long clGetKernelWorkGroupInfo;
    static final long clEnqueueNDRangeKernel;
    static final long clEnqueueTask;
    static final long clEnqueueNativeKernel;
    static final long clWaitForEvents;
    static final long clGetEventInfo;
    static final long clRetainEvent;
    static final long clReleaseEvent;
    static final long clEnqueueMarker;
    static final long clEnqueueBarrier;
    static final long clEnqueueWaitForEvents;
    static final long clGetEventProfilingInfo;
    static final long clFlush;
    static final long clFinish;
    static final long clGetExtensionFunctionAddress;
    static final boolean OpenCL10GL;
    static final long clCreateFromGLBuffer;
    static final long clCreateFromGLTexture2D;
    static final long clCreateFromGLTexture3D;
    static final long clCreateFromGLRenderbuffer;
    static final long clGetGLObjectInfo;
    static final long clGetGLTextureInfo;
    static final long clEnqueueAcquireGLObjects;
    static final long clEnqueueReleaseGLObjects;
    static final boolean OpenCL11;
    static final long clCreateSubBuffer;
    static final long clSetMemObjectDestructorCallback;
    static final long clEnqueueReadBufferRect;
    static final long clEnqueueWriteBufferRect;
    static final long clEnqueueCopyBufferRect;
    static final long clCreateUserEvent;
    static final long clSetUserEventStatus;
    static final long clSetEventCallback;
    static final boolean OpenCL12;
    static final long clRetainDevice;
    static final long clReleaseDevice;
    static final long clCreateSubDevices;
    static final long clCreateImage;
    static final long clCreateProgramWithBuiltInKernels;
    static final long clCompileProgram;
    static final long clLinkProgram;
    static final long clUnloadPlatformCompiler;
    static final long clGetKernelArgInfo;
    static final long clEnqueueFillBuffer;
    static final long clEnqueueFillImage;
    static final long clEnqueueMigrateMemObjects;
    static final long clEnqueueMarkerWithWaitList;
    static final long clEnqueueBarrierWithWaitList;
    static final long clSetPrintfCallback;
    static final long clGetExtensionFunctionAddressForPlatform;
    static final boolean OpenCL12GL;
    static final long clCreateFromGLTexture;
    static final boolean CL_EXT_device_fission;
    static final long clRetainDeviceEXT;
    static final long clReleaseDeviceEXT;
    static final long clCreateSubDevicesEXT;
    static final boolean CL_EXT_migrate_memobject;
    static final long clEnqueueMigrateMemObjectEXT;
    static final boolean CL_KHR_gl_event;
    static final long clCreateEventFromGLsyncKHR;
    static final boolean CL_KHR_gl_sharing;
    static final long clGetGLContextInfoKHR;
    static final boolean CL_KHR_icd;
    static final long clIcdGetPlatformIDsKHR;
    static final boolean CL_KHR_subgroups;
    static final long clGetKernelSubGroupInfoKHR;
    static final boolean CL_KHR_terminate_context;
    static final long clTerminateContextKHR;

    private CLCapabilities() {
    }

    public static CLPlatformCapabilities getPlatformCapabilities(CLPlatform platform) {
        platform.checkValid();
        CLPlatformCapabilities caps = (CLPlatformCapabilities)platform.getCapabilities();
        if (caps == null) {
            caps = new CLPlatformCapabilities(platform);
            platform.setCapabilities(caps);
        }
        return caps;
    }

    public static CLDeviceCapabilities getDeviceCapabilities(CLDevice device) {
        device.checkValid();
        CLDeviceCapabilities caps = (CLDeviceCapabilities)device.getCapabilities();
        if (caps == null) {
            caps = new CLDeviceCapabilities(device);
            device.setCapabilities(caps);
        }
        return caps;
    }

    private static boolean isAPPLE_ContextLoggingFunctionsSupported() {
        return clLogMessagesToSystemLogAPPLE != 0 & clLogMessagesToStdoutAPPLE != 0 & clLogMessagesToStderrAPPLE != 0;
    }

    private static boolean isAPPLE_SetMemObjectDestructorSupported() {
        return clSetMemObjectDestructorAPPLE != 0;
    }

    private static boolean isAPPLE_gl_sharingSupported() {
        return clGetGLContextInfoAPPLE != 0;
    }

    private static boolean isCL10Supported() {
        return clGetPlatformIDs != 0 & clGetPlatformInfo != 0 & clGetDeviceIDs != 0 & clGetDeviceInfo != 0 & clCreateContext != 0 & clCreateContextFromType != 0 & clRetainContext != 0 & clReleaseContext != 0 & clGetContextInfo != 0 & clCreateCommandQueue != 0 & clRetainCommandQueue != 0 & clReleaseCommandQueue != 0 & clGetCommandQueueInfo != 0 & clCreateBuffer != 0 & clEnqueueReadBuffer != 0 & clEnqueueWriteBuffer != 0 & clEnqueueCopyBuffer != 0 & clEnqueueMapBuffer != 0 & clCreateImage2D != 0 & clCreateImage3D != 0 & clGetSupportedImageFormats != 0 & clEnqueueReadImage != 0 & clEnqueueWriteImage != 0 & clEnqueueCopyImage != 0 & clEnqueueCopyImageToBuffer != 0 & clEnqueueCopyBufferToImage != 0 & clEnqueueMapImage != 0 & clGetImageInfo != 0 & clRetainMemObject != 0 & clReleaseMemObject != 0 & clEnqueueUnmapMemObject != 0 & clGetMemObjectInfo != 0 & clCreateSampler != 0 & clRetainSampler != 0 & clReleaseSampler != 0 & clGetSamplerInfo != 0 & clCreateProgramWithSource != 0 & clCreateProgramWithBinary != 0 & clRetainProgram != 0 & clReleaseProgram != 0 & clBuildProgram != 0 & clUnloadCompiler != 0 & clGetProgramInfo != 0 & clGetProgramBuildInfo != 0 & clCreateKernel != 0 & clCreateKernelsInProgram != 0 & clRetainKernel != 0 & clReleaseKernel != 0 & clSetKernelArg != 0 & clGetKernelInfo != 0 & clGetKernelWorkGroupInfo != 0 & clEnqueueNDRangeKernel != 0 & clEnqueueTask != 0 & clEnqueueNativeKernel != 0 & clWaitForEvents != 0 & clGetEventInfo != 0 & clRetainEvent != 0 & clReleaseEvent != 0 & clEnqueueMarker != 0 & clEnqueueBarrier != 0 & clEnqueueWaitForEvents != 0 & clGetEventProfilingInfo != 0 & clFlush != 0 & clFinish != 0 & clGetExtensionFunctionAddress != 0;
    }

    private static boolean isCL10GLSupported() {
        return clCreateFromGLBuffer != 0 & clCreateFromGLTexture2D != 0 & clCreateFromGLTexture3D != 0 & clCreateFromGLRenderbuffer != 0 & clGetGLObjectInfo != 0 & clGetGLTextureInfo != 0 & clEnqueueAcquireGLObjects != 0 & clEnqueueReleaseGLObjects != 0;
    }

    private static boolean isCL11Supported() {
        return clCreateSubBuffer != 0 & clSetMemObjectDestructorCallback != 0 & clEnqueueReadBufferRect != 0 & clEnqueueWriteBufferRect != 0 & clEnqueueCopyBufferRect != 0 & clCreateUserEvent != 0 & clSetUserEventStatus != 0 & clSetEventCallback != 0;
    }

    private static boolean isCL12Supported() {
        if (clSetPrintfCallback == 0) {
            // empty if block
        }
        return clRetainDevice != 0 & clReleaseDevice != 0 & clCreateSubDevices != 0 & clCreateImage != 0 & clCreateProgramWithBuiltInKernels != 0 & clCompileProgram != 0 & clLinkProgram != 0 & clUnloadPlatformCompiler != 0 & clGetKernelArgInfo != 0 & clEnqueueFillBuffer != 0 & clEnqueueFillImage != 0 & clEnqueueMigrateMemObjects != 0 & clEnqueueMarkerWithWaitList != 0 & clEnqueueBarrierWithWaitList != 0 & true & clGetExtensionFunctionAddressForPlatform != 0;
    }

    private static boolean isCL12GLSupported() {
        return clCreateFromGLTexture != 0;
    }

    private static boolean isEXT_device_fissionSupported() {
        return clRetainDeviceEXT != 0 & clReleaseDeviceEXT != 0 & clCreateSubDevicesEXT != 0;
    }

    private static boolean isEXT_migrate_memobjectSupported() {
        return clEnqueueMigrateMemObjectEXT != 0;
    }

    private static boolean isKHR_gl_eventSupported() {
        return clCreateEventFromGLsyncKHR != 0;
    }

    private static boolean isKHR_gl_sharingSupported() {
        return clGetGLContextInfoKHR != 0;
    }

    private static boolean isKHR_icdSupported() {
        if (clIcdGetPlatformIDsKHR == 0) {
            // empty if block
        }
        return true;
    }

    private static boolean isKHR_subgroupsSupported() {
        return clGetKernelSubGroupInfoKHR != 0;
    }

    private static boolean isKHR_terminate_contextSupported() {
        return clTerminateContextKHR != 0;
    }

    static {
        clLogMessagesToSystemLogAPPLE = CL.getFunctionAddress("clLogMessagesToSystemLogAPPLE");
        clLogMessagesToStdoutAPPLE = CL.getFunctionAddress("clLogMessagesToStdoutAPPLE");
        clLogMessagesToStderrAPPLE = CL.getFunctionAddress("clLogMessagesToStderrAPPLE");
        clSetMemObjectDestructorAPPLE = CL.getFunctionAddress("clSetMemObjectDestructorAPPLE");
        clGetGLContextInfoAPPLE = CL.getFunctionAddress("clGetGLContextInfoAPPLE");
        clGetPlatformIDs = CL.getFunctionAddress("clGetPlatformIDs");
        clGetPlatformInfo = CL.getFunctionAddress("clGetPlatformInfo");
        clGetDeviceIDs = CL.getFunctionAddress("clGetDeviceIDs");
        clGetDeviceInfo = CL.getFunctionAddress("clGetDeviceInfo");
        clCreateContext = CL.getFunctionAddress("clCreateContext");
        clCreateContextFromType = CL.getFunctionAddress("clCreateContextFromType");
        clRetainContext = CL.getFunctionAddress("clRetainContext");
        clReleaseContext = CL.getFunctionAddress("clReleaseContext");
        clGetContextInfo = CL.getFunctionAddress("clGetContextInfo");
        clCreateCommandQueue = CL.getFunctionAddress("clCreateCommandQueue");
        clRetainCommandQueue = CL.getFunctionAddress("clRetainCommandQueue");
        clReleaseCommandQueue = CL.getFunctionAddress("clReleaseCommandQueue");
        clGetCommandQueueInfo = CL.getFunctionAddress("clGetCommandQueueInfo");
        clCreateBuffer = CL.getFunctionAddress("clCreateBuffer");
        clEnqueueReadBuffer = CL.getFunctionAddress("clEnqueueReadBuffer");
        clEnqueueWriteBuffer = CL.getFunctionAddress("clEnqueueWriteBuffer");
        clEnqueueCopyBuffer = CL.getFunctionAddress("clEnqueueCopyBuffer");
        clEnqueueMapBuffer = CL.getFunctionAddress("clEnqueueMapBuffer");
        clCreateImage2D = CL.getFunctionAddress("clCreateImage2D");
        clCreateImage3D = CL.getFunctionAddress("clCreateImage3D");
        clGetSupportedImageFormats = CL.getFunctionAddress("clGetSupportedImageFormats");
        clEnqueueReadImage = CL.getFunctionAddress("clEnqueueReadImage");
        clEnqueueWriteImage = CL.getFunctionAddress("clEnqueueWriteImage");
        clEnqueueCopyImage = CL.getFunctionAddress("clEnqueueCopyImage");
        clEnqueueCopyImageToBuffer = CL.getFunctionAddress("clEnqueueCopyImageToBuffer");
        clEnqueueCopyBufferToImage = CL.getFunctionAddress("clEnqueueCopyBufferToImage");
        clEnqueueMapImage = CL.getFunctionAddress("clEnqueueMapImage");
        clGetImageInfo = CL.getFunctionAddress("clGetImageInfo");
        clRetainMemObject = CL.getFunctionAddress("clRetainMemObject");
        clReleaseMemObject = CL.getFunctionAddress("clReleaseMemObject");
        clEnqueueUnmapMemObject = CL.getFunctionAddress("clEnqueueUnmapMemObject");
        clGetMemObjectInfo = CL.getFunctionAddress("clGetMemObjectInfo");
        clCreateSampler = CL.getFunctionAddress("clCreateSampler");
        clRetainSampler = CL.getFunctionAddress("clRetainSampler");
        clReleaseSampler = CL.getFunctionAddress("clReleaseSampler");
        clGetSamplerInfo = CL.getFunctionAddress("clGetSamplerInfo");
        clCreateProgramWithSource = CL.getFunctionAddress("clCreateProgramWithSource");
        clCreateProgramWithBinary = CL.getFunctionAddress("clCreateProgramWithBinary");
        clRetainProgram = CL.getFunctionAddress("clRetainProgram");
        clReleaseProgram = CL.getFunctionAddress("clReleaseProgram");
        clBuildProgram = CL.getFunctionAddress("clBuildProgram");
        clUnloadCompiler = CL.getFunctionAddress("clUnloadCompiler");
        clGetProgramInfo = CL.getFunctionAddress("clGetProgramInfo");
        clGetProgramBuildInfo = CL.getFunctionAddress("clGetProgramBuildInfo");
        clCreateKernel = CL.getFunctionAddress("clCreateKernel");
        clCreateKernelsInProgram = CL.getFunctionAddress("clCreateKernelsInProgram");
        clRetainKernel = CL.getFunctionAddress("clRetainKernel");
        clReleaseKernel = CL.getFunctionAddress("clReleaseKernel");
        clSetKernelArg = CL.getFunctionAddress("clSetKernelArg");
        clGetKernelInfo = CL.getFunctionAddress("clGetKernelInfo");
        clGetKernelWorkGroupInfo = CL.getFunctionAddress("clGetKernelWorkGroupInfo");
        clEnqueueNDRangeKernel = CL.getFunctionAddress("clEnqueueNDRangeKernel");
        clEnqueueTask = CL.getFunctionAddress("clEnqueueTask");
        clEnqueueNativeKernel = CL.getFunctionAddress("clEnqueueNativeKernel");
        clWaitForEvents = CL.getFunctionAddress("clWaitForEvents");
        clGetEventInfo = CL.getFunctionAddress("clGetEventInfo");
        clRetainEvent = CL.getFunctionAddress("clRetainEvent");
        clReleaseEvent = CL.getFunctionAddress("clReleaseEvent");
        clEnqueueMarker = CL.getFunctionAddress("clEnqueueMarker");
        clEnqueueBarrier = CL.getFunctionAddress("clEnqueueBarrier");
        clEnqueueWaitForEvents = CL.getFunctionAddress("clEnqueueWaitForEvents");
        clGetEventProfilingInfo = CL.getFunctionAddress("clGetEventProfilingInfo");
        clFlush = CL.getFunctionAddress("clFlush");
        clFinish = CL.getFunctionAddress("clFinish");
        clGetExtensionFunctionAddress = CL.getFunctionAddress("clGetExtensionFunctionAddress");
        clCreateFromGLBuffer = CL.getFunctionAddress("clCreateFromGLBuffer");
        clCreateFromGLTexture2D = CL.getFunctionAddress("clCreateFromGLTexture2D");
        clCreateFromGLTexture3D = CL.getFunctionAddress("clCreateFromGLTexture3D");
        clCreateFromGLRenderbuffer = CL.getFunctionAddress("clCreateFromGLRenderbuffer");
        clGetGLObjectInfo = CL.getFunctionAddress("clGetGLObjectInfo");
        clGetGLTextureInfo = CL.getFunctionAddress("clGetGLTextureInfo");
        clEnqueueAcquireGLObjects = CL.getFunctionAddress("clEnqueueAcquireGLObjects");
        clEnqueueReleaseGLObjects = CL.getFunctionAddress("clEnqueueReleaseGLObjects");
        clCreateSubBuffer = CL.getFunctionAddress("clCreateSubBuffer");
        clSetMemObjectDestructorCallback = CL.getFunctionAddress("clSetMemObjectDestructorCallback");
        clEnqueueReadBufferRect = CL.getFunctionAddress("clEnqueueReadBufferRect");
        clEnqueueWriteBufferRect = CL.getFunctionAddress("clEnqueueWriteBufferRect");
        clEnqueueCopyBufferRect = CL.getFunctionAddress("clEnqueueCopyBufferRect");
        clCreateUserEvent = CL.getFunctionAddress("clCreateUserEvent");
        clSetUserEventStatus = CL.getFunctionAddress("clSetUserEventStatus");
        clSetEventCallback = CL.getFunctionAddress("clSetEventCallback");
        clRetainDevice = CL.getFunctionAddress("clRetainDevice");
        clReleaseDevice = CL.getFunctionAddress("clReleaseDevice");
        clCreateSubDevices = CL.getFunctionAddress("clCreateSubDevices");
        clCreateImage = CL.getFunctionAddress("clCreateImage");
        clCreateProgramWithBuiltInKernels = CL.getFunctionAddress("clCreateProgramWithBuiltInKernels");
        clCompileProgram = CL.getFunctionAddress("clCompileProgram");
        clLinkProgram = CL.getFunctionAddress("clLinkProgram");
        clUnloadPlatformCompiler = CL.getFunctionAddress("clUnloadPlatformCompiler");
        clGetKernelArgInfo = CL.getFunctionAddress("clGetKernelArgInfo");
        clEnqueueFillBuffer = CL.getFunctionAddress("clEnqueueFillBuffer");
        clEnqueueFillImage = CL.getFunctionAddress("clEnqueueFillImage");
        clEnqueueMigrateMemObjects = CL.getFunctionAddress("clEnqueueMigrateMemObjects");
        clEnqueueMarkerWithWaitList = CL.getFunctionAddress("clEnqueueMarkerWithWaitList");
        clEnqueueBarrierWithWaitList = CL.getFunctionAddress("clEnqueueBarrierWithWaitList");
        clSetPrintfCallback = CL.getFunctionAddress("clSetPrintfCallback");
        clGetExtensionFunctionAddressForPlatform = CL.getFunctionAddress("clGetExtensionFunctionAddressForPlatform");
        clCreateFromGLTexture = CL.getFunctionAddress("clCreateFromGLTexture");
        clRetainDeviceEXT = CL.getFunctionAddress("clRetainDeviceEXT");
        clReleaseDeviceEXT = CL.getFunctionAddress("clReleaseDeviceEXT");
        clCreateSubDevicesEXT = CL.getFunctionAddress("clCreateSubDevicesEXT");
        clEnqueueMigrateMemObjectEXT = CL.getFunctionAddress("clEnqueueMigrateMemObjectEXT");
        clCreateEventFromGLsyncKHR = CL.getFunctionAddress("clCreateEventFromGLsyncKHR");
        clGetGLContextInfoKHR = CL.getFunctionAddress("clGetGLContextInfoKHR");
        clIcdGetPlatformIDsKHR = CL.getFunctionAddress("clIcdGetPlatformIDsKHR");
        clGetKernelSubGroupInfoKHR = CL.getFunctionAddress("clGetKernelSubGroupInfoKHR");
        clTerminateContextKHR = CL.getFunctionAddress("clTerminateContextKHR");
        CL_APPLE_ContextLoggingFunctions = CLCapabilities.isAPPLE_ContextLoggingFunctionsSupported();
        CL_APPLE_SetMemObjectDestructor = CLCapabilities.isAPPLE_SetMemObjectDestructorSupported();
        CL_APPLE_gl_sharing = CLCapabilities.isAPPLE_gl_sharingSupported();
        OpenCL10 = CLCapabilities.isCL10Supported();
        OpenCL10GL = CLCapabilities.isCL10GLSupported();
        OpenCL11 = CLCapabilities.isCL11Supported();
        OpenCL12 = CLCapabilities.isCL12Supported();
        OpenCL12GL = CLCapabilities.isCL12GLSupported();
        CL_EXT_device_fission = CLCapabilities.isEXT_device_fissionSupported();
        CL_EXT_migrate_memobject = CLCapabilities.isEXT_migrate_memobjectSupported();
        CL_KHR_gl_event = CLCapabilities.isKHR_gl_eventSupported();
        CL_KHR_gl_sharing = CLCapabilities.isKHR_gl_sharingSupported();
        CL_KHR_icd = CLCapabilities.isKHR_icdSupported();
        CL_KHR_subgroups = CLCapabilities.isKHR_subgroupsSupported();
        CL_KHR_terminate_context = CLCapabilities.isKHR_terminate_contextSupported();
    }
}

