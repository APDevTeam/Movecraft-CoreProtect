package net.countercraft.movecraft.coreprotect;

import net.coreprotect.CoreProtect;
import net.coreprotect.CoreProtectAPI;
import net.countercraft.movecraft.coreprotect.listeners.CraftDetectListener;
import net.countercraft.movecraft.coreprotect.listeners.CraftReleaseListener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

public final class MovecraftCoreProtect extends JavaPlugin {
    @Nullable
    private static MovecraftCoreProtect instance;
    @Nullable
    private CoreProtectAPI coreProtectAPI;

    @Nullable
    public static MovecraftCoreProtect getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        Plugin plugin = getServer().getPluginManager().getPlugin("CoreProtect");
        if (!(plugin instanceof CoreProtect)) {
            // error
            coreProtectAPI = null;
            setEnabled(false);
            return;
        }
        coreProtectAPI = ((CoreProtect) plugin).getAPI();
        if (!coreProtectAPI.isEnabled()) {
            // error
            coreProtectAPI = null;
            setEnabled(false);
            return;
        }

        saveDefaultConfig();

        Config.LOG_INTERACTIONS = getConfig().getBoolean("LogInteractions", true);
        Config.LOG_BLOCKS = getConfig().getBoolean("LogBlocks", false);

        getServer().getPluginManager().registerEvents(new CraftDetectListener(), this);
        getServer().getPluginManager().registerEvents(new CraftReleaseListener(), this);

        instance = this;
    }

    @Nullable
    public CoreProtectAPI getCoreProtectAPI() {
        return coreProtectAPI;
    }
}
