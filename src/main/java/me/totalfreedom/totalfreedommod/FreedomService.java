package me.totalfreedom.totalfreedommod;

import me.totalfreedom.totalfreedommod.util.FLog;
import org.bukkit.Server;
import org.bukkit.event.Listener;

import java.util.logging.Logger;

public abstract class FreedomService implements Listener
{
    protected final TotalFreedomMod plugin;
    protected final Server server;
    protected final Logger logger;

    public FreedomService()
    {
        plugin = TotalFreedomMod.getPlugin();
        server = plugin.getServer();
        logger = FLog.getPluginLogger();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        plugin.fsh.add(this);
    }

    public abstract void onStart();

    public abstract void onStop();
}
