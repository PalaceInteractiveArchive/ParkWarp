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
            player.sendMessage(ChatColor.RED + "/translatewarp [target world] [rel x] [rel y] [rel z] [warp]");
            return;
        }
        World world = Bukkit.getWorld(args[0]);
        if (world == null) {
            player.sendMessage(ChatColor.RED + "Unknown world '" + args[0] + "'!");
            return;
        }
        if (!(MiscUtil.checkIfDouble(args[1]) &&
                MiscUtil.checkIfDouble(args[2]) &&
                MiscUtil.checkIfDouble(args[3]))) {
            player.sendMessage(ChatColor.RED + "The coordinates must be numbers!");
            return;
        }
        Warp warp = ParkWarp.getWarpUtil().findWarp(args[4]);
        if (warp == null) {
            player.sendMessage(ChatColor.RED + "Unknown warp '" + args[4] + "'!");
            return;
        }
        warp.setWorld(world);
        warp.setX(warp.getX() + Double.parseDouble(args[1]));
        warp.setY(warp.getY() + Double.parseDouble(args[2]));
        warp.setZ(warp.getZ() + Double.parseDouble(args[3]));
        ParkWarp.getWarpUtil().removeWarp(warp);
        ParkWarp.getWarpUtil().addWarp(warp);
    }
}
