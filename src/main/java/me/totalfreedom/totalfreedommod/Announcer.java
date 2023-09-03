package me.totalfreedom.totalfreedommod;

import com.google.common.collect.Lists;
import me.totalfreedom.totalfreedommod.config.ConfigEntry;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.Collections;
import java.util.List;

public class Announcer extends FreedomService
{

    private final List<String> announcements = Lists.newArrayList();
    private boolean enabled;
    private long interval;
    private String prefix;
    private BukkitTask announcer;

    @Override
    public void onStart()
    {
        enabled = ConfigEntry.ANNOUNCER_ENABLED.getBoolean();
        interval = ConfigEntry.ANNOUNCER_INTERVAL.getInteger() * 20L;
        prefix = FUtil.colorize(ConfigEntry.ANNOUNCER_PREFIX.getString());

        announcements.clear();
        for (Object announcement : ConfigEntry.ANNOUNCER_ANNOUNCEMENTS.getList())
        {
            announcements.add(FUtil.colorize((String)announcement));
        }

        if (!enabled)
        {
            return;
        }

        announcer = new BukkitRunnable()
        {
            private int current = 0;

            @Override
            public void run()
            {
                current++;

                if (current >= announcements.size())
                {
                    current = 0;
                }

                announce(announcements.get(current));
            }
        }.runTaskTimer(plugin, interval, interval);
    }

    @Override
    public void onStop()
    {
        if (announcer == null)
        {
            return;
        }

        FUtil.cancel(announcer);
        announcer = null;
    }

    public List<String> getAnnouncements()
    {
        return Collections.unmodifiableList(announcements);
    }

    public void announce(String message)
    {
        FUtil.bcastMsg(prefix + message);
    }

    public boolean isEnabled()
    {
        return enabled;
    }

    public long getInterval()
    {
        return interval;
    }

    public String getPrefix()
    {
        return prefix;
    }

    public BukkitTask getAnnouncer()
    {
        return announcer;
    }
}
