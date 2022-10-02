package com.parpar8090.mystorage.settings;

import com.parpar8090.mystorage.MyStoragePlugin;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;

public class Settings extends Config{
    private static FileConfiguration config;

    public Settings() {
        super("", "settings.yml", MyStoragePlugin.getInstance().getClass().getResourceAsStream("/settings.yml"));
        Settings.config = super.getConfig();
    }

    public static String INVENTORY_TITLE;
    public static List<String> STORAGE_COMMAND_ALIASES;

    private static void load(){
        INVENTORY_TITLE = config.getString("inventory-title");
        STORAGE_COMMAND_ALIASES = config.getStringList("storage-command-aliases");
    }

    public static void loadAll(){
        load();
    }
}
