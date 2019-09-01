package network.palace.parkwarp.commands;

import network.palace.core.Core;
import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
import network.palace.core.player.Rank;
import network.palace.parkwarp.ParkWarp;
import network.palace.parkwarp.handlers.Warp;
import network.palace.parkwarp.utils.WarpUtil;
import org.bukkit.ChatColor;
import org.bukkit.Location;

@CommandMeta(description = "Update Warp", rank = Rank.MOD)
public class UpdateWarpCommand extends CoreCommand {

    public UpdateWarpCommand() {
        super("uwarp");
    }

    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        if (args.length == 0) {
            player.sendMessage(ChatColor.RED + "/uwarp [Warp Name] <Rank>");
            return;
        }
        WarpUtil wu = ParkWarp.getWarpUtil();
        final String w = args[0];
        if (!wu.warpExists(w)) {
            player.sendMessage(ChatColor.RED
                    + "A warp doesn't exist by that name! To add a warp, type /setwarp [Warp Name]");
            return;
        }
        Location loc = player.getLocation();
        final Warp warp = wu.findWarp(w);
        final Warp newWarp = new Warp(w, Core.getServerType(), loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(),
                loc.getPitch(), loc.getWorld().getName());
        if (args.length > 1) {
            Rank rank = Rank.fromString(args[1]);
            newWarp.setRank(rank);
        }
        Core.runTaskAsynchronously(ParkWarp.getInstance(), () -> {
            wu.removeWarp(warp);
            wu.addWarp(newWarp);
            wu.updateWarps();
            player.sendMessage(ChatColor.GRAY + "Warp " + w + " has been updated. Rank minimum: " + (newWarp.getRank() == null ? Rank.SETTLER.getFormattedName() : newWarp.getRank().getFormattedName()));
        });
    }
}
