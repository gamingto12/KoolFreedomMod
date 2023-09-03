package me.totalfreedom.totalfreedommod.command;

import me.totalfreedom.totalfreedommod.admin.Admin;
import me.totalfreedom.totalfreedommod.rank.Rank;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.entity.ThrownPotion;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@CommandPermissions(level = Rank.SUPER, source = SourceType.ONLY_IN_GAME)
@CommandParameters(description = "Allows admins to see potions that are thrown.", usage = "/<command> <enable | on | disable | off> | history [player] <page>", aliases = "potspy")
public class Command_potionspy extends FreedomCommand
{

    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        Admin admin = plugin.al.getAdmin(playerSender);

        if (args.length <= 0)
        {
            setPotionSpyState(admin, !admin.getPotionSpy());
            return true;
        }
        else
        {
            String titleText = "&8&m------------------&r &ePotionSpy &8&m------------------&r";
            String validPageText = "Please specify a valid page number between 1 and %s.";
            String noPlayerRecord = "That player has not thrown any potions yet.";
            String splashedText = "&r%s splashed a potion at &eX: %s Y: %s Z: %s&r\nin the world '&e%s&r' about &e%s &rago%s.";
            String bottomText = "&8&m--------------------&r &e%s / %s &8&m--------------------&r";
            switch (args[0].toLowerCase())
            {
                case "enable":
                case "on":
                    setPotionSpyState(admin, true);
                    break;

                case "disable":
                case "off":
                    setPotionSpyState(admin, false);
                    break;

                case "history":
                    if (args.length == 3)
                    {
                        Player player = Bukkit.getPlayer(args[1]);
                        if (player == null)
                        {
                            msg(sender, "Please specify a valid player name.");
                            return true;
                        }

                        List<Map.Entry<ThrownPotion, Long>> thrownPotions = new ArrayList<>(plugin.mo.getPlayerThrownPotions(player)); // Make a copy of the list to avoid modifying the original.

                        List<String> potionThrowNotifications = new ArrayList<>();
                        int lastPage = (int)Math.ceil(thrownPotions.size() / 5.0);

                        if (thrownPotions.isEmpty())
                        {
                            msg(sender, noPlayerRecord);
                            return true;
                        }
                        if (!NumberUtils.isNumber(args[2]))
                        {
                            msg(sender, String.format(validPageText, lastPage));
                            return true;
                        }

                        Collections.reverse(thrownPotions);
                        int pageIndex = Integer.parseInt(args[2]);

                        for (Map.Entry<ThrownPotion, Long> potionEntry : thrownPotions)
                        {
                            ThrownPotion potion = potionEntry.getKey();
                            boolean trollPotions = plugin.mo.isTrollPotion(potion);

                            potionThrowNotifications.add(ChatColor.translateAlternateColorCodes('&', String.format(splashedText, player.getName(), potion.getLocation().getBlockX(),
                                    potion.getLocation().getBlockY(), potion.getLocation().getBlockZ(), potion.getWorld().getName(), getUnixTimeDifference(potionEntry.getValue(), System.currentTimeMillis()), trollPotions ? " &c(most likely troll potion/potions)" : "")));
                        }

                        List<String> page = FUtil.getPageFromList(potionThrowNotifications, 5, pageIndex - 1);
                        if (!page.isEmpty())
                        {
                            msg(sender, ChatColor.translateAlternateColorCodes('&', titleText));
                            for (String potionThrowNotification : page)
                            {
                                msg(sender, potionThrowNotification);
                            }
                        }
                        else
                        {
                            msg(sender, String.format(validPageText, lastPage));
                            return true;
                        }

                        msg(sender, ChatColor.translateAlternateColorCodes('&', String.format(bottomText, pageIndex, lastPage)));
                    }
                    else if (args.length == 2)
                    {
                        List<Map.Entry<ThrownPotion, Long>> thrownPotions = new ArrayList<>();
                        thrownPotions.addAll(plugin.mo.getAllThrownPotions()); // Make a copy of the list to avoid modifying the original.

                        List<String> potionThrowNotifications = new ArrayList<>();
                        int lastPage = (int)Math.ceil(thrownPotions.size() / 5.0);

                        if (thrownPotions.isEmpty())
                        {
                            if (Bukkit.getPlayer(args[1]) != null)
                            {
                                msg(sender, noPlayerRecord);
                            }
                            else
                            {
                                msg(sender, "No potions have been thrown yet.");
                            }
                            return true;
                        }
                        if (!NumberUtils.isNumber(args[1]))
                        {
                            msg(sender, String.format(validPageText, lastPage));
                            return true;
                        }

                        Collections.reverse(thrownPotions);
                        int pageIndex = Integer.parseInt(args[1]);

                        for (Map.Entry<ThrownPotion, Long> potionEntry : thrownPotions)
                        {
                            ThrownPotion potion = potionEntry.getKey();
                            Player player = (Player)potion.getShooter();
                            boolean trollPotions = plugin.mo.isTrollPotion(potion);

                            if (player != null)
                            {
                                potionThrowNotifications.add(ChatColor.translateAlternateColorCodes('&', String.format(splashedText, player.getName(), potion.getLocation().getBlockX(),
                                        potion.getLocation().getBlockY(), potion.getLocation().getBlockZ(), potion.getWorld().getName(), getUnixTimeDifference(potionEntry.getValue(), System.currentTimeMillis()), trollPotions ? " &c(most likely troll potion/potions)" : "")));
                            }
                        }

                        List<String> page = FUtil.getPageFromList(potionThrowNotifications, 5, pageIndex - 1);
                        if (!page.isEmpty())
                        {
                            msg(sender, ChatColor.translateAlternateColorCodes('&', titleText));
                            for (String potionThrowNotification : page)
                            {
                                msg(sender, potionThrowNotification);
                            }
                        }
                        else
                        {
                            msg(sender, String.format(validPageText, lastPage));
                            return true;
                        }

                        msg(sender, ChatColor.translateAlternateColorCodes('&', String.format(bottomText, pageIndex, lastPage)));
                    }
                    else
                    {
                        return false;
                    }
                    break;
                default:
                    return false;
            }
        }
        return true;
    }

    private void setPotionSpyState(Admin admin, boolean state)
    {
        admin.setPotionSpy(state);
        plugin.al.save(admin);
        plugin.al.updateTables();
        msg("PotionSpy is now " + (admin.getPotionSpy() ? "enabled." : "disabled."));
    }

    /**
     * Get the unix time difference in string format (1h, 30m, 15s).
     *
     * @param past The unix time at the start.
     * @param now  The current unix time.
     * @return A string that displays the time difference between the two unix time values.
     */
    private String getUnixTimeDifference(long past, long now)
    {
        long unix = now - past;
        long seconds = Math.round(unix / 1000.0);
        if (seconds < 60)
        {
            return seconds + "s";
        }
        else
        {
            long minutes = Math.round(seconds / 60.0);
            if (minutes < 60)
            {
                return minutes + "m";
            }
            else
            {
                long hours = Math.round(minutes / 60.0);
                if (hours < 24)
                {
                    return hours + "h";
                }
                else
                {
                    return Math.round(hours / 24.0) + "d";
                }
            }
        }
    }
}