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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
                Warp warp = ParkWarp.getInstance().getWarpUtil().findWarp(w);
                if (warp == null) {
                    sender.sendMessage(ChatColor.RED + "Warp not found!");
                    return;
                }
                String targetServer = warp.getServer();
                String currentServer = Core.getServerType();
                final Location loc = warp.getLocation();
                if (targetServer.equals(currentServer)) {
                    if (tp.isInsideVehicle()) {
                        tp.getBukkitPlayer().eject();
                        sender.sendMessage(ChatColor.BLUE + tp.getName() + " has arrived at " + ChatColor.WHITE +
                                "[" + ChatColor.GREEN + w + ChatColor.WHITE + "]");
                        Bukkit.getScheduler().runTaskLater(ParkWarp.getInstance(), () -> tp.teleport(warp.getLocation(),
                                PlayerTeleportEvent.TeleportCause.COMMAND), 10L);
                        return;
                    }
                    tp.teleport(warp.getLocation(), PlayerTeleportEvent.TeleportCause.COMMAND);
                    tp.sendMessage(ChatColor.BLUE + "You have arrived at " + ChatColor.WHITE + "[" +
                            ChatColor.GREEN + w + ChatColor.WHITE + "]");
                    sender.sendMessage(ChatColor.BLUE + tp.getName() + " has arrived at " + ChatColor.WHITE + "[" +
                            ChatColor.GREEN + w + ChatColor.WHITE + "]");
                    return;
                } else {
                    ParkWarp.getInstance().getWarpUtil().crossServerWarp(tp.getUniqueId(), w, targetServer);
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
            Warp warp = ParkWarp.getInstance().getWarpUtil().findWarp(w);
            if (warp == null) {
                player.sendMessage(ChatColor.RED + "Warp not found!");
                return;
            }
            Rank rank = player.getRank();
            if (warp.getName().toLowerCase().startsWith("dvc")) {
                if (rank.getRankId() < Rank.DWELLER.getRankId()) {
                    player.sendMessage(ChatColor.RED + "You must be the " + Rank.DWELLER.getFormattedName()
                            + ChatColor.RED + " rank or above to use this warp!");
                    return;
                }
            }
            if (warp.getName().toLowerCase().startsWith("share")) {
                if (rank.getRankId() < Rank.DWELLER.getRankId()) {
                    player.sendMessage(ChatColor.RED + "You must be the " + Rank.DWELLER.getFormattedName()
                            + ChatColor.RED + " rank or above to use this warp!");
                    return;
                }
            }
            if (warp.getName().toLowerCase().startsWith("honor")) {
                if (rank.getRankId() < Rank.HONORABLE.getRankId()) {
                    player.sendMessage(ChatColor.RED + "You must be the " + Rank.HONORABLE.getFormattedName()
                            + ChatColor.RED + " rank or above to use this warp!");
                    return;
                }
            }
            if (warp.getName().toLowerCase().startsWith("char")) {
                if (rank.getRankId() < Rank.CHARACTER.getRankId()) {
                    player.sendMessage(ChatColor.RED + "You must be the " + Rank.CHARACTER.getFormattedName()
                            + ChatColor.RED + " rank or above to use this warp!");
                    return;
                }
            }
            if (warp.getName().toLowerCase().startsWith("staff")) {
                if (rank.getRankId() < Rank.TRAINEE.getRankId()) {
                    player.sendMessage(ChatColor.RED + "You must be the " + Rank.TRAINEE.getFormattedName()
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
                        + ChatColor.WHITE + "[" + ChatColor.GREEN + w
                        + ChatColor.WHITE + "]");
                return;
            } else {
                ParkWarp.getInstance().getWarpUtil().crossServerWarp(player.getUniqueId(), warp.getName(), targetServer);
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
            if (ParkWarp.getInstance().getWarpUtil().findWarp(w) == null) {
                player.sendMessage(ChatColor.RED + "Warp not found!");
                return;
            } else {
                warp = ParkWarp.getInstance().getWarpUtil().findWarp(w);
            }
            final String targetServer = warp.getServer();
            String currentServer = Core.getServerType();
            final Location loc = warp.getLocation();
            if (targetServer.equals(currentServer)) {
                player.sendMessage(ChatColor.BLUE + tp.getName()
                        + " has arrived at " + ChatColor.WHITE + "["
                        + ChatColor.GREEN + w + ChatColor.WHITE + "]");
                if (tp.isInsideVehicle()) {
                    tp.sendMessage(ChatColor.RED + "You can't teleport while on a ride!");
                    return;
                }
                tp.teleport(warp.getLocation(), PlayerTeleportEvent.TeleportCause.COMMAND);
                tp.sendMessage(ChatColor.BLUE + "You have arrived at "
                        + ChatColor.WHITE + "[" + ChatColor.GREEN + w
                        + ChatColor.WHITE + "]");
                return;
            } else {
                ParkWarp.getInstance().getWarpUtil().crossServerWarp(tp.getUniqueId(), w, targetServer);
                player.sendMessage(ChatColor.BLUE + tp.getName()
                        + " has arrived at " + ChatColor.WHITE + "["
                        + ChatColor.GREEN + w + ChatColor.WHITE + "]");
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
        List<String> allWarps = new ArrayList<>();
        boolean staff = player.getRank().getRankId() >= Rank.TRAINEE.getRankId();
        for (Warp w : ParkWarp.getInstance().getWarpUtil().getWarps()) {
            if (serverOnly && !w.getServer().equalsIgnoreCase(Core.getServerType())) continue;
            if (!staff && w.getName().toLowerCase().startsWith("staff")) continue;
            allWarps.add(w.getName());
        }
        Collections.sort(allWarps);
        if (page < 1 || allWarps.size() < ((page - 1) * 20)) {
            page = 1;
        }
        int max = page * 20;
        List<String> names = allWarps.subList(20 * (page - 1), allWarps.size() < max ? allWarps.size() : max);
        FormattedMessage msg = new FormattedMessage((serverOnly ? "Server " : "") + "Warps (Page " + page + "):\n")
                .color(ChatColor.GOLD);
        for (int i = 0; i < names.size(); i++) {
            String warp = names.get(i);
            if (i >= (names.size() - 1)) {
                msg.then(warp).color(ChatColor.GRAY).command("/warp " + warp).tooltip(ChatColor.GREEN +
                        "Click to warp to " + ChatColor.BLUE + warp + ChatColor.GREEN + "!");
                continue;
            }
            msg.then(warp + ", ").color(ChatColor.GRAY).command("/warp " + warp).tooltip(ChatColor.GREEN +
                    "Click to warp to " + ChatColor.BLUE + warp + ChatColor.GREEN + "!");
        }
        msg.send(player);
    }
}