package network.palace.parkwarp.commands;

import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.Rank;
import network.palace.parkwarp.ParkWarp;
import network.palace.parkwarp.handlers.Warp;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;

@CommandMeta(description = "Get information about a warp", rank = Rank.MOD, aliases = "winfo")
public class WarpInfoCommand extends CoreCommand {

    public WarpInfoCommand() {
        super("warpinfo");
    }

    @Override
    protected void handleCommandUnspecific(CommandSender sender, String[] args) throws CommandException {
        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED + "/winfo [name]");
            return;
        }
        Warp warp = ParkWarp.getWarpUtil().findWarp(args[0]);
        if (warp == null) {
            sender.sendMessage(ChatColor.RED + "Warp not found!");
            return;
        }
        Rank rank = warp.getRank() == null ? Rank.SETTLER : warp.getRank();
        sender.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "Warp Info:");
        sender.sendMessage(ChatColor.AQUA + "Name: " + rank.getTagColor() + warp.getName());
        sender.sendMessage(ChatColor.AQUA + "X: " + ChatColor.GREEN + warp.getX());
        sender.sendMessage(ChatColor.AQUA + "Y: " + ChatColor.GREEN + warp.getY());
        sender.sendMessage(ChatColor.AQUA + "Z: " + ChatColor.GREEN + warp.getZ());
        sender.sendMessage(ChatColor.AQUA + "Yaw: " + ChatColor.GREEN + warp.getYaw());
        sender.sendMessage(ChatColor.AQUA + "Pitch: " + ChatColor.GREEN + warp.getPitch());
        sender.sendMessage(ChatColor.AQUA + "World: " + ChatColor.GREEN + warp.getWorldName());
        sender.sendMessage(ChatColor.AQUA + "Server: " + ChatColor.GREEN + warp.getServer());
        sender.sendMessage(ChatColor.AQUA + "Rank: " + ChatColor.GREEN + rank.getFormattedName());
    }

    private static String cleanLocationString(Location loc) {
        return loc.getX() + "," + loc.getY() + "," + loc.getZ();
    }
}
