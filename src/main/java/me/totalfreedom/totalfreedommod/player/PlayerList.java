package me.totalfreedom.totalfreedommod.player;

import com.google.common.collect.Maps;
import me.totalfreedom.totalfreedommod.FreedomService;
import me.totalfreedom.totalfreedommod.admin.Admin;
import me.totalfreedom.totalfreedommod.util.FLog;
import me.totalfreedom.totalfreedommod.util.FUtil;
import me.totalfreedom.totalfreedommod.rank.Rank;
import me.totalfreedom.totalfreedommod.config.ConfigEntry;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class PlayerList extends FreedomService
{

    public final Map<String, FPlayer> playerMap = Maps.newHashMap(); // ip,dataMap
    public final Map<String, PlayerData> dataMap = Maps.newHashMap(); // username, data

    @Override
    public void onStart()
    {
        dataMap.clear();
        loadMasterBuilders();
    }

    @Override
    public void onStop()
    {
    }

    public FPlayer getPlayerSync(Player player)
    {
        synchronized (playerMap)
        {
            return getPlayer(player);
        }
    }

    public void loadMasterBuilders()
    {
        ResultSet resultSet = plugin.sql.getMasterBuilders();

        if (resultSet == null)
        {
            return;
        }

        try
        {
            while (resultSet.next())
            {
                PlayerData playerData = load(resultSet);
                dataMap.put(playerData.getName(), playerData);
            }
        }
        catch (SQLException e)
        {
            FLog.severe("Failed to parse master builders: " + e.getMessage());
        }
    }

    public String getIp(OfflinePlayer player)
    {
        if (player.isOnline())
        {
            return FUtil.getIp(Objects.requireNonNull(player.getPlayer()));
        }

        final PlayerData entry = getData(player.getName());

        return (entry == null ? null : entry.getIps().iterator().next());
    }

    public List<String> getMasterBuilderNames()
    {
        List<String> masterBuilders = new ArrayList<>();
        for (PlayerData playerData : plugin.pl.dataMap.values())
        {
            if (playerData.isMasterBuilder())
            {
                masterBuilders.add(playerData.getName());
            }
        }
        return masterBuilders;
    }

    public boolean canManageMasterBuilders(String name)
    {
        PlayerData data = getData(name);

        return (!ConfigEntry.HOST_SENDER_NAMES.getStringList().contains(name.toLowerCase()) && data != null && !ConfigEntry.SERVER_OWNERS.getStringList().contains(data.getName()))
                && !ConfigEntry.SERVER_EXECUTIVES.getStringList().contains(data.getName())
                && !isTelnetMasterBuilder(data)
                && !ConfigEntry.HOST_SENDER_NAMES.getStringList().contains(name.toLowerCase());
    }

    public boolean isTelnetMasterBuilder(PlayerData playerData)
    {
        Admin admin = plugin.al.getEntryByName(playerData.getName());
        return admin != null && admin.getRank().isAtLeast(Rank.SUPER) && playerData.isMasterBuilder();
    }

    // May not return null
    public FPlayer getPlayer(Player player)
    {
        FPlayer tPlayer = playerMap.get(FUtil.getIp(player));
        if (tPlayer != null)
        {
            return tPlayer;
        }

        tPlayer = new FPlayer(plugin, player);
        playerMap.put(FUtil.getIp(player), tPlayer);

        return tPlayer;
    }

    public PlayerData loadByName(String name)
    {
        return load(plugin.sql.getPlayerByName(name));
    }

    public PlayerData loadByIp(String ip)
    {
        return load(plugin.sql.getPlayerByIp(ip));
    }

    public PlayerData load(ResultSet resultSet)
    {
        if (resultSet == null)
        {
            return null;
        }
        return new PlayerData(resultSet);
    }

    public Boolean isPlayerImpostor(Player player)
    {
        PlayerData playerData = getData(player);
        return plugin.dc.enabled
                && !plugin.al.isAdmin(player)
                && (playerData.hasVerification())
                && !playerData.getIps().contains(FUtil.getIp(player));
    }

    public boolean IsImpostor(Player player)
    {
        return isPlayerImpostor(player) || plugin.al.isAdminImpostor(player);
    }

    public void verify(Player player, String backupCode)
    {
        PlayerData playerData = getData(player);
        if (backupCode != null)
        {
            playerData.removeBackupCode(backupCode);
        }

        playerData.addIp(FUtil.getIp(player));
        save(playerData);

        if (plugin.al.isAdminImpostor(player))
        {
            Admin admin = plugin.al.getEntryByName(player.getName());
            admin.setLastLogin(new Date());
            admin.addIp(FUtil.getIp(player));
            plugin.al.updateTables();
            plugin.al.save(admin);
        }

        plugin.rm.updateDisplay(player);
    }

    public void syncIps(Admin admin)
    {
        PlayerData playerData = getData(admin.getName());
        playerData.clearIps();
        playerData.addIps(admin.getIps());
        plugin.pl.save(playerData);
    }

    public void syncIps(PlayerData playerData)
    {
        Admin admin = plugin.al.getEntryByName(playerData.getName());

        if (admin != null && admin.isActive())
        {
            admin.clearIPs();
            admin.addIps(playerData.getIps());
            plugin.al.updateTables();
            plugin.al.save(admin);
        }
    }


    public void save(PlayerData player)
    {
        try
        {
            ResultSet currentSave = plugin.sql.getPlayerByName(player.getName());
            for (Map.Entry<String, Object> entry : player.toSQLStorable().entrySet())
            {
                Object storedValue = plugin.sql.getValue(currentSave, entry.getKey(), entry.getValue());
                if (storedValue != null && !storedValue.equals(entry.getValue()) || storedValue == null && entry.getValue() != null || entry.getValue() == null)
                {
                    plugin.sql.setPlayerValue(player, entry.getKey(), entry.getValue());
                }
            }
        }
        catch (SQLException e)
        {
            FLog.severe("Failed to save player: " + e.getMessage());
        }
    }

    public PlayerData getData(Player player)
    {
        // Check for existing data
        PlayerData playerData = dataMap.get(player.getName());
        if (playerData != null)
        {
            return playerData;
        }

        // Load data
        playerData = loadByName(player.getName());

        if (playerData == null)
        {
            playerData = loadByIp(FUtil.getIp(player));
            if (playerData != null)
            {
                plugin.sql.updatePlayerName(playerData.getName(), player.getName());
                playerData.setName(player.getName());
                dataMap.put(player.getName(), playerData);
                return playerData;
            }
        }
        else
        {
            dataMap.put(player.getName(), playerData);
            return playerData;
        }

        // Create new data if nonexistent
        FLog.info("Creating new player verification entry for " + player.getName());

        // Create new player
        playerData = new PlayerData(player);
        playerData.addIp(FUtil.getIp(player));

        // Store player
        dataMap.put(player.getName(), playerData);

        // Save player
        plugin.sql.addPlayer(playerData);
        return playerData;

    }

    public PlayerData getData(String username)
    {
        // Check for existing data
        PlayerData playerData = dataMap.get(username);
        if (playerData != null)
        {
            return playerData;
        }

        playerData = loadByName(username);

        if (playerData != null)
        {
            dataMap.put(username, playerData);
        }
        else
        {
            return null;
        }

        return playerData;
    }

    public PlayerData getDataByIp(String ip)
    {
        PlayerData player = loadByIp(ip);

        if (player != null)
        {
            dataMap.put(player.getName(), player);
        }

        return player;
    }

    public Map<String, FPlayer> getPlayerMap()
    {
        return playerMap;
    }

    public Map<String, PlayerData> getDataMap()
    {
        return dataMap;
    }
}