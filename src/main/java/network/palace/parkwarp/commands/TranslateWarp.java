package network.palace.parkwarp.commands;

import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
import network.palace.core.player.Rank;
import network.palace.core.utils.MiscUtil;
import network.palace.parkwarp.ParkWarp;
import network.palace.parkwarp.handlers.Warp;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;

@CommandMeta(description = "Translate the x/y/z coordinates for a warp", rank = Rank.DEVELOPER)
public class TranslateWarp extends CoreCommand {

    public TranslateWarp() {
        super("translatewarp");
    }

    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        if (args.length != 5) {
            player.sendMessage(ChatColor.RED + "/translatewarp [from world] [to world] [rel x] [rel y] [rel z]");
            return;
        }
        World fromWorld = Bukkit.getWorld(args[0]), toWorld = Bukkit.getWorld(args[1]);
        if (fromWorld == null) {
            player.sendMessage(ChatColor.RED + "Unknown world '" + args[0] + "'!");
            return;
        }
        if (toWorld == null) {
            player.sendMessage(ChatColor.RED + "Unknown world '" + args[1] + "'!");
            return;
        }
        if (!(MiscUtil.checkIfDouble(args[2]) &&
                MiscUtil.checkIfDouble(args[3]) &&
                MiscUtil.checkIfDouble(args[4]))) {
            player.sendMessage(ChatColor.RED + "The coordinates must be numbers!");
            return;
        }
        double dx = Double.parseDouble(args[2]);
        double dy = Double.parseDouble(args[3]);
        double dz = Double.parseDouble(args[4]);
        for (Warp warp : ParkWarp.getWarpUtil().getWarps()) {
//            Warp warp = ParkWarp.getWarpUtil().findWarp(args[4]);
            if (warp == null || !warp.getWorldName().equalsIgnoreCase(fromWorld.getName())) continue;
            warp.setWorldLocal(toWorld);
            warp.setX(warp.getX() + dx);
            warp.setY(warp.getY() + dy);
            warp.setZ(warp.getZ() + dz);
            ParkWarp.getWarpUtil().removeWarp(warp);
            ParkWarp.getWarpUtil().addWarp(warp);
            player.sendMessage(ChatColor.GREEN + "Updated " + warp.getName());
        }
        player.sendMessage(ChatColor.YELLOW + "Done!");
    }
}
