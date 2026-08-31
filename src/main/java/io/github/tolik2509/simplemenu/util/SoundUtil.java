package io.github.tolik2509.simplemenu.util;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;

public class SoundUtil {
    public SoundUtil() {

    }

    public static void playResultSound(HumanEntity human, boolean success) {
        if (human instanceof Player pl) {
            playResultSound(pl, pl.getLocation(), success);
        }

    }

    public static void playResultSound(Player player, boolean success) {
        playResultSound(player, player.getLocation(), success);
    }

    public static void playResultSound(Player player, Location location, boolean success) {
        Sound sound = success ? Sound.BLOCK_NOTE_BLOCK_HARP : Sound.BLOCK_NOTE_BLOCK_BASS;
        player.playSound(location, sound, 1.0F, 1.0F);
    }
}
