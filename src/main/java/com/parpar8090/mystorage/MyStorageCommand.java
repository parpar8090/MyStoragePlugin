package com.parpar8090.mystorage;

import com.parpar8090.mystorage.settings.Settings;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MyStorageCommand extends Command {
    protected MyStorageCommand() {
        super(Settings.STORAGE_COMMAND_ALIASES.get(0));
        setAliases(Settings.STORAGE_COMMAND_ALIASES);
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED+"Only players can run this command!");
            return true;
        }
        Player player = (Player) sender;
        player.playSound(player.getLocation(), Sound.BLOCK_CHEST_OPEN, 1, 1);
        MyStoragePlugin.getInstance().openStorage((Player) sender);
        return true;
    }
}
