package me.totalfreedom.totalfreedommod.command;

import io.papermc.lib.PaperLib;
import me.totalfreedom.totalfreedommod.rank.Rank;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = Rank.NON_OP, source = SourceType.ONLY_IN_GAME)
@CommandParameters(description = "Teleport to the spawn point for the current world, instead of the Essentials spawn point.", usage = "/<command>", aliases = "worldspawn,gotospawn")
public class Command_localspawn extends FreedomCommand
{
    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        PaperLib.teleportAsync(playerSender, playerSender.getWorld().getSpawnLocation());
        msg("Teleported to spawnpoint for world \"" + playerSender.getWorld().getName() + "\".");
        return true;
    }
}
