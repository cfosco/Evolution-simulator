/*
 * Decompiled with CFR 0_122.
 */
package com.badlogic.gdx.physics.box2d.joints;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.JointDef;

public class MouseJointDef
extends JointDef {
    public final Vector2 target = new Vector2();
    public float maxForce = 0.0f;
    public float frequencyHz = 5.0f;
    public float dampingRatio = 0.7f;

    public MouseJointDef() {
        this.type = JointDef.JointType.MouseJoint;
    }
}

