package network.palace.parkwarp.commands;

import network.palace.core.Core;
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
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandMeta(description = "Update Warp")
@CommandPermission(rank = Rank.MOD)
public class Commanduwarp extends CoreCommand {

    public Commanduwarp() {
        super("uwarp");
    }

    @Override
    protected void handleCommandUnspecific(CommandSender sender, String[] args) throws CommandException {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED
                    + "Only players can use this command!");
            return;
        }
        final Player player = (Player) sender;
        if (args.length == 1) {
            final String w = args[0];
            if (!WarpUtil.warpExists(w)) {
                player.sendMessage(ChatColor.RED
                        + "A warp doesn't exist by that name! To add a warp, type /setwarp [Warp Name]");
                return;
            }
            Location loc = player.getLocation();
            final Warp warp = WarpUtil.findWarp(w);
            final Warp newWarp = new Warp(w, Core.getServerType(), loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(),
                    loc.getPitch(), loc.getWorld().getName());
            Bukkit.getScheduler().runTaskAsynchronously(ParkWarp.getInstance(), () -> {
                ParkWarp.removeWarp(warp);
                ParkWarp.addWarp(newWarp);
                WarpUtil.removeWarp(warp);
                WarpUtil.addWarp(newWarp);
                WarpUtil.updateWarps();
                player.sendMessage(ChatColor.GRAY + "Warp " + w + " has been updated.");
            });
            return;
        }
        player.sendMessage(ChatColor.RED + "/uwarp [Warp Name]");
    }
}