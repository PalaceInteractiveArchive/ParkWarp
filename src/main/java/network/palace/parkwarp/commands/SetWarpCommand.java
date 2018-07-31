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
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandMeta(description = "Set a new warp", rank = Rank.MOD)
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
        if (args.length > 0) {
            WarpUtil wu = ParkWarp.getInstance().getWarpUtil();
            final String w = args[0];
            Location loc = player.getLocation();
            if (wu.warpExists(w)) {
                player.sendMessage(ChatColor.RED
                        + "A warp already exists by that name! To change the location of that warp, type /uwarp [Warp Name] <Rank>");
                return;
            }
            final Warp warp = new Warp(w, Core.getServerType(), loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(),
                    loc.getPitch(), loc.getWorld().getName());
            if (args.length > 1) {
                Rank rank = Rank.fromString(args[1]);
                warp.setRank(rank);
            }
            Core.runTaskAsynchronously(() -> {
                wu.addWarp(warp);
                wu.updateWarps();
                player.sendMessage(ChatColor.GRAY + "Warp " + w + " set.");
            });
            return;
        }
        player.sendMessage(ChatColor.RED + "/setwarp [Warp Name] <Rank>");
    }
}
