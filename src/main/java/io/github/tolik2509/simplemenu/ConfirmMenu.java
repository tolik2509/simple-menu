package io.github.tolik2509.simplemenu;

import io.github.tolik2509.simplemenu.button.BaseButtonType;
import io.github.tolik2509.simplemenu.button.ButtonMenuContainer;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

/**
 * Меню подтверждения. Использует встроенные слоты кнопок из контейнера,
 * либо дефолтные координаты convert(1, 3) и convert(1, 5).
 */
public class ConfirmMenu extends AbstractMenu {

    private final Consumer<HumanEntity> confirmAction;
    private final Component title;
    private final ButtonMenuContainer buttonContainer;
    private boolean autoBack = true;

    public ConfirmMenu(@Nullable AbstractMenu parent,
                       @NotNull Consumer<HumanEntity> confirmAction,
                       @NotNull Component title,
                       @NotNull ButtonMenuContainer buttonContainer) {
        super(parent);
        this.confirmAction = confirmAction;
        this.title = title;
        this.buttonContainer = buttonContainer;
    }

    @Override
    protected @NotNull Inventory createInventory() {
        return Bukkit.createInventory(this, 27, title);
    }

    public ConfirmMenu disableAutoBack() {
        this.autoBack = false;
        return this;
    }

    @Override
    protected boolean drawMenu() {
        // Кнопка подтверждения
        addButton(
                buttonContainer.getButton(BaseButtonType.CONFIRM),
                new Action(event -> {
                    HumanEntity player = event.getWhoClicked();
                    confirmAction.accept(player);
                    if (autoBack) {
                        back(player);
                    }
                    return true;
                }),
                convert(1, 3) // Дефолтный слот, если у самой кнопки приоритет/слот не задан
        );

        // Кнопка отказа
        addButton(
                buttonContainer.getButton(BaseButtonType.DENY),
                new Action(event -> {
                    back(event.getWhoClicked());
                    return true;
                }),
                convert(1, 5) // Дефолтный слот, если у самой кнопки приоритет/слот не задан
        );

        return true;
    }
}