package io.github.tolik2509.simplemenu.util;

import io.github.tolik2509.simplemenu.AbstractMenu;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MenuTickManager implements Runnable {
    private final Map<UUID, AbstractMenu> activeMenus = new HashMap<>();
    public void register(Player player, AbstractMenu menu) {
        this.activeMenus.put(player.getUniqueId(), menu);
        menu.open(player);
    }

    public void run() {
        this.activeMenus.entrySet().removeIf(
                (entry) -> !entry.getValue().hasInventory()
                        || entry.getValue().getInventory().getViewers().isEmpty()
        );
        int currentTick = Bukkit.getCurrentTick();

        for (AbstractMenu menu : new ArrayList<>(activeMenus.values())) {
            if (currentTick % menu.getUpdateInterval() == 0) {
                menu.updateMenu();
            }
        }

    }
}
