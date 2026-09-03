package io.github.tolik2509.simplemenu.button;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class ButtonUtil {
    private static final String BUTTON_KEY = "menu_button";
    public static Button getButtonFromSection(JavaPlugin plugin, String defName,
                                              @Nullable ConfigurationSection section){
        int slot = section!=null?section.getInt("slot", -1):-1;
        return new Button(
                ButtonUtil.getItemFromSection(plugin, defName, section), slot
        );
    }
    public static ItemStack getItemFromSection(JavaPlugin plugin, String defName,
                                               @Nullable ConfigurationSection section){
        if (section == null){
            ItemStack itemStack = new ItemStack(Material.IRON_BLOCK);
            itemStack.editMeta(itemMeta ->{
                itemMeta.displayName(Component.text(defName));
                itemMeta.getPersistentDataContainer().set(
                    new NamespacedKey(plugin, BUTTON_KEY),
                    PersistentDataType.BYTE,
                    (byte) 1);
            });
            return itemStack;
        }
        String base64 = section.getString("base64");

        Material material;
        ItemStack item;
        ItemMeta meta;
        if (base64 == null){
            material = Material.valueOf( section.getString("type", Material.QUARTZ_BLOCK.name() ).toUpperCase());
            item = new ItemStack(material);
            meta = item.getItemMeta();
        }else {
            item = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta skullMeta = (SkullMeta) item.getItemMeta();
            setTextureSkull(base64, skullMeta);
            meta = skullMeta;
        }
        String name = section.getString("name", defName);

        List<Component> lore = section.getStringList("lore").stream()
                .map(line-> LegacyComponentSerializer.legacySection().deserialize(line))
                .collect(Collectors.toList());
        meta.displayName(Component.text(name));
        if (lore.size()>0){
            meta.lore(lore);
        }
        ConfigurationSection enchantmentSection = section.getConfigurationSection("enchant");
        if (enchantmentSection != null){
            String enchantType = enchantmentSection.getString("name", "PROTECTION_ENVIRONMENTAL");
            int level = enchantmentSection.getInt("level", 1);
            boolean levelIgnore = enchantmentSection.getBoolean("level-ignore");
            Enchantment enchantment = Enchantment.getByKey(new NamespacedKey(
                    plugin, enchantType
            ));
            if (enchantment != null)
                meta.addEnchant(enchantment, level, levelIgnore);
            else
                plugin.getLogger().warning("Не найдено зачарование: "+enchantType);
        }


        //meta.addEnchant(Enchantment.LUCK, index, true);
        item.setItemMeta(meta);
        item.editMeta(itemMeta -> itemMeta.getPersistentDataContainer().set(
                new NamespacedKey(plugin, BUTTON_KEY),
                PersistentDataType.BYTE,
                (byte) 1)
        );
        return item;
    }
    private static void setTextureSkull(String base64, SkullMeta skullMeta) {
        if(base64.isEmpty()) return;
        PlayerProfile profile = Bukkit.createProfile(UUID.randomUUID());
        profile.getProperties().add(
                new ProfileProperty("textures", base64)
        );
        skullMeta.setPlayerProfile(profile);
    }
}
