package org.biggertin.blockchat;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.Set;

public final class Blockchat extends JavaPlugin implements Listener {

    private final Set<Player> authenticatedPlayers = new HashSet<>();
    private final String password = "password";

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this, this);
        getLogger().info("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! Blockchat enabled !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
    }

    @Override
    public void onDisable() {
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        if (!authenticatedPlayers.contains(player)) {
            if (event.getMessage().equalsIgnoreCase(password)) {
                authenticatedPlayers.add(player);
                player.sendMessage("You have been authenticated! You can now chat freely.");
            } else {
                player.sendMessage("You cannot chat until you provide the correct password! Please type the correct password!");
                event.setCancelled(true);
            }
        }
    }
}