package me.totalfreedom.totalfreedommod.util;

import me.totalfreedom.totalfreedommod.TotalFreedomMod;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class FSync
{

    public static void playerMsg(final Player player, final String message)
    {
        final TotalFreedomMod plugin = TotalFreedomMod.getPlugin();
        new BukkitRunnable()
        {

            @Override
            public void run()
            {
                FUtil.playerMsg(player, message);
            }

        }.runTask(plugin);
    }

    public static void playerMsg(final CommandSender sender, final String message)
    {
        final TotalFreedomMod plugin = TotalFreedomMod.getPlugin();
        new BukkitRunnable()
        {

            @Override
            public void run()
            {
                sender.sendMessage(message);
            }

        }.runTask(plugin);
    }

    public static void playerKick(final Player player, final String reason)
    {
        final TotalFreedomMod plugin = TotalFreedomMod.getPlugin();
        new BukkitRunnable()
        {

            @Override
            public void run()
            {
                player.kickPlayer(reason);
            }

        }.runTask(plugin);
    }

    public static void adminChatMessage(final CommandSender sender, final String message)
    {
        final TotalFreedomMod plugin = TotalFreedomMod.getPlugin();
        new BukkitRunnable()
        {

            @Override
            public void run()
            {
                plugin.cm.adminChat(sender, message);
            }

        }.runTask(plugin);
    }

    public static void seniorChatMessage(final CommandSender sender, final String message)
    {
        final TotalFreedomMod plugin = TotalFreedomMod.getPlugin();
        new BukkitRunnable()
        {

            @Override
            public void run()
            {
                plugin.cm.seniorChat(sender, message);
            }

        }.runTask(plugin);
    }

    public static void autoEject(final Player player, final String kickMessage)
    {
        final TotalFreedomMod plugin = TotalFreedomMod.getPlugin();
        new BukkitRunnable()
        {

            @Override
            public void run()
            {
                plugin.ae.autoEject(player, kickMessage);
            }

        }.runTask(plugin);
    }

    public static void bcastMsg(final String message, final ChatColor color)
    {
        final TotalFreedomMod plugin = TotalFreedomMod.getPlugin();
        new BukkitRunnable()
        {

            @Override
            public void run()
            {
                FUtil.bcastMsg(message, color);
            }

        }.runTask(plugin);
    }
}