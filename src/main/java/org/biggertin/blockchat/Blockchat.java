package org.biggertin.blockchat;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class Blockchat extends JavaPlugin implements Listener {

    private final Set<Player> authenticatedPlayers = new HashSet<>();
    private String password; // Changed to non-final since it will be loaded from config

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this, this);
        
        // Check if the config.yml file exists before saving the default config
        if (!new File(getDataFolder(), "config.yml").exists()) {
            getLogger().info("No config.yml found, creating a new one...");
            saveDefaultConfig();
        } else {
            getLogger().info("Loading existing config.yml...");
        }
        
        password = getConfig().getString("password", "password"); // Load password from config
        getLogger().info("Blockchat enabled!");

        // Load authenticated players from config
        List<String> canChatUsernames = getConfig().getStringList("canChat");
        for (String username : canChatUsernames) {
            Player player = Bukkit.getPlayerExact(username);
            if (player != null) {
                authenticatedPlayers.add(player);
            }
        }
        // Print the users that can chat in console on start up
        getLogger().info("Users that can chat: " + String.join(", ", canChatUsernames) + " | Chat password: " + password);
    }

    @Override
    public void onDisable() {
        List<String> canChatUsernames = getConfig().getStringList("canChat");
        for (Player player : authenticatedPlayers) {
            if (!canChatUsernames.contains(player.getName())) {
                canChatUsernames.add(player.getName());
            }
        }
        getConfig().set("canChat", canChatUsernames);
        saveConfig(); // Ensure changes are saved on disable
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        if (!authenticatedPlayers.contains(player)) {
            String message = event.getMessage();
            if (message.equalsIgnoreCase(password) || message.toLowerCase().startsWith(password.toLowerCase() + " ")) {
                authenticatedPlayers.add(player);
                player.sendMessage("You have been authenticated! You can now chat freely.");
                List<String> canChatUsernames = getConfig().getStringList("canChat");
                if (!canChatUsernames.contains(player.getName())) {
                    canChatUsernames.add(player.getName());
                    getConfig().set("canChat", canChatUsernames);
                    saveConfig(); // Save immediately after modification
                }
                if (message.length() > password.length() + 1) {
                    Bukkit.getServer().broadcastMessage("<" + player.getDisplayName() + "> " + message.substring(password.length() + 1));
                }
                event.setCancelled(true);
            } else {
                player.sendMessage("You cannot chat until you provide the correct password! Please type the correct password!");
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        List<String> canChatUsernames = getConfig().getStringList("canChat");
        if (canChatUsernames.contains(player.getName())) {
            authenticatedPlayers.add(player);
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("reloadconfig")) {
            reloadConfig();
            password = getConfig().getString("password", "password");
            sender.sendMessage(ChatColor.GREEN + "Configuration reloaded. New password: " + password);
            return true;
        }
        return false;
    }
}