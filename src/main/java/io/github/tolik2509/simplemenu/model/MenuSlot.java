package io.github.tolik2509.simplemenu.model;

import io.github.tolik2509.simplemenu.Action;
import org.bukkit.inventory.ItemStack;

public record MenuSlot(Action action, ItemStack buttonItem) {
    public MenuSlot(Action action, ItemStack buttonItem) {
        this.action = action;
        this.buttonItem = buttonItem;
    }

    public Action action() {
        return this.action;
    }

    public ItemStack buttonItem() {
        return this.buttonItem;
    }
}
