package io.github.tolik2509.simplemenu.button;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.function.Consumer;
import java.util.function.UnaryOperator;

public record Button(ItemStack item, int slot) {
    public Button createModifiedButton(UnaryOperator<ItemStack> operator) {
        return createModifiedButton(operator, slot);
    }
    public Button createModifiedButton(UnaryOperator<ItemStack> operator, int slot) {
        ItemStack clone = cloneItem();
        return new Button(operator.apply(clone), slot);
    }
    public Button createModifiedButton(Consumer<ItemMeta> operator) {
        return createModifiedButton(operator, slot);
    }
    public Button createModifiedButton(Consumer<ItemMeta> operator, int slot) {
        ItemStack clone = cloneItem();
        clone.editMeta(operator);
        return new Button(clone, slot);
    }
    public ItemStack cloneItem() {
        return item.clone();
    }
    public static Button fromItem(ItemStack item){
        return new Button(item, -1);
    }
}