package io.github.tolik2509.simplemenu;

import io.github.tolik2509.simplemenu.core.SimpleMenuAPI;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.server.PluginDisableEvent;

public class MenuListener implements Listener {
    public MenuListener() {
    }

    @EventHandler
    public final void onClick(InventoryClickEvent event) {
        if (event.getClickedInventory().getHolder() instanceof AbstractMenu ab) {
            ab.onInventoryClickEvent(event);
        }

    }

    @EventHandler
    public final void onClose(InventoryCloseEvent event) {
        if (event.getInventory().getHolder() instanceof AbstractMenu ab) {
            ab.handleClose(event);
        }

    }

    @EventHandler
    public void onPluginDisable(PluginDisableEvent event) {
        if (event.getPlugin().equals(SimpleMenuAPI.getPlugin())) {
            SimpleMenuAPI.shutdown();
        }

    }
}
