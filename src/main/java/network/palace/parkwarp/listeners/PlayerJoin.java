package network.palace.parkwarp.listeners;

import network.palace.core.Core;
import network.palace.core.events.CorePlayerJoinedEvent;
import network.palace.core.player.CPlayer;
import network.palace.core.player.Rank;
import network.palace.parkwarp.ParkWarp;
import network.palace.parkwarp.handlers.Warp;
import org.bson.Document;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;

public class PlayerJoin implements Listener {

    @EventHandler
    public void onPlayerJoin(CorePlayerJoinedEvent event) {
        CPlayer player = event.getPlayer();
        if (!player.getRegistry().hasEntry("onlineData")) return;
        Document onlineData = (Document) player.getRegistry().getEntry("onlineData");
        if (!onlineData.containsKey("crossServerWarp")) return;
        Core.getMongoHandler().setOnlineDataValue(player.getUniqueId(), "crossServerWarp", null);
        Core.getMongoHandler().setOnlineDataValue(player.getUniqueId(), "crossServerWarpExpires", null);
        String warpName = onlineData.getString("crossServerWarp");
        long expires = onlineData.getLong("crossServerWarpExpires");
        if (System.currentTimeMillis() >= expires) return;
        Warp warp = ParkWarp.getWarpUtil().findWarp(warpName);
        // Warp either doesn't exist or isn't on this server, do nothing
        if (warp == null || warp.getWorld() == null) return;
        player.teleport(warp, PlayerTeleportEvent.TeleportCause.PLUGIN);
        ChatColor rankColor = warp.getRank() == null ? Rank.GUEST.getTagColor() : warp.getRank().getTagColor();
        player.sendMessage(ChatColor.BLUE + "You have arrived at " + ChatColor.WHITE + "[" +
                rankColor + warp.getName() + ChatColor.WHITE + "]");
    }
}
