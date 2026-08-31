package io.github.tolik2509.simplemenu;

import io.github.tolik2509.simplemenu.button.Button;
import io.github.tolik2509.simplemenu.util.MenuLogger;
import java.util.HashMap;
import java.util.Objects;
import javax.annotation.Nullable;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryCloseEvent.Reason;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractMenu implements InventoryHolder {
    private static final int CLICK_TIMEOUT = 150;
    private final HashMap<Integer, Action> actions;
    @Nullable
    private final AbstractMenu parent;
    @Nullable
    private AbstractMenu next;
    @Nullable
    private Inventory inventory;
    private long lastClickTime;
    private int updateInterval;
    private Component title;

    public AbstractMenu() {
        this(null, Component.empty());
    }

    public AbstractMenu(@Nullable AbstractMenu parent) {
        this(parent, Component.empty());
    }

    public AbstractMenu(@NotNull Component title) {
        this(null, title);
    }

    public AbstractMenu(@Nullable AbstractMenu parent, @NotNull Component title) {
        this.actions = new HashMap<>();
        this.lastClickTime = System.currentTimeMillis();
        this.updateInterval = 5;
        this.parent = parent;
        this.title = Objects.requireNonNull(title, "Title cannot be null!");
    }

    public final synchronized void open(HumanEntity viewer) {
        inventory = createInventory();
        clearButtons();
        if (drawMenu()) {
            viewer.openInventory(inventory);
        }

    }

    protected abstract boolean drawMenu();

    public final void updateMenu() {
        if (inventory != null) {
            boolean isEmptyViews = inventory.getViewers().isEmpty();
            if (next != null) {
                next.updateMenu();
            }

            if (!isEmptyViews) {
                inventory.clear();
                drawMenu();
            }
        }
    }

    protected @NotNull Inventory createInventory() {
        return Bukkit.createInventory(this, 54, title);
    }

    public final void onInventoryClickEvent(InventoryClickEvent event) {
        if (System.currentTimeMillis() - lastClickTime < CLICK_TIMEOUT) {
            event.setCancelled(true);
        } else {
            Action buttonAction = actions.get(event.getSlot());
            lastClickTime = System.currentTimeMillis();
            if (buttonAction != null) {
                if (buttonAction.lockChecker() != null && buttonAction.lockChecker().isLock()) {
                    event.setCancelled(true);
                    return;
                }

                if (buttonAction.event().test(event)) {
                    event.setCancelled(true);
                }
            } else if (onClickDelegate(event)) {
                event.setCancelled(true);
            }

        }
    }

    protected boolean onClickDelegate(InventoryClickEvent event) {
        return true;
    }

    public void addButton(@NotNull Button button, @NotNull Action action, int slot) {
        int finalSlot = button.slot() != -1 ? button.slot() : slot;
        addButton(button.item(), action, finalSlot);
    }

    public void addButton(@NotNull Button button, @NotNull Action action) {
        Inventory inv = getInventory();
        int slot = button.slot();
        if (slot == -1) {
            boolean found = false;

            for(int i = 0; i < inv.getSize(); ++i) {
                if (inv.getItem(i) == null) {
                    addButton(button.item(), action, i);
                    found = true;
                    break;
                }
            }

            if (!found) {
                MenuLogger.warning("error.slot_out_of_bounds", "Auto-Slot", inv.getSize());
            }
        } else {
            addButton(button.item(), action, slot);
        }

    }

    public void addButton(@NotNull ItemStack item, @NotNull Action action, int slot) {
        Inventory inv = getInventory();
        if (slot >= 0 && slot < inv.getSize()) {
            actions.put(slot, action);
            inv.setItem(slot, item);
        } else {
            MenuLogger.warning("error.slot_out_of_bounds", slot, inv.getSize());
        }
    }

    public void openNewMenu(AbstractMenu nextMenu, HumanEntity viewer) {
        next = nextMenu;
        nextMenu.open(viewer);
    }

    public void back(HumanEntity viewer) {
        back(1, viewer);
    }

    public void back(int depth, HumanEntity viewer) {
        if (parent != null) {
            if (depth > 1) {
                parent.back(depth - 1, viewer);
            } else {
                parent.open(viewer);
                parent.next = null;
            }
        } else if (inventory != null) {
            inventory.close();
        }

    }

    protected boolean next(HumanEntity human) {
        if (next == null) {
            return false;
        } else {
            next.open(human);
            return true;
        }
    }

    public void backToRoot(HumanEntity viewer) {
        if (parent != null) {
            parent.backToRoot(viewer);
        } else {
            createInventory();
            open(viewer);
            next = null;
        }

    }

    public void nextToLastMenu(HumanEntity viewer) {
        if (next != null) {
            next.nextToLastMenu(viewer);
        } else {
            createInventory();
            open(viewer);
        }

    }

    public final void handleClose(InventoryCloseEvent event) {
        InventoryCloseEvent.Reason reason = event.getReason();
        if (reason != Reason.OPEN_NEW && reason != Reason.UNKNOWN) {
            onDestroy();
        }
    }

    protected void onDestroy() {}

    protected int convert(int trackIndex, int index) {
        if (trackIndex < 0 || index < 0 || index > 8) {
            MenuLogger.warning("error.coord_out_of_bounds", trackIndex, index);
            trackIndex = Math.max(0, trackIndex);
            index = Math.max(0, Math.min(8, index));
        }

        return trackIndex * 9 + index;
    }

    protected int[] convert(int slot) {
        if (slot < 0) {
            MenuLogger.warning("error.slot_negative", slot);
            slot = 0;
        }

        int trackIndex = slot / 9;
        int index = slot % 9;
        return new int[]{trackIndex, index};
    }

    protected void removeButton(int slot) {
        actions.remove(slot);
        getInventory().setItem(slot, null);
    }

    protected void filling(int track, int maxSlot, Material material) {
        if (inventory != null) {
            ItemStack item = new ItemStack(material);

            for(int slot = 0; slot < 9; ++slot) {
                int targetSlot = convert(track, slot);
                ItemStack currentItem = slot < maxSlot ? item : null;
                inventory.setItem(targetSlot, currentItem);
            }

        }
    }
    /**
     * Динамически меняет заголовок меню и сразу перерисовывает инвентарь для существа.
     * Используется для анимаций заголовка или тикающих таймеров.
     *
     * @param viewer Существо (игрок), у которого сейчас открыто это меню.
     * @param newTitle Новый компонент заголовка.
     */
    public final void updateTitle(@NotNull HumanEntity viewer, @NotNull Component newTitle) {
        // Меняем внутреннее состояние заголовка
        this.title = Objects.requireNonNull(newTitle, "Title cannot be null!");

        // Если инвентарь уже был открыт (существует в памяти)
        if (inventory != null) {
            // Пересоздаем инвентарь в Bukkit с новой геометрией и тайтлом
            inventory = createInventory();

            // Наполняем его кнопками заново (вызываем пользовательский метод отрисовки)
            drawMenu();

            // Открываем обновленное окно игроку.
            // Благодаря нашей проверке Reason.OPEN_NEW, цепочка событий не разорвется!
            viewer.openInventory(inventory);
        }
    }

    protected boolean hasNext() {
        return next != null;
    }

    protected void clearButtons() {
        this.actions.clear();
    }
    public AbstractMenu setUpdateInterval(int updateInterval) {
        this.updateInterval = updateInterval;
        return this;
    }

    public final @NotNull Inventory getInventory() {
        if (!hasInventory()) {
            inventory = createInventory();
        }

        return inventory;
    }

    public final Component getTitle() {
        return title;
    }


    public int getUpdateInterval() {
        return updateInterval;
    }


    public final boolean hasInventory() {
        return inventory != null;
    }
}