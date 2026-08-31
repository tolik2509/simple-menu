package io.github.tolik2509.simplemenu.lock;

public interface LockChecker {
    boolean isLock();
    default void lock(){}
    default void unlock(){}
}
