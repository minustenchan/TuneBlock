package com.github.minustenchan.tuneblock;

import com.github.minustenchan.tuneblock.listeners.TuneBlockListeners;
import com.jeff_media.customblockdata.CustomBlockData;
import org.bukkit.event.HandlerList;
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
        getServer().getPluginManager().registerEvents(new TuneBlockListeners(), this);
        CustomBlockData.registerListener(this);
    }

    @Override
    public void onDisable() {
        HandlerList.unregisterAll(this);
    }
}
