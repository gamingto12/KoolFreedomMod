package me.totalfreedom.totalfreedommod.command;

import me.totalfreedom.totalfreedommod.config.ConfigEntry;
import me.totalfreedom.totalfreedommod.rank.Rank;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@CommandPermissions(level = Rank.SUPER, source = SourceType.BOTH)
@CommandParameters(description = "Denies joining of operators and only allows admins to join.", usage = "/<command> [on | off]", aliases = "staffmode")
public class Command_adminmode extends FreedomCommand
{

    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (args.length != 1)
        {
            return false;
        }

        if (args[0].equalsIgnoreCase("off"))
        {
            ConfigEntry.ADMIN_ONLY_MODE.setBoolean(false);
            FUtil.adminAction(sender.getName(), "Opening the server to all players", true);
            return true;
        }
        else if (args[0].equalsIgnoreCase("on"))
        {
            ConfigEntry.ADMIN_ONLY_MODE.setBoolean(true);
            FUtil.adminAction(sender.getName(), "Closing the server to non-admins", true);
            for (Player player : server.getOnlinePlayers())
            {
                if (!isAdmin(player))
                {
                    player.kickPlayer("Server is now closed to non-admins.");
                }
            }
            return true;
        }

        return false;
    }

    @Override
    public List<String> getTabCompleteOptions(CommandSender sender, Command command, String alias, String[] args)
    {
        if (args.length == 1 && plugin.al.isAdmin(sender) && !(sender instanceof Player))
        {
            return Arrays.asList("on", "off");
        }

        return Collections.emptyList();
    }
}