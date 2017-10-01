/*
 * Decompiled with CFR 0_122.
 */
package com.badlogic.gdx.graphics.g3d.utils;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.model.Animation;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.graphics.g3d.model.NodeAnimation;
import com.badlogic.gdx.graphics.g3d.model.NodeKeyframe;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.Pool;

public class BaseAnimationController {
    private final Pool<Transform> transformPool;
    private static final ObjectMap<Node, Transform> transforms = new ObjectMap();
    private boolean applying;
    public final ModelInstance target;
    private static final Transform tmpT = new Transform();

    public BaseAnimationController(ModelInstance target) {
        this.transformPool = new Pool<Transform>(){

            @Override
            protected Transform newObject() {
                return new Transform();
            }
        };
        this.applying = false;
        this.target = target;
    }

    protected void begin() {
        if (this.applying) {
            throw new GdxRuntimeException("You must call end() after each call to being()");
        }
        this.applying = true;
    }

    protected void apply(Animation animation, float time, float weight) {
        if (!this.applying) {
            throw new GdxRuntimeException("You must call begin() before adding an animation");
        }
        BaseAnimationController.applyAnimation(transforms, this.transformPool, weight, animation, time);
    }

    protected void end() {
        if (!this.applying) {
            throw new GdxRuntimeException("You must call begin() first");
        }
        for (ObjectMap.Entry entry : transforms.entries()) {
            ((Transform)entry.value).toMatrix4(((Node)entry.key).localTransform);
            this.transformPool.free((Transform)entry.value);
        }
        transforms.clear();
        this.target.calculateTransforms();
        this.applying = false;
    }

    protected void applyAnimation(Animation animation, float time) {
        if (this.applying) {
            throw new GdxRuntimeException("Call end() first");
        }
        BaseAnimationController.applyAnimation(null, null, 1.0f, animation, time);
        this.target.calculateTransforms();
    }

    protected void applyAnimations(Animation anim1, float time1, Animation anim2, float time2, float weight) {
        if (anim2 == null || weight == 0.0f) {
            this.applyAnimation(anim1, time1);
        } else if (anim1 == null || weight == 1.0f) {
            this.applyAnimation(anim2, time2);
        } else {
            if (this.applying) {
                throw new GdxRuntimeException("Call end() first");
            }
            this.begin();
            this.apply(anim1, time1, 1.0f);
            this.apply(anim2, time2, weight);
            this.end();
        }
    }

    protected static void applyAnimation(ObjectMap<Node, Transform> out, Pool<Transform> pool, float alpha, Animation animation, float time) {
        if (out != null) {
            for (Node node : out.keys()) {
                node.isAnimated = false;
            }
        }
        for (NodeAnimation nodeAnim : animation.nodeAnimations) {
            Node node = nodeAnim.node;
            node.isAnimated = true;
            int n = nodeAnim.keyframes.size - 1;
            int first = 0;
            int second = -1;
            for (int i = 0; i < n; ++i) {
                if (time < nodeAnim.keyframes.get((int)i).keytime || time > nodeAnim.keyframes.get((int)(i + 1)).keytime) continue;
                first = i;
                second = i + 1;
                break;
            }
            Transform transform = tmpT;
            NodeKeyframe firstKeyframe = nodeAnim.keyframes.get(first);
            transform.set(firstKeyframe.translation, firstKeyframe.rotation, firstKeyframe.scale);
            if (second > first) {
                NodeKeyframe secondKeyframe = nodeAnim.keyframes.get(second);
                float t = (time - firstKeyframe.keytime) / (secondKeyframe.keytime - firstKeyframe.keytime);
                transform.lerp(secondKeyframe.translation, secondKeyframe.rotation, secondKeyframe.scale, t);
            }
            if (out == null) {
                transform.toMatrix4(node.localTransform);
                continue;
            }
            Transform t = out.get(node, null);
            if (t != null) {
                if (alpha > 0.999999f) {
                    t.set(transform);
                    continue;
                }
                t.lerp(transform, alpha);
                continue;
            }
            if (alpha > 0.999999f) {
                out.put(node, pool.obtain().set(transform));
                continue;
            }
            out.put(node, pool.obtain().set(node.translation, node.rotation, node.scale).lerp(transform, alpha));
        }
        if (out != null) {
            for (ObjectMap.Entry e : out.entries()) {
                if (((Node)e.key).isAnimated) continue;
                ((Node)e.key).isAnimated = true;
                ((Transform)e.value).lerp(((Node)e.key).translation, ((Node)e.key).rotation, ((Node)e.key).scale, alpha);
            }
        }
    }

    protected void removeAnimation(Animation animation) {
        for (NodeAnimation nodeAnim : animation.nodeAnimations) {
            nodeAnim.node.isAnimated = false;
        }
    }

    public static final class Transform
    implements Pool.Poolable {
        public final Vector3 translation = new Vector3();
        public final Quaternion rotation = new Quaternion();
        public final Vector3 scale = new Vector3(1.0f, 1.0f, 1.0f);

        public Transform idt() {
            this.translation.set(0.0f, 0.0f, 0.0f);
            this.rotation.idt();
            this.scale.set(1.0f, 1.0f, 1.0f);
            return this;
        }

        public Transform set(Vector3 t, Quaternion r, Vector3 s) {
            this.translation.set(t);
            this.rotation.set(r);
            this.scale.set(s);
            return this;
        }

        public Transform set(Transform other) {
            return this.set(other.translation, other.rotation, other.scale);
        }

        public Transform lerp(Transform target, float alpha) {
            return this.lerp(target.translation, target.rotation, target.scale, alpha);
        }

        public Transform lerp(Vector3 targetT, Quaternion targetR, Vector3 targetS, float alpha) {
            this.translation.lerp(targetT, alpha);
            this.rotation.slerp(targetR, alpha);
            this.scale.lerp(targetS, alpha);
            return this;
        }

        public Matrix4 toMatrix4(Matrix4 out) {
            return out.set(this.translation, this.rotation, this.scale);
        }

        @Override
        public void reset() {
            this.idt();
        }
    }

}

