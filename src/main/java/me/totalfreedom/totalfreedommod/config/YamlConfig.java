package me.totalfreedom.totalfreedommod.config;

import me.totalfreedom.totalfreedommod.TotalFreedomMod;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class YamlConfig extends YamlConfiguration
{
    private final File file;

    public YamlConfig(TotalFreedomMod plugin, String name, boolean copyDefaults)
    {
        this.file = new File(plugin.getDataFolder(), name);

        if (copyDefaults)
        {
            options().copyDefaults(true);
        }

        if (!file.exists())
        {
            plugin.saveResource(name, false);
        }
        load();
    }

    public YamlConfig(TotalFreedomMod plugin, String name)
    {
        this(plugin, name, true);
    }

    public void load()
    {
        try
        {
            super.load(file);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void save()
    {
        try
        {
            super.save(file);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void clear()
    {
        for (String key : super.getKeys(false))
        {
            super.set(key, null);
        }
    }
}
