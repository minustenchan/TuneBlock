package com.github.minustenchan.tuneblock;

import com.github.minustenchan.tuneblock.listeners.TuneBlockListeners;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class TuneBlockPlugin extends JavaPlugin {

    private static JavaPlugin plugin;

    public TuneBlockPlugin() {
        plugin = this;
    }

    public static JavaPlugin get() {
        return plugin;
    }

    @Override
    public void onEnable() {
        PluginManager pluginManager = getServer().getPluginManager();
        if (pluginManager.isPluginEnabled("Oraxen")) {
            pluginManager.registerEvents(new TuneBlockListeners(), this);
        } else {
            getLogger().severe("Oraxen needs to be enabled in order for this plugin to work");
            pluginManager.disablePlugin(this);
        }
    }

    @Override
    public void onDisable() {
        HandlerList.unregisterAll(this);
    }
}
