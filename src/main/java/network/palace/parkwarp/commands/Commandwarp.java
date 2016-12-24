package network.palace.parkwarp.commands;

import network.palace.core.Core;
import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.message.FormattedMessage;
import network.palace.core.player.Rank;
import network.palace.parkwarp.ParkWarp;
import network.palace.parkwarp.handlers.Warp;
import network.palace.parkwarp.utils.WarpUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@CommandMeta(description = "Warp to a location")
public class Commandwarp extends CoreCommand {

    public Commandwarp() {
        super("warp");
    }

    @Override
    protected void handleCommandUnspecific(CommandSender sender, String[] args) throws CommandException {
        if (!(sender instanceof Player)) {
            if (args.length == 2) {
                if (Bukkit.getPlayer(args[1]) == null) {
                    sender.sendMessage(ChatColor.RED + "Player not found.");
                    return;
                }
                final Player tp = Bukkit.getPlayer(args[1]);
                final String w = args[0];
                Warp warp;
                if (WarpUtil.findWarp(w) == null) {
                    sender.sendMessage(ChatColor.RED + "Warp not found!");
                    return;
                } else {
                    warp = WarpUtil.findWarp(w);
                }
                String targetServer = warp.getServer();
                String currentServer = Core.getInstance().getServerType();
                final Location loc = warp.getLocation();
                if (targetServer.equals(currentServer)) {
                    if (tp.isInsideVehicle()) {
                        tp.eject();
                        sender.sendMessage(ChatColor.BLUE + tp.getName() + " has arrived at " + ChatColor.WHITE +
                                "[" + ChatColor.GREEN + w + ChatColor.WHITE + "]");
                        Bukkit.getScheduler().runTaskLater(ParkWarp.getInstance(), () -> tp.teleport(warp.getLocation()), 10L);
                        return;
                    }
                    tp.teleport(warp.getLocation());
                    tp.sendMessage(ChatColor.BLUE + "You have arrived at " + ChatColor.WHITE + "[" +
                            ChatColor.GREEN + w + ChatColor.WHITE + "]");
                    sender.sendMessage(ChatColor.BLUE + tp.getName() + " has arrived at " + ChatColor.WHITE + "[" +
                            ChatColor.GREEN + w + ChatColor.WHITE + "]");
                    return;
                } else {
                    WarpUtil.crossServerWarp(tp.getUniqueId(), w, targetServer);
                    return;
                }
            }
            sender.sendMessage(ChatColor.RED + "/warp [Warp Name] [Username]");
            return;
        }
        final Player player = (Player) sender;
        if (args.length == 1) {
            if (isInt(args[0])) {
                listWarps(player, Integer.parseInt(args[0]));
                return;
            }
            if (args[0].equalsIgnoreCase("-s")) {
                listWarpsServer(player, 1);
                return;
            }
            final String w = args[0];
            Warp warp = WarpUtil.findWarp(w);
            if (warp == null) {
                player.sendMessage(ChatColor.RED + "Warp not found!");
                return;
            }
            Rank rank = Core.getPlayerManager().getPlayer(player).getRank();
            if (warp.getName().toLowerCase().startsWith("dvc")) {
                if (rank.getRankId() < Rank.DVCMEMBER.getRankId()) {
                    player.sendMessage(ChatColor.RED + "You must be the " + Rank.DVCMEMBER.getNameWithBrackets()
                            + ChatColor.RED + " rank or above to use this warp!");
                    return;
                }
            }
            if (warp.getName().toLowerCase().startsWith("share")) {
                if (rank.getRankId() < Rank.SHAREHOLDER.getRankId()) {
                    player.sendMessage(ChatColor.RED + "You must be the " + Rank.SHAREHOLDER.getNameWithBrackets()
                            + ChatColor.RED + " rank or above to use this warp!");
                    return;
                }
            }
            if (warp.getName().toLowerCase().startsWith("char")) {
                if (rank.getRankId() < Rank.CHARACTER.getRankId()) {
                    player.sendMessage(ChatColor.RED + "You must be the " + Rank.CHARACTER.getNameWithBrackets()
                            + ChatColor.RED + " rank or above to use this warp!");
                    return;
                }
            }
            if (warp.getName().toLowerCase().startsWith("staff")) {
                if (rank.getRankId() < Rank.SQUIRE.getRankId()) {
                    player.sendMessage(ChatColor.RED + "You must be the " + Rank.SQUIRE.getNameWithBrackets()
                            + ChatColor.RED + " rank or above to use this warp!");
                    return;
                }
            }
            String targetServer = warp.getServer();
            String currentServer = Core.getInstance().getServerType();
            final Location loc = warp.getLocation();
            if (targetServer.equals(currentServer)) {
                if (player.isInsideVehicle()) {
                    player.sendMessage(ChatColor.RED + "You can't teleport while on a ride!");
                    return;
                }
                player.teleport(warp.getLocation());
                player.sendMessage(ChatColor.BLUE + "You have arrived at "
                        + ChatColor.WHITE + "[" + ChatColor.GREEN + w
                        + ChatColor.WHITE + "]");
                return;
            } else {
                WarpUtil.crossServerWarp(player.getUniqueId(), warp.getName(), targetServer);
                return;
            }
        }
        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("-s")) {
                if (isInt(args[1])) {
                    listWarpsServer(player, Integer.parseInt(args[1]));
                    return;
                }
                listWarpsServer(player, 1);
                return;
            }
            Rank rank = Core.getPlayerManager().getPlayer(player).getRank();
            if (rank.getRankId() < Rank.SQUIRE.getRankId()) {
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
            if (WarpUtil.findWarp(w) == null) {
                player.sendMessage(ChatColor.RED + "Warp not found!");
                return;
            } else {
                warp = WarpUtil.findWarp(w);
            }
            final String targetServer = warp.getServer();
            String currentServer = Core.getInstance().getServerType();
            final Location loc = warp.getLocation();
            if (targetServer.equals(currentServer)) {
                player.sendMessage(ChatColor.BLUE + tp.getName()
                        + " has arrived at " + ChatColor.WHITE + "["
                        + ChatColor.GREEN + w + ChatColor.WHITE + "]");
                if (tp.isInsideVehicle()) {
                    tp.sendMessage(ChatColor.RED + "You can't teleport while on a ride!");
                    return;
                }
                tp.teleport(warp.getLocation());
                tp.sendMessage(ChatColor.BLUE + "You have arrived at "
                        + ChatColor.WHITE + "[" + ChatColor.GREEN + w
                        + ChatColor.WHITE + "]");
                return;
            } else {
                WarpUtil.crossServerWarp(tp.getUniqueId(), w, targetServer);
                player.sendMessage(ChatColor.BLUE + tp.getName()
                        + " has arrived at " + ChatColor.WHITE + "["
                        + ChatColor.GREEN + w + ChatColor.WHITE + "]");
                return;
            }
        }
        listWarps(player, 1);
    }

    private void listWarpsServer(Player player, int page) {
        List<Warp> warps = ParkWarp.getWarps();
        List<Warp> list = new ArrayList<>();
        String server = Core.getInstance().getServerType();
        list.addAll(warps.stream().filter(w -> w.getServer().equalsIgnoreCase(server)).collect(Collectors.toList()));
        List<String> nlist = list.stream().map(Warp::getName).collect(Collectors.toList());
        Collections.sort(nlist);
        if (nlist.size() < (page - 1) * 20 && page != 1) {
            page = 1;
        }
        int max = page * 20;
        List<String> names = nlist.subList(20 * (page - 1), nlist.size() < max ? nlist.size() : max);
        FormattedMessage msg = new FormattedMessage("Server Warps (Page " + page + "):\n").color(ChatColor.GOLD);
        for (int i = 0; i < names.size(); i++) {
            String warp = names.get(i);
            if (i == (names.size() - 1)) {
                msg.then(warp).color(ChatColor.GRAY).command("/warp " + warp).tooltip(ChatColor.GREEN +
                        "Click to warp to " + ChatColor.BLUE + warp + ChatColor.GREEN + "!");
                continue;
            }
            msg.then(warp + ", ").color(ChatColor.GRAY).command("/warp " + warp).tooltip(ChatColor.GREEN +
                    "Click to warp to " + ChatColor.BLUE + warp + ChatColor.GREEN + "!");
        }
        msg.send(player);
    }

    private boolean isInt(String s) {
        try {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }


    public static void listWarps(Player player, int page) {
        List<Warp> warps = ParkWarp.getWarps();
        List<String> nlist = warps.stream().map(Warp::getName).collect(Collectors.toList());
        Collections.sort(nlist);
        if (nlist.size() < (page - 1) * 20 && page != 1) {
            page = 1;
        }
        int max = page * 20;
        List<String> names = nlist.subList(20 * (page - 1), nlist.size() < max ? nlist.size() : max);
        FormattedMessage msg = new FormattedMessage("Warps (Page " + page + "):\n").color(ChatColor.GRAY);
        for (int i = 0; i < names.size(); i++) {
            String warp = names.get(i);
            if (i == (names.size() - 1)) {
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