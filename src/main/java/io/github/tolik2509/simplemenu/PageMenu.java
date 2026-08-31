package io.github.tolik2509.simplemenu;

import io.github.tolik2509.simplemenu.button.BaseButtonType;
import io.github.tolik2509.simplemenu.button.ButtonMenuContainer;
import io.github.tolik2509.simplemenu.model.MenuSlot;
import io.github.tolik2509.simplemenu.util.MenuLogger;
import io.github.tolik2509.simplemenu.util.SoundUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public abstract class PageMenu extends AbstractMenu {
    private record Cache(@NotNull List<MenuSlot> slots, long createAt) {
        public Cache(@NotNull List<MenuSlot> slots) {
            this(slots, System.currentTimeMillis());
        }

        private Cache(@NotNull List<MenuSlot> slots, long createAt) {
            this.slots = slots;
            this.createAt = createAt;
        }

        public boolean isOutdated(long outdatedTimeMillis) {
            return System.currentTimeMillis() - this.createAt > outdatedTimeMillis;
        }

        public @NotNull List<MenuSlot> slots() {
            return this.slots;
        }

        public long createAt() {
            return this.createAt;
        }
    }
    private final Map<Integer, Cache> pageCaches;
    private final ButtonMenuContainer buttonMenuContainer;
    protected int size;
    protected int startIndex;
    protected int maxSlots;
    protected int pageIndex;
    protected long cacheLifetimeMillis;
    protected Component title;

    public PageMenu(@Nullable AbstractMenu parent, @NotNull ButtonMenuContainer buttonMenuContainer) {
        this(parent, buttonMenuContainer, Component.empty(), 0);
    }

    public PageMenu(@Nullable AbstractMenu parent, @NotNull ButtonMenuContainer buttonMenuContainer, @NotNull TextComponent title) {
        this(parent, buttonMenuContainer, title, 0);
    }

    public PageMenu(@Nullable AbstractMenu parent, @NotNull ButtonMenuContainer buttonMenuContainer, @NotNull TextComponent title, int pageIndex) {
        super(parent, title);
        this.pageCaches = new HashMap<>();
        this.size = 54;
        this.startIndex = 9;
        this.maxSlots = 36;
        this.cacheLifetimeMillis = 5000L;
        this.title = Component.empty();
        this.buttonMenuContainer = buttonMenuContainer;
        this.pageIndex = pageIndex;
    }

    protected @NotNull Inventory createInventory() {
        return Bukkit.createInventory(this, this.size, this.getTitle().replaceText((b) -> {
            b.matchLiteral("{index}").replacement("" + (this.pageIndex + 1));
        }));
    }

    protected boolean drawMenu() {
        addButton(buttonMenuContainer.getButton(BaseButtonType.BACK), new Action((event) -> {
            if (pageIndex == 0) {
                back(event.getWhoClicked());
            } else {
                --pageIndex;
                SoundUtil.playResultSound(event.getWhoClicked(), true);
                drawMenu();
            }
            return true;
        }), 7);
        addButton(buttonMenuContainer.getButton(BaseButtonType.NEXT), new Action((event) -> {
            if (this.pageIndex < 100) {
                pageIndex++;
            }

            SoundUtil.playResultSound(event.getWhoClicked(), true);
            if (this.hasNext()) {
                this.next(event.getWhoClicked());
            } else {
                this.drawMenu();
            }

            return true;
        }), 8);
        this.acceptSlot((slots) -> {
            if (slots != null) {
                this.pageCaches.put(this.pageIndex, new Cache(slots));
                int invIndex = this.getStartIndex();
                int index = 0;

                while(index < slots.size() && invIndex < this.getMaxSlot()) {
                    if (this.getInventory().getItem(invIndex) != null) {
                        ++invIndex;
                    } else {
                        MenuSlot slot = slots.get(index++);
                        this.addButton(slot.buttonItem(), slot.action(), invIndex++);
                    }
                }

            }
        });
        return true;
    }


    private void acceptSlot(Consumer<List<MenuSlot>> accept) {
        Cache cache = pageCaches.get(pageIndex);

        if (cache != null) {
            // Передаем настраиваемое время из поля класса прямо в метод рекорда
            if (cache.isOutdated(cacheLifetimeMillis)) {
                pageCaches.remove(pageIndex);
                loadSlots(pageIndex, accept);
            } else {
                accept.accept(cache.slots()); // В record геттеры называются просто именем поля
            }
        } else {
            loadSlots(pageIndex, accept);
        }
    }

    public abstract void loadSlots(int pageIndex, Consumer<List<MenuSlot>> accept);

    public int getPageIndex() {
        return this.pageIndex;
    }

    public int getStartIndex() {
        return this.startIndex;
    }

    public int getMaxSlot() {
        return this.maxSlots;
    }

    protected int getOffset() {
        return Math.max(0, this.pageIndex) * this.getMaxSlot();
    }

    protected int getLimit() {
        return this.getOffset() + this.getMaxSlot();
    }

    /**
     * Устанавливает физический размер инвентаря.
     * Можно вызывать только ДО того, как инвентарь будет физически создан.
     */
    public PageMenu setSize(int size) {
        if (!hasInventory()) {
            MenuLogger.warning("error.cannot_change_size_runtime");
            return this;
        }
        if (size < 9 || size > 54 || size % 9 != 0) {
            MenuLogger.warning("error.invalid_inventory_size", size);
            this.size = 54;
            return this;
        }
        this.size = size;
        return this;
    }

    /**
     * Устанавливает количество элементов на одной странице.
     */
    public PageMenu setMaxSlots(int maxSlots) {
        if (maxSlots <= 0 || maxSlots > this.size) {
            MenuLogger.warning("error.invalid_max_slots", maxSlots, this.size);
            this.maxSlots = this.size - this.startIndex; // Дефолт: всё доступное место
            return this;
        }
        this.maxSlots = maxSlots;
        return this;
    }

    public PageMenu setPageIndex(int pageIndex) {
        this.pageIndex = Math.max(0, pageIndex);
        return this;
    }

    protected void clearCache() {
        this.pageCaches.clear();
    }

    /**
     * Устанавливает стартовый слот, с которого начнется выкладка элементов пагинации.
     */
    public PageMenu setStartIndex(int startIndex) {
        // Защита: индекс не может быть отрицательным или выходить за рамки максимального размера
        if (startIndex < 0 || startIndex >= this.size) {
            MenuLogger.warning("error.invalid_start_index", startIndex, this.size);
            this.startIndex = 0; // Сбрасываем на безопасный начальный слот
            return this;
        }
        this.startIndex = startIndex;
        return this;
    }

    /**
     * Устанавливает время жизни кэша страниц в миллисекундах.
     */
    public PageMenu setCacheLifetimeMillis(long cacheLifetimeMillis) {
        if (cacheLifetimeMillis < 0) {
            MenuLogger.warning("error.negative_cache_time", cacheLifetimeMillis);
            this.cacheLifetimeMillis = 5000L; // Дефолтные 5 секунд
            return this;
        }
        this.cacheLifetimeMillis = cacheLifetimeMillis;
        return this;
    }
}