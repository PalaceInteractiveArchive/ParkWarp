package network.palace.parkwarp.commands;

import network.palace.core.Core;
import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.message.FormattedMessage;
import network.palace.core.player.CPlayer;
import network.palace.core.player.Rank;
import network.palace.core.utils.MiscUtil;
import network.palace.parkwarp.ParkWarp;
import network.palace.parkwarp.handlers.Warp;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@CommandMeta(description = "Warp to a location")
public class WarpCommand extends CoreCommand {

    public WarpCommand() {
        super("warp");
    }

    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        if (args.length == 1) {
            if (MiscUtil.checkIfInt(args[0])) {
                //Listing warp pages
                listWarps(player, Integer.parseInt(args[0]), false);
                return;
            }
            if (args[0].equalsIgnoreCase("-s")) {
                //Listing local warps
                listWarps(player, 1, true);
                return;
            }
            final String w = args[0];
            Warp warp = ParkWarp.getWarpUtil().findWarp(w);
            if (warp == null) {
                player.sendMessage(ChatColor.RED + "Warp not found!");
                return;
            }
            ChatColor warpColor = warp.getRank() == null ? ChatColor.DARK_AQUA : warp.getRank().getTagColor();
            if (warp.getRank() != null) {
                Rank required = warp.getRank();
                if (player.getRank().getRankId() < required.getRankId()) {
                    player.sendMessage(ChatColor.RED + "You must be the " + required.getFormattedName()
                            + ChatColor.RED + " rank or above to visit this warp!");
                    return;
                }
            }
            String targetServer = warp.getServer();
            if (targetServer.equals(Core.getServerType())) {
                //Warping self to local warp
                if (ParkWarp.getWarpUtil().isToggledWarps() && player.getRank().getRankId() < Rank.SPECIALGUEST.getRankId()) {
                    //Warping is disabled for players below Special Guest, block this warp
                    player.sendMessage(ChatColor.RED + "Warping is disabled here.");
                    return;
                }
                if (player.isInsideVehicle()) {
                    //Is on ride
                    player.sendMessage(ChatColor.RED + "You can't teleport while on a ride!");
                    return;
                }
                //Isn't on ride
                player.teleport(warp, PlayerTeleportEvent.TeleportCause.COMMAND);
                player.sendMessage(ChatColor.BLUE + "You have arrived at "
                        + ChatColor.WHITE + "[" + warpColor + w
                        + ChatColor.WHITE + "]");
                return;
            } else {
                //Warping self to remote warp
                ParkWarp.getWarpUtil().crossServerWarp(player.getUniqueId(), warp.getName(), targetServer);
                return;
            }
        }
        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("-s")) {
                if (MiscUtil.checkIfInt(args[1])) {
                    listWarps(player, Integer.parseInt(args[1]), true);
                    return;
                }
                listWarps(player, 1, true);
                return;
            }
            if (player.getRank().getRankId() < Rank.TRAINEE.getRankId()) {
                player.performCommand("warp " + args[0]);
                return;
            }
            teleportPlayer(player.getBukkitPlayer(), args[1], args[0]);
        }
        listWarps(player, 1, false);
    }

    @Override
    protected void handleCommandUnspecific(CommandSender sender, String[] args) throws CommandException {
        if (args.length == 2) {
            teleportPlayer(sender, args[1], args[0]);
        } else {
            sender.sendMessage(ChatColor.RED + "/warp [Warp Name] [Username]");
        }
    }

    private void teleportPlayer(CommandSender sender, String playerName, String warpString) {
        CPlayer tp;
        if ((tp = Core.getPlayerManager().getPlayer(playerName)) == null) {
            sender.sendMessage(ChatColor.RED + "Player not found.");
            return;
        }
        teleportPlayer(sender, tp, warpString, true);
    }

    private void teleportPlayer(CommandSender sender, CPlayer tp, String warpString, boolean force) {
        Warp warp = ParkWarp.getWarpUtil().findWarp(warpString);
        if (warp == null) {
            sender.sendMessage(ChatColor.RED + "Warp not found!");
            return;
        }
        ChatColor warpColor = warp.getRank() == null ? ChatColor.DARK_AQUA : warp.getRank().getTagColor();
        String targetServer = warp.getServer();
        if (targetServer.equals(Core.getServerType())) {
            //Being warped by console to local warp
            if (tp.isInsideVehicle()) {
                //Is on ride
                tp.eject();
                Core.runTaskLater(ParkWarp.getInstance(), () ->
                        tp.teleport(warp, PlayerTeleportEvent.TeleportCause.COMMAND), 10L);
            } else {
                //Isn't on ride
                tp.teleport(warp, PlayerTeleportEvent.TeleportCause.COMMAND);
            }
            tp.sendMessage(ChatColor.BLUE + "You have arrived at " + ChatColor.WHITE + "[" +
                    warpColor + warp.getName() + ChatColor.WHITE + "]");
            if (force) {
                sender.sendMessage(ChatColor.BLUE + tp.getName() + " has arrived at " + ChatColor.WHITE + "[" +
                        warpColor + warp.getName() + ChatColor.WHITE + "]");
            }
        } else {
            //Being warped to remote warp
            ParkWarp.getWarpUtil().crossServerWarp(tp.getUniqueId(), warpString, targetServer);
        }
    }

    /**
     * List warps to a player depending on certain parameters
     *
     * @param player     the player to message the list to
     * @param page       the page number to show
     * @param serverOnly whether or not to only list warps on the current server
     */
    public void listWarps(CPlayer player, int page, boolean serverOnly) {
        Rank rank = player.getRank();

        Stream<Warp> warpStream = ParkWarp.getWarpUtil().getWarps()
                .stream()
                .filter(warp -> {
                    if (serverOnly && (warp.getWorld() == null || !warp.getServer().equalsIgnoreCase(Core.getServerType()))) {
                        return false;
                    }
                    return warp.getRank() == null || rank.getRankId() >= warp.getRank().getRankId();
                }).sorted(Comparator.comparing(o -> o.getName().toLowerCase()));

        List<Warp> warps = warpStream.collect(Collectors.toList());

        if (page < 1 || warps.size() < ((page - 1) * 20)) page = 1;
        int max = page * 20;

        FormattedMessage msg = new FormattedMessage((serverOnly ? "Server " : "") + "Warps (Page " + page + "):\n")
                .color(ChatColor.GOLD);

        warps = warps.subList(20 * (page - 1), Math.min(warps.size(), max));

        for (int i = 0; i < warps.size(); i++) {
            Warp warp = warps.get(i);
            String name = warp.getName();
            if (i < (warps.size() - 1)) {
                name = name + ", ";
            }
            Rank warpRank = warp.getRank() == null ? Rank.SETTLER : warp.getRank();
            msg.then(name)
                    .color(warpRank.getTagColor())
                    .command("/warp " + warp.getName())
                    .tooltip(ChatColor.GREEN + "Click to warp to " + ChatColor.BLUE + warp.getName() + ChatColor.GREEN + "!");
        }
        msg.send(player);
    }
}
