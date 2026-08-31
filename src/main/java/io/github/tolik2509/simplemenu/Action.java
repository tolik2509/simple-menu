package io.github.tolik2509.simplemenu;

import java.util.function.Predicate;

import io.github.tolik2509.simplemenu.lock.LockChecker;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.Nullable;

public record Action(Predicate<InventoryClickEvent> event, @Nullable LockChecker lockChecker) {
    public Action(Predicate<InventoryClickEvent> event) {
        this(event, null);
    }

    public Action(Predicate<InventoryClickEvent> event, @Nullable LockChecker lockChecker) {
        this.event = event;
        this.lockChecker = lockChecker;
    }

    public Predicate<InventoryClickEvent> event() {
        return this.event;
    }

    public @Nullable LockChecker lockChecker() {
        return this.lockChecker;
    }
}
