package me.totalfreedom.totalfreedommod.command;

import me.totalfreedom.totalfreedommod.config.ConfigEntry;
import me.totalfreedom.totalfreedommod.player.FPlayer;
import me.totalfreedom.totalfreedommod.rank.Rank;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = Rank.OP, source = SourceType.ONLY_IN_GAME)
@CommandParameters(description = "Give yourself a prefix with random colors", usage = "/<command> <tag>", aliases = "tn")
public class Command_tagnyan extends FreedomCommand
{

    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (args.length < 1)
        {
            return false;
        }

        final StringBuilder tag = new StringBuilder();

        for (char c : ChatColor.stripColor(FUtil.colorize(StringUtils.join(args, " "))).toCharArray())
        {
            tag.append(FUtil.randomChatColor()).append(c);
        }

        String tagStr = tag.toString();

        int tagLimit = (plugin.al.isAdmin(sender) ? 30 : 20);

        final String rawTag = ChatColor.stripColor(tagStr).toLowerCase();

        if (rawTag.length() > tagLimit)
        {
            msg("That tag is too long (Max is " + tagLimit + " characters).");
            return true;
        }

        if (!plugin.al.isAdmin(sender))
        {
            for (String word : ConfigEntry.FORBIDDEN_WORDS.getStringList())
            {
                if (rawTag.contains(word))
                {
                    msg("That tag contains a forbidden word.");
                    return true;
                }
            }
        }

        final FPlayer data = plugin.pl.getPlayer(playerSender);
        data.setTag(tagStr);

        msg("Set tag to " + tag);
        return true;
    }
}