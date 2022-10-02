package com.parpar8090.mystorage.settings;

import com.google.common.base.Charsets;
import com.parpar8090.mystorage.MyStoragePlugin;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;

public abstract class Config {
    @Getter
    private final File file;

    @Getter
    private FileConfiguration config;

    @Getter
    @Setter
    private String selectedPath = null;

    /**
     * @param path          The path to file starting from {@link JavaPlugin#getDataFolder()}
     * @param fileName      File name
     * @param defaultConfig The stream to the default config file inside the jar. Set to null if there are no defaults
     */
    public Config(String path, String fileName, InputStream defaultConfig) {
        InputStreamReader defaultConfigReader;
        if (defaultConfig != null)
            defaultConfigReader = new InputStreamReader(defaultConfig, Charsets.UTF_8);
        else defaultConfigReader = null;

        if (!fileName.endsWith(".yml"))
            fileName = fileName + ".yml";

        if (path.startsWith("\\"))
            path = path.substring(1);

        file = new File(MyStoragePlugin.getInstance().getDataFolder() + File.separator + path, fileName);

        createOrLoadFile(defaultConfigReader);
    }

    public void createOrLoadFile(InputStreamReader defaultConfigStream) {
        try {
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
            config = YamlConfiguration.loadConfiguration(new InputStreamReader(new FileInputStream(file), Charsets.UTF_8));
        } catch (Exception e) {
            Bukkit.getLogger().severe("Could not load config file!");
            e.printStackTrace();
            return;
        }

        //InputStream defaultConfigStream = OmegaWalls.class.getResourceAsStream(jarPath + File.separator + file.getName());
        //when null, it means there's no default
        if (defaultConfigStream != null) {
            FileConfiguration defaultConfig = YamlConfiguration.loadConfiguration(defaultConfigStream);

            config.setDefaults(defaultConfig);
        }

        config.options().copyHeader(true).copyDefaults(true);

        saveConfig();

        onLoad();
    }

    public void onLoad() {
    }

    public void saveConfig() {
        try {
            config.save(file);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }
}
