package net.countercraft.movecraft.coreprotect.listeners;

import net.coreprotect.CoreProtectAPI;
import net.countercraft.movecraft.MovecraftLocation;
import net.countercraft.movecraft.coreprotect.Config;
import net.countercraft.movecraft.coreprotect.MovecraftCoreProtect;
import net.countercraft.movecraft.craft.PilotedCraft;
import net.countercraft.movecraft.craft.type.CraftType;
import net.countercraft.movecraft.events.CraftReleaseEvent;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

public class CraftReleaseListener implements Listener {
    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onCraftRelease(@NotNull CraftReleaseEvent e) {
        if (!(e.getCraft() instanceof PilotedCraft))
            return;

        if (MovecraftCoreProtect.getInstance() == null)
            return;
        CoreProtectAPI api = MovecraftCoreProtect.getInstance().getCoreProtectAPI();
        if (api == null)
            return;

        String userName = ((PilotedCraft) e.getCraft()).getPilot().getName();
        World w = e.getCraft().getWorld();
        String typeName = e.getCraft().getType().getStringProperty(CraftType.NAME);
        for (MovecraftLocation movecraftLocation : e.getCraft().getHitBox()) {
            Location loc = movecraftLocation.toBukkit(w);
            Block block = loc.getBlock();
            BlockState state = block.getState();

            if (Config.LOG_INTERACTIONS && state instanceof Sign) {
                Sign sign = (Sign) state;
                if (sign.getLine(0).equalsIgnoreCase(typeName) // First line is of the correct type
                        || (sign.getLine(0).equalsIgnoreCase("Subcraft Rotate") // Or first line is Subcraft Rotate
                            && sign.getLine(1).equalsIgnoreCase(typeName)) // And second line is the correct type
                        || sign.getLine(0).equalsIgnoreCase("Release")) { // Or first line is Release
                    // Log interaction with sign
                    if (!api.logInteraction(userName, loc))
                        throw new RuntimeException(); // Throw an exception on failure to log
                }
            }
            if (Config.LOG_BLOCKS) {
                // Log placement of all released blocks
                if (!api.logPlacement(userName, loc, block.getType(), block.getBlockData()))
                    throw new RuntimeException(); // Throw an exception on failure to log
            }
        }
    }
}
