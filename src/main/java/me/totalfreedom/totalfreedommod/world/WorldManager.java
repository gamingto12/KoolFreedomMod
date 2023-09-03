package me.totalfreedom.totalfreedommod.world;

import io.papermc.lib.PaperLib;
import me.totalfreedom.totalfreedommod.FreedomService;
import me.totalfreedom.totalfreedommod.config.ConfigEntry;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.weather.ThunderChangeEvent;
import org.bukkit.event.weather.WeatherChangeEvent;

import static me.totalfreedom.totalfreedommod.util.FUtil.playerMsg;

public class WorldManager extends FreedomService
{

    public Flatlands flatlands;
    public AdminWorld adminworld;
    public MasterBuilderWorld masterBuilderWorld;

    public WorldManager()
    {
        this.flatlands = new Flatlands();
        this.adminworld = new AdminWorld();
        this.masterBuilderWorld = new MasterBuilderWorld();
    }

    @Override
    public void onStart()
    {
        flatlands.getWorld();
        adminworld.getWorld();
        masterBuilderWorld.getWorld();

        // Disable weather
        if (ConfigEntry.DISABLE_WEATHER.getBoolean())
        {
            for (World world : server.getWorlds())
            {
                world.setThundering(false);
                world.setStorm(false);
                world.setThunderDuration(0);
                world.setWeatherDuration(0);
            }
        }
    }

    @Override
    public void onStop()
    {
        flatlands.getWorld().save();
        adminworld.getWorld().save();
        masterBuilderWorld.getWorld().save();
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onThunderChange(ThunderChangeEvent event)
    {
        try
        {
            if (event.getWorld().equals(adminworld.getWorld()) && adminworld.getWeatherMode() != WorldWeather.OFF)
            {
                return;
            }
            else if (event.getWorld().equals(masterBuilderWorld.getWorld()) && masterBuilderWorld.getWeatherMode() != WorldWeather.OFF)
            {
                return;
            }
        }
        catch (Exception ignored)
        {
        }

        if (ConfigEntry.DISABLE_WEATHER.getBoolean() && event.toThunderState())
        {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onWeatherChange(WeatherChangeEvent event)
    {
        try
        {
            if (event.getWorld().equals(adminworld.getWorld()) && adminworld.getWeatherMode() != WorldWeather.OFF)
            {
                return;
            }
            else if (event.getWorld().equals(masterBuilderWorld.getWorld()) && masterBuilderWorld.getWeatherMode() != WorldWeather.OFF)
            {
                return;
            }
        }
        catch (Exception ignored)
        {
        }

        if (ConfigEntry.DISABLE_WEATHER.getBoolean() && event.toWeatherState())
        {
            event.setCancelled(true);
        }
    }

    public void gotoWorld(Player player, String targetWorld)
    {
        if (player == null)
        {
            return;
        }

        FUtil.fixCommandVoid(player);

        if (player.getWorld().getName().equalsIgnoreCase(targetWorld))
        {
            playerMsg(player, "Going to main world.", ChatColor.GRAY);
            PaperLib.teleportAsync(player, Bukkit.getWorlds().get(0).getSpawnLocation());
            return;
        }

        for (World world : Bukkit.getWorlds())
        {
            if (world.getName().equalsIgnoreCase(targetWorld))
            {
                playerMsg(player, "Going to world: " + targetWorld, ChatColor.GRAY);
                PaperLib.teleportAsync(player, world.getSpawnLocation());
                return;
            }
        }

        playerMsg(player, "World " + targetWorld + " not found.", ChatColor.GRAY);
    }
}