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
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
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
    protected void handleCommandUnspecific(CommandSender sender, String[] args) throws CommandException {
        if (!(sender instanceof Player)) {
            if (args.length == 2) {
                if (Core.getPlayerManager().getPlayer(args[1]) == null) {
                    sender.sendMessage(ChatColor.RED + "Player not found.");
                    return;
                }
                final CPlayer tp = Core.getPlayerManager().getPlayer(args[1]);
                final String w = args[0];
                Warp warp = ParkWarp.getWarpUtil().findWarp(w);
                if (warp == null) {
                    sender.sendMessage(ChatColor.RED + "Warp not found!");
                    return;
                }
                ChatColor warpColor = warp.getRank() == null ? ChatColor.DARK_AQUA : warp.getRank().getTagColor();
                String targetServer = warp.getServer();
                String currentServer = Core.getServerType();
                final Location loc = warp.getLocation();
                if (targetServer.equals(currentServer)) {
                    if (tp.isInsideVehicle()) {
                        tp.eject();
                        sender.sendMessage(ChatColor.BLUE + tp.getName() + " has arrived at " + ChatColor.WHITE +
                                "[" + warpColor + w + ChatColor.WHITE + "]");
                        Bukkit.getScheduler().runTaskLater(ParkWarp.getInstance(), () -> tp.teleport(warp.getLocation(),
                                PlayerTeleportEvent.TeleportCause.COMMAND), 10L);
                        return;
                    }
                    tp.teleport(warp.getLocation(), PlayerTeleportEvent.TeleportCause.COMMAND);
                    tp.sendMessage(ChatColor.BLUE + "You have arrived at " + ChatColor.WHITE + "[" +
                            warpColor + w + ChatColor.WHITE + "]");
                    sender.sendMessage(ChatColor.BLUE + tp.getName() + " has arrived at " + ChatColor.WHITE + "[" +
                            warpColor + w + ChatColor.WHITE + "]");
                    return;
                } else {
                    ParkWarp.getWarpUtil().crossServerWarp(tp.getUniqueId(), w, targetServer);
                    return;
                }
            }
            sender.sendMessage(ChatColor.RED + "/warp [Warp Name] [Username]");
            return;
        }
        CPlayer player = Core.getPlayerManager().getPlayer(((Player) sender).getUniqueId());
        if (args.length == 1) {
            if (MiscUtil.checkIfInt(args[0])) {
                listWarps(player, Integer.parseInt(args[0]), false);
                return;
            }
            if (args[0].equalsIgnoreCase("-s")) {
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
            Rank rank = player.getRank();
            if (warp.getRank() != null) {
                Rank required = warp.getRank();
                if (rank.getRankId() < required.getRankId()) {
                    player.sendMessage(ChatColor.RED + "You must be the " + required.getFormattedName()
                            + ChatColor.RED + " rank or above to use this warp!");
                    return;
                }
            }
            String targetServer = warp.getServer();
            String currentServer = Core.getServerType();
            final Location loc = warp.getLocation();
            if (targetServer.equals(currentServer)) {
                if (player.isInsideVehicle()) {
                    player.sendMessage(ChatColor.RED + "You can't teleport while on a ride!");
                    return;
                }
                player.teleport(warp.getLocation(), PlayerTeleportEvent.TeleportCause.COMMAND);
                player.sendMessage(ChatColor.BLUE + "You have arrived at "
                        + ChatColor.WHITE + "[" + warpColor + w
                        + ChatColor.WHITE + "]");
                return;
            } else {
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
            Rank rank = player.getRank();
            if (rank.getRankId() < Rank.TRAINEE.getRankId()) {
                player.performCommand("warp " + args[0]);
                return;
            }
            if (Bukkit.getPlayer(args[1]) == null) {
                player.sendMessage(ChatColor.RED + "Player not found.");
                return;
            }
            final Player tp = Bukkit.getPlayer(args[1]);
            final String w = args[0];
            Warp warp;
            if (ParkWarp.getWarpUtil().findWarp(w) == null) {
                player.sendMessage(ChatColor.RED + "Warp not found!");
                return;
            } else {
                warp = ParkWarp.getWarpUtil().findWarp(w);
            }
            ChatColor warpColor = warp.getRank() == null ? ChatColor.DARK_AQUA : warp.getRank().getTagColor();
            final String targetServer = warp.getServer();
            String currentServer = Core.getServerType();
            final Location loc = warp.getLocation();
            if (targetServer.equals(currentServer)) {
                player.sendMessage(ChatColor.BLUE + tp.getName()
                        + " has arrived at " + ChatColor.WHITE + "["
                        + warpColor + w + ChatColor.WHITE + "]");
                if (tp.isInsideVehicle()) {
                    tp.sendMessage(ChatColor.RED + "You can't teleport while on a ride!");
                    return;
                }
                tp.teleport(warp.getLocation(), PlayerTeleportEvent.TeleportCause.COMMAND);
                tp.sendMessage(ChatColor.BLUE + "You have arrived at "
                        + ChatColor.WHITE + "[" + warpColor + w
                        + ChatColor.WHITE + "]");
                return;
            } else {
                ParkWarp.getWarpUtil().crossServerWarp(tp.getUniqueId(), w, targetServer);
                player.sendMessage(ChatColor.BLUE + tp.getName()
                        + " has arrived at " + ChatColor.WHITE + "["
                        + warpColor + w + ChatColor.WHITE + "]");
                return;
            }
        }
        listWarps(player, 1, false);
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

        warps = warps.subList(20 * (page - 1), warps.size() < max ? warps.size() : max);

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
