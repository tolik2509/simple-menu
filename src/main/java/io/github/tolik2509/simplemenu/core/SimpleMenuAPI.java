package io.github.tolik2509.simplemenu.core;

import io.github.tolik2509.simplemenu.MenuListener;
import io.github.tolik2509.simplemenu.util.MenuLogger;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

public final class SimpleMenuAPI {
    private static JavaPlugin plugin;
    private static MenuListener menuListener;
    private static boolean initialized = false;

    private SimpleMenuAPI() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static void init(JavaPlugin plugin) {
        if (initialized) {
            MenuLogger.warning("error.already_initialized");
        } else {
            SimpleMenuAPI.plugin = plugin;
            MenuLogger.init(plugin.getLogger());
            menuListener = new MenuListener();
            plugin.getServer().getPluginManager().registerEvents(menuListener, plugin);
            initialized = true;
        }
    }

    public static void shutdown() {
        if (initialized) {
            initialized = false;
            MenuLogger.info("info.auto_shutdown");
            if (menuListener != null) {
                HandlerList.unregisterAll(menuListener);
                menuListener = null;
            }

            MenuLogger.init(null);
            plugin = null;
            initialized = false;
        }
    }

    public static JavaPlugin getPlugin() {
        if (!initialized) {
            throw new IllegalStateException("SimpleMenuAPI is not initialized! Call SimpleMenuAPI.init(this) in onEnable().");
        } else {
            return plugin;
        }
    }
}