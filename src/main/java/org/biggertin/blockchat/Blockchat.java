package org.biggertin.blockchat;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;

public final class Blockchat extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this, this);
        getLogger().info("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! Blockchat enabled !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        event.getPlayer().sendMessage("You cannot chat!");
        event.setCancelled(true);
    }
}