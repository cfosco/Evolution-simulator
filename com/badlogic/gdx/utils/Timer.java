/*
 * Decompiled with CFR 0_122.
 */
package com.badlogic.gdx.utils;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.LifecycleListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;

public class Timer {
    static final Array<Timer> instances = new Array(1);
    static TimerThread thread;
    private static final int CANCELLED = -1;
    private static final int FOREVER = -2;
    static Timer instance;
    private final Array<Task> tasks = new Array(false, 8);

    public static Timer instance() {
        if (instance == null) {
            instance = new Timer();
        }
        return instance;
    }

    public Timer() {
        this.start();
    }

    public Task postTask(Task task) {
        return this.scheduleTask(task, 0.0f, 0.0f, 0);
    }

    public Task scheduleTask(Task task, float delaySeconds) {
        return this.scheduleTask(task, delaySeconds, 0.0f, 0);
    }

    public Task scheduleTask(Task task, float delaySeconds, float intervalSeconds) {
        return this.scheduleTask(task, delaySeconds, intervalSeconds, -2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Task scheduleTask(Task task, float delaySeconds, float intervalSeconds, int repeatCount) {
        if (task.repeatCount != -1) {
            throw new IllegalArgumentException("The same task may not be scheduled twice.");
        }
        task.executeTimeMillis = System.nanoTime() / 1000000 + (long)(delaySeconds * 1000.0f);
        task.intervalMillis = (long)(intervalSeconds * 1000.0f);
        task.repeatCount = repeatCount;
        Array<Task> array = this.tasks;
        synchronized (array) {
            this.tasks.add(task);
        }
        Timer.wake();
        return task;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void stop() {
        Array<Timer> array = instances;
        synchronized (array) {
            instances.removeValue(this, true);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void start() {
        Array<Timer> array = instances;
        synchronized (array) {
            if (instances.contains(this, true)) {
                return;
            }
            instances.add(this);
            if (thread == null) {
                thread = new TimerThread();
            }
            Timer.wake();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void clear() {
        Array<Task> array = this.tasks;
        synchronized (array) {
            int n = this.tasks.size;
            for (int i = 0; i < n; ++i) {
                this.tasks.get(i).cancel();
            }
            this.tasks.clear();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    long update(long timeMillis, long waitMillis) {
        Array<Task> array = this.tasks;
        synchronized (array) {
            int n = this.tasks.size;
            for (int i = 0; i < n; ++i) {
                Task task = this.tasks.get(i);
                if (task.executeTimeMillis > timeMillis) {
                    waitMillis = Math.min(waitMillis, task.executeTimeMillis - timeMillis);
                    continue;
                }
                if (task.repeatCount != -1) {
                    if (task.repeatCount == 0) {
                        task.repeatCount = -1;
                    }
                    Gdx.app.postRunnable(task);
                }
                if (task.repeatCount == -1) {
                    this.tasks.removeIndex(i);
                    --i;
                    --n;
                    continue;
                }
                task.executeTimeMillis = timeMillis + task.intervalMillis;
                waitMillis = Math.min(waitMillis, task.intervalMillis);
                if (task.repeatCount <= 0) continue;
                --task.repeatCount;
            }
        }
        return waitMillis;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void delay(long delayMillis) {
        Array<Task> array = this.tasks;
        synchronized (array) {
            int n = this.tasks.size;
            for (int i = 0; i < n; ++i) {
                Task task = this.tasks.get(i);
                task.executeTimeMillis += delayMillis;
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    static void wake() {
        Array<Timer> array = instances;
        synchronized (array) {
            instances.notifyAll();
        }
    }

    public static Task post(Task task) {
        return Timer.instance().postTask(task);
    }

    public static Task schedule(Task task, float delaySeconds) {
        return Timer.instance().scheduleTask(task, delaySeconds);
    }

    public static Task schedule(Task task, float delaySeconds, float intervalSeconds) {
        return Timer.instance().scheduleTask(task, delaySeconds, intervalSeconds);
    }

    public static Task schedule(Task task, float delaySeconds, float intervalSeconds, int repeatCount) {
        return Timer.instance().scheduleTask(task, delaySeconds, intervalSeconds, repeatCount);
    }

    static {
        instance = new Timer();
    }

    static class TimerThread
    implements Runnable,
    LifecycleListener {
        Application app;
        private long pauseMillis;

        public TimerThread() {
            Gdx.app.addLifecycleListener(this);
            this.resume();
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         * Enabled aggressive block sorting
         * Enabled unnecessary exception pruning
         * Enabled aggressive exception aggregation
         */
        @Override
        public void run() {
            do {
                Array<Timer> array = Timer.instances;
                synchronized (array) {
                    if (this.app != Gdx.app) {
                        return;
                    }
                    long timeMillis = System.nanoTime() / 1000000;
                    long waitMillis = 5000;
                    int n = Timer.instances.size;
                    for (int i = 0; i < n; ++i) {
                        try {
                            waitMillis = Timer.instances.get(i).update(timeMillis, waitMillis);
                            continue;
                        }
                        catch (Throwable ex) {
                            throw new GdxRuntimeException("Task failed: " + Timer.instances.get(i).getClass().getName(), ex);
                        }
                    }
                    if (this.app != Gdx.app) {
                        return;
                    }
                    try {
                        if (waitMillis > 0) {
                            Timer.instances.wait(waitMillis);
                        }
                    }
                    catch (InterruptedException ignored) {
                        // empty catch block
                    }
                }
            } while (true);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void resume() {
            long delayMillis = System.nanoTime() / 1000000 - this.pauseMillis;
            Array<Timer> array = Timer.instances;
            synchronized (array) {
                int n = Timer.instances.size;
                for (int i = 0; i < n; ++i) {
                    Timer.instances.get(i).delay(delayMillis);
                }
            }
            this.app = Gdx.app;
            new Thread((Runnable)this, "Timer").start();
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void pause() {
            this.pauseMillis = System.nanoTime() / 1000000;
            Array<Timer> array = Timer.instances;
            synchronized (array) {
                this.app = null;
                Timer.wake();
            }
            Timer.thread = null;
        }

        @Override
        public void dispose() {
            this.pause();
            Gdx.app.removeLifecycleListener(this);
            Timer.instances.clear();
            Timer.instance = null;
        }
    }

    public static abstract class Task
    implements Runnable {
        long executeTimeMillis;
        long intervalMillis;
        int repeatCount = -1;

        @Override
        public abstract void run();

        public void cancel() {
            this.executeTimeMillis = 0;
            this.repeatCount = -1;
        }

        public boolean isScheduled() {
            return this.repeatCount != -1;
        }

        public long getExecuteTimeMillis() {
            return this.executeTimeMillis;
        }
    }

}

