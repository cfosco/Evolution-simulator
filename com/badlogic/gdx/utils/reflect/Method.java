/*
 * Decompiled with CFR 0_122.
 */
package com.badlogic.gdx.utils.reflect;

import com.badlogic.gdx.utils.reflect.ReflectionException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

public final class Method {
    private final java.lang.reflect.Method method;

    Method(java.lang.reflect.Method method) {
        this.method = method;
    }

    public String getName() {
        return this.method.getName();
    }

    public Class getReturnType() {
        return this.method.getReturnType();
    }

    public Class[] getParameterTypes() {
        return this.method.getParameterTypes();
    }

    public Class getDeclaringClass() {
        return this.method.getDeclaringClass();
    }

    public boolean isAccessible() {
        return this.method.isAccessible();
    }

    public void setAccessible(boolean accessible) {
        this.method.setAccessible(accessible);
    }

    public boolean isAbstract() {
        return Modifier.isAbstract(this.method.getModifiers());
    }

    public boolean isDefaultAccess() {
        return !this.isPrivate() && !this.isProtected() && !this.isPublic();
    }

    public boolean isFinal() {
        return Modifier.isFinal(this.method.getModifiers());
    }

    public boolean isPrivate() {
        return Modifier.isPrivate(this.method.getModifiers());
    }

    public boolean isProtected() {
        return Modifier.isProtected(this.method.getModifiers());
    }

    public boolean isPublic() {
        return Modifier.isPublic(this.method.getModifiers());
    }

    public boolean isNative() {
        return Modifier.isNative(this.method.getModifiers());
    }

    public boolean isStatic() {
        return Modifier.isStatic(this.method.getModifiers());
    }

    public boolean isVarArgs() {
        return this.method.isVarArgs();
    }

    public /* varargs */ Object invoke(Object obj, Object ... args) throws ReflectionException {
        try {
            return this.method.invoke(obj, args);
        }
        catch (IllegalArgumentException e) {
            throw new ReflectionException("Illegal argument(s) supplied to method: " + this.getName(), e);
        }
        catch (IllegalAccessException e) {
            throw new ReflectionException("Illegal access to method: " + this.getName(), e);
        }
        catch (InvocationTargetException e) {
            throw new ReflectionException("Exception occurred in method: " + this.getName(), e);
        }
    }
}

