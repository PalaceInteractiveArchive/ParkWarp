package network.palace.parkwarp.commands;

import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CommandPermission;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.Rank;
import network.palace.parkwarp.ParkWarp;
import network.palace.parkwarp.utils.WarpUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

/**
 * Created by Marc on 11/21/16
 */
@CommandMeta(description = "Reload warps")
@CommandPermission(rank = Rank.MOD)
public class Commandwrl extends CoreCommand {

    public Commandwrl() {
        super("wrl");
    }

    @Override
    protected void handleCommandUnspecific(CommandSender sender, String[] args) throws CommandException {
        sender.sendMessage(ChatColor.BLUE + "Reloading Warps...");
        ParkWarp.clearWarps();
        WarpUtil.refreshWarps();
        sender.sendMessage(ChatColor.BLUE + "Warps Reloaded!");
    }
}