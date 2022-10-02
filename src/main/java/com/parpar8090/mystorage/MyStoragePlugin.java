package com.parpar8090.mystorage;

import com.parpar8090.mystorage.commands.MyStorageCommand;
import com.parpar8090.mystorage.settings.Settings;
import com.parpar8090.mystorage.sqlite.InventoryDB;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandMap;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.util.concurrent.CompletableFuture;

public final class MyStoragePlugin extends JavaPlugin implements Listener {
    @Getter
    private static MyStoragePlugin instance;
    @Getter
    private InventoryDB inventoryDB;

    public MyStoragePlugin(){
        instance = this;
        new Settings();
    }

    @Override
    public void onEnable() {
        Bukkit.getLogger().info("Enabling MyStorage plugin!");
        // Plugin startup logic
        Settings.loadAll();

        inventoryDB = new InventoryDB();

        getServer().getPluginManager().registerEvents(this, this);

        loadCommands();
    }

    private void loadCommands() {
        try {
            final Field commandMap = Bukkit.getServer().getClass().getDeclaredField("commandMap");

            commandMap.setAccessible(true);
            CommandMap map = (CommandMap) commandMap.get(Bukkit.getServer());

            map.register(Settings.STORAGE_COMMAND_ALIASES.get(0), new MyStorageCommand());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        Bukkit.getLogger().info("Disabling MyStorage plugin!");
        inventoryDB.close();
    }

    public void openStorage(Player player){
        PersonalInventory inv = inventoryDB.cachedInventories.get(player.getUniqueId());
        if(inv == null) return;
        inv.open(player);
    }

    @Deprecated
    public void viewStorage(String playerName, Player viewer){
        PersonalInventory inv = inventoryDB.cachedInventories.get(Bukkit.getOfflinePlayer(playerName).getUniqueId());
        if(inv == null) return;
        inv.open(viewer);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e){
        CompletableFuture.runAsync(()-> inventoryDB.loadInventory(e.getPlayer()));
    }

    @EventHandler
    public void onCloseInventory(InventoryCloseEvent e){
        PersonalInventory personalInventory = inventoryDB.cachedInventories.get(e.getPlayer().getUniqueId());
        if(!e.getInventory().equals(personalInventory.getInv())) return;
        e.getPlayer().sendMessage(ChatColor.GREEN+"Saving your inventory");
        CompletableFuture.runAsync(()-> inventoryDB.saveInventory(e.getPlayer(), personalInventory));
    }
}
