package me.totalfreedom.totalfreedommod.fun;

import me.totalfreedom.totalfreedommod.FreedomService;
import me.totalfreedom.totalfreedommod.config.ConfigEntry;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Landminer extends FreedomService
{
    private final List<Landmine> landmines = new ArrayList<>();

    @Override
    public void onStart()
    {
        landmines.clear();
    }

    @Override
    public void onStop()
    {
    }

    public void add(Landmine landmine)
    {
        landmines.add(landmine);
    }

    public void remove(Landmine landmine)
    {
        landmines.remove(landmine);
    }

    @SuppressWarnings("deprecation")
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerMove(PlayerMoveEvent event)
    {
        if (!(ConfigEntry.LANDMINES_ENABLED.getBoolean() && ConfigEntry.ALLOW_EXPLOSIONS.getBoolean()))
        {
            return;
        }

        final Player player = event.getPlayer();

        final Iterator<Landmine> lit = landmines.iterator();
        while (lit.hasNext())
        {
            final Landmine landmine = lit.next();

            final Location location = landmine.location;
            if (location.getBlock().getType() != Material.TNT)
            {
                lit.remove();
                continue;
            }

            if (landmine.planter.equals(player))
            {
                break;
            }

            if (!player.getWorld().equals(location.getWorld()))
            {
                continue;
            }

            if (player.getLocation().distanceSquared(location) > (landmine.radius * landmine.radius))
            {
                break;
            }

            landmine.location.getBlock().setType(Material.AIR);

            final TNTPrimed tnt1 = location.getWorld().spawn(location, TNTPrimed.class);
            tnt1.setFuseTicks(40);
            tnt1.setPassenger(player);
            tnt1.setVelocity(new Vector(0.0, 2.0, 0.0));

            final TNTPrimed tnt2 = location.getWorld().spawn(player.getLocation(), TNTPrimed.class);
            tnt2.setFuseTicks(1);

            player.setGameMode(GameMode.SURVIVAL);
            lit.remove();
        }
    }

    public List<Landmine> getLandmines()
    {
        return landmines;
    }

    public static class Landmine
    {
        private final Location location;
        private final Player planter;
        private final double radius;

        public Landmine(Location location, Player player, double radius)
        {
            this.location = location;
            this.planter = player;
            this.radius = radius;
        }

        @Override
        public String toString()
        {
            return this.location.toString() + ", " + this.radius + ", " + this.planter.getName();
        }

        public Location getLocation()
        {
            return location;
        }

        public Player getPlanter()
        {
            return planter;
        }

        public double getRadius()
        {
            return radius;
        }
    }
}