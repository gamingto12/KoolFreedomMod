package me.totalfreedom.totalfreedommod.permissions;

import me.totalfreedom.totalfreedommod.TotalFreedomMod;

import java.util.List;

public enum PermissionEntry
{
    REMOVE("remove"),
    OPERATORS("operators"),
    MASTER_BUILDERS("master_builders"),
    ADMINS("admins"),
    SENIOR_ADMINS("senior_admins");

    private final String configName;

    PermissionEntry(String configName)
    {
        this.configName = configName;
    }

    public String getConfigName()
    {
        return configName;
    }

    public List<?> getList()
    {
        return getConfig().getList(this);
    }

    @SuppressWarnings("unchecked")
    public List<String> getEntry()
    {
        return (List<String>)getList();
    }

    private PermissionConfig getConfig()
    {
        return TotalFreedomMod.getPlugin().permissions;
    }
}
