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

@CommandMeta(description = "Set a new warp")
@CommandPermission(rank = Rank.MOD)
public class SetWarpCommand extends CoreCommand {

    public SetWarpCommand() {
        super("setwarp");
    }

    @Override
    protected void handleCommandUnspecific(CommandSender sender, String[] args) throws CommandException {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command!");
            return;
        }
        final Player player = (Player) sender;
        if (args.length == 1) {
            WarpUtil wu = ParkWarp.getInstance().getWarpUtil();
            final String w = args[0];
            Location loc = player.getLocation();
            if (wu.warpExistsSql(w)) {
                player.sendMessage(ChatColor.RED
                        + "A warp already exists by that name! To change the location of that warp, type /uwarp [Warp Name]");
                return;
            }
            final Warp warp = new Warp(w, Core.getServerType(), loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(),
                    loc.getPitch(), loc.getWorld().getName());
            Bukkit.getScheduler().runTaskAsynchronously(ParkWarp.getInstance(), () -> {
                wu.addWarp(warp);
                wu.addWarpSql(warp);
                wu.updateWarps();
                player.sendMessage(ChatColor.GRAY + "Warp " + w + " set.");
            });
            return;
        }
        player.sendMessage(ChatColor.RED + "/setwarp [Warp Name]");
    }
}