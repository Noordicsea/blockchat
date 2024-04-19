package org.biggertin.blockchat;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class Blockchat extends JavaPlugin implements Listener {

    private final Set<Player> authenticatedPlayers = new HashSet<>();
    private String password; // Changed to non-final since it will be loaded from config

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this, this);
        saveDefaultConfig(); // Save the default configuration
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
    }

    @Override
    public void onDisable() {
        // Save the "can chat" list to config on disable
        List<String> canChatUsernames = getConfig().getStringList("canChat");
        for (Player player : authenticatedPlayers) {
            if (!canChatUsernames.contains(player.getName())) {
                canChatUsernames.add(player.getName());
            }
        }
        getConfig().set("canChat", canChatUsernames);
        saveConfig();
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        if (!authenticatedPlayers.contains(player)) {
            String message = event.getMessage();
            if (message.equalsIgnoreCase(password) || message.toLowerCase().startsWith(password.toLowerCase() + " ")) {
                authenticatedPlayers.add(player);
                player.sendMessage("You have been authenticated! You can now chat freely.");
                // Add player to "can chat" list in config
                List<String> canChatUsernames = getConfig().getStringList("canChat");
                if (!canChatUsernames.contains(player.getName())) {
                    canChatUsernames.add(player.getName());
                    getConfig().set("canChat", canChatUsernames);
                    saveConfig();
                }
                if (message.length() > password.length() + 1) {
                    // Send everything after the password as a chat message.
                    Bukkit.getServer().broadcastMessage("<" + player.getDisplayName() + "> " + message.substring(password.length() + 1));
                }
                event.setCancelled(true); // Cancel the event to prevent the original message from being sent.
            } else {
                player.sendMessage("You cannot chat until you provide the correct password! Please type the correct password!");
                event.setCancelled(true);
            }
        }
    }
}