package io.github.tolik2509.simplemenu.lock;

public class LockCheckerDef implements LockChecker{
    private volatile boolean lock;
    public LockCheckerDef() {this.lock = false;}

    public synchronized void lock() {
        this.lock = true;
    }
    public synchronized void unlock() {
        this.lock = false;
    }
    @Override
    public boolean isLock() {
        return lock;
    }
}
