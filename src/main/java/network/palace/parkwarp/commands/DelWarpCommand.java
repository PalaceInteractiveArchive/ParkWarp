package network.palace.parkwarp.commands;

import network.palace.core.Core;
import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.Rank;
import network.palace.parkwarp.ParkWarp;
import network.palace.parkwarp.handlers.Warp;
import network.palace.parkwarp.utils.WarpUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

@CommandMeta(description = "Delete a warp", rank = Rank.MOD)
public class DelWarpCommand extends CoreCommand {

    public DelWarpCommand() {
        super("delwarp");
    }

    @Override
    protected void handleCommandUnspecific(CommandSender sender, String[] args) throws CommandException {
        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED + "/delwarp [Warp Name]");
            return;
        }
        WarpUtil wu = ParkWarp.getWarpUtil();
        final String w = args[0];
        final Warp warp = wu.findWarp(w);
        if (warp == null) {
            sender.sendMessage(ChatColor.RED + "Warp not found!");
            return;
        }
        Core.runTaskAsynchronously(() -> {
            wu.removeWarp(warp);
            wu.updateWarps();
            sender.sendMessage(ChatColor.GRAY + "Warp " + w + " has been removed.");
        });
    }
}
