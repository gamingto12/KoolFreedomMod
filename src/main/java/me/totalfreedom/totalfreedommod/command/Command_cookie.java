package me.totalfreedom.totalfreedommod.command;

import me.totalfreedom.totalfreedommod.rank.Rank;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

@CommandPermissions(level = Rank.SUPER, source = SourceType.BOTH)
@CommandParameters(description = "For those who have no friends - gives a cookie to everyone on the server.", usage = "/<command>")
public class Command_cookie extends FreedomCommand
{
    public static final String COOKIE_LYRICS = "Imagine that you have zero cookies and you split them evenly among zero friends. How many cookies does each person get? See? It doesn't make sense. And Cookie Monster is sad that there are no cookies, and you are sad that you have no friends.";
    public static final String LORE = "But, you can have a cookie anyways,\nsince you are sad you are have no friends.";

    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        final StringBuilder output = new StringBuilder();

        for (final String word : COOKIE_LYRICS.split(" "))
        {
            output.append(FUtil.randomChatColor()).append(word).append(" ");
        }

        final StringBuilder lore = new StringBuilder();

        for (final String word : LORE.split(" "))
        {
            lore.append(FUtil.randomChatColor()).append(word).append(" ");
        }

        final ItemStack heldItem = new ItemStack(Material.COOKIE);
        final ItemMeta heldItemMeta = heldItem.getItemMeta();
        String name = ChatColor.DARK_RED + "C" +
                ChatColor.GOLD + "o" +
                ChatColor.YELLOW + "o" +
                ChatColor.DARK_GREEN + "k" +
                ChatColor.DARK_BLUE + "i" +
                ChatColor.DARK_PURPLE + "e";
        assert heldItemMeta != null;
        heldItemMeta.setDisplayName(name);
        heldItemMeta.setLore(Arrays.asList(lore.toString().split("\n")));
        heldItem.setItemMeta(heldItemMeta);

        for (final Player player : server.getOnlinePlayers())
        {
            final int firstEmpty = player.getInventory().firstEmpty();
            if (firstEmpty >= 0)
            {
                player.getInventory().setItem(firstEmpty, heldItem);
            }
        }

        FUtil.bcastMsg(output.toString());
        return true;
    }
}
