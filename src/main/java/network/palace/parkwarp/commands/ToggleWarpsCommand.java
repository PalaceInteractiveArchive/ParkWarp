package network.palace.parkwarp.commands;

import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.Rank;
import network.palace.parkwarp.ParkWarp;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

@CommandMeta(description = "Toggle the ability to warp by players below Special Guest", rank = Rank.COORDINATOR)
public class ToggleWarpsCommand extends CoreCommand {

    public ToggleWarpsCommand() {
        super("togglewarps");
    }

    @Override
    protected void handleCommandUnspecific(CommandSender sender, String[] args) throws CommandException {
        if (ParkWarp.getWarpUtil().toggleWarps()) {
            sender.sendMessage(ChatColor.RED + "You've disabled warp usage for players below " + Rank.SPECIALGUEST.getFormattedName() + "!");
        } else {
            sender.sendMessage(ChatColor.GREEN + "You've enabled warp usage for players below " + Rank.SPECIALGUEST.getFormattedName() + "!");
        }
    }
}
