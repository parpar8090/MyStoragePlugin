package com.parpar8090.mystorage;

import com.parpar8090.mystorage.settings.Settings;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class PersonalInventory {
    @Getter
    private final Inventory inv;

    public PersonalInventory(Player player, int rows) {
        String title = Settings.INVENTORY_TITLE;
        title = title.replace("{player_name}", player.getName());
        ChatColor.translateAlternateColorCodes('&', title);

        this.inv = Bukkit.createInventory(player, rows * 9, title);
    }

    public void open(Player player) {
        player.openInventory(inv);
    }
}
