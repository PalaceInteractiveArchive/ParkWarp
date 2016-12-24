package network.palace.parkwarp.commands;

import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CommandPermission;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.Rank;
import network.palace.parkwarp.ParkWarp;
import network.palace.parkwarp.handlers.Warp;
import network.palace.parkwarp.utils.WarpUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

@CommandMeta(description = "Delete a warp")
@CommandPermission(rank = Rank.KNIGHT)
public class Commanddelwarp extends CoreCommand {

    public Commanddelwarp() {
        super("delwarp");
    }

    @Override
    protected void handleCommandUnspecific(CommandSender sender, String[] args) throws CommandException {
        if (args.length == 1) {
            final String w = args[0];
            final Warp warp = WarpUtil.findWarp(w);
            if (WarpUtil.findWarp(w) == null) {
                sender.sendMessage(ChatColor.RED + "Warp not found!");
                return;
            }
            Bukkit.getScheduler().runTaskAsynchronously(ParkWarp.getInstance(), () -> {
                ParkWarp.removeWarp(warp);
                WarpUtil.removeWarp(warp);
                WarpUtil.updateWarps();
                sender.sendMessage(ChatColor.GRAY + "Warp " + w
                        + " has been removed.");
            });
            return;
        }
        sender.sendMessage(ChatColor.RED + "/delwarp [Warp Name]");
    }
}
