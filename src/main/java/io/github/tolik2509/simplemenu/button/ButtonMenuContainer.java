package io.github.tolik2509.simplemenu.button;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.concurrent.ConcurrentHashMap;

public class ButtonMenuContainer {
    private final JavaPlugin plugin;
    private final File configFile;
    private final ConcurrentHashMap<String, Button> map = new ConcurrentHashMap<>();

    public ButtonMenuContainer(JavaPlugin plugin, File configFile) {
        this.plugin = plugin;
        this.configFile = configFile;
        this.reload();
    }

    public void reload() {
        FileConfiguration config = YamlConfiguration.loadConfiguration(this.configFile);
        this.loadButtons(config, "");
    }

    private void loadButtons(ConfigurationSection section, String currentPath) {
        if (section != null) {
            for (String key : section.getKeys(false)) {
                String fullPath = currentPath.isEmpty() ? key : currentPath + "." + key;
                ConfigurationSection subSection = section.getConfigurationSection(key);
                if (subSection != null) {
                    if (subSection.isString("material")) {
                        Button button = ButtonUtil.getButtonFromSection(this.plugin, subSection.getName(), section);
                        this.map.put(fullPath, button);
                    } else {
                        this.loadButtons(subSection, fullPath);
                    }
                }
            }

        }
    }

    public Button getButton(ButtonKey buttonKey) {
        return map.getOrDefault(buttonKey.getPath(), ButtonUtil.getButtonFromSection(this.plugin, "default", null));
    }

    public Button getButton(String path) {
        return map.getOrDefault(path, ButtonUtil.getButtonFromSection(this.plugin, "default", null));
    }
}