package network.palace.parkwarp.utils;

import com.mongodb.client.FindIterable;
import lombok.Getter;
import network.palace.core.Core;
import network.palace.core.messagequeue.packets.SendPlayerPacket;
import network.palace.core.player.CPlayer;
import network.palace.core.player.Rank;
import network.palace.parkwarp.handlers.Warp;
import network.palace.parkwarp.packets.RefreshWarpsPacket;
import org.bson.Document;
import org.bukkit.Bukkit;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class WarpUtil {
    //False if all players can warp, true if below SG can't
    @Getter private boolean toggledWarps = false;

    private final List<Warp> warps = new ArrayList<>();

    public WarpUtil() {
        refreshWarps();
    }

    public void crossServerWarp(CPlayer player, final String warp, final String server) throws Exception {
        Core.getMongoHandler().setOnlineDataValue(player.getUniqueId(), "crossServerWarp", warp);
        Core.getMongoHandler().setOnlineDataValue(player.getUniqueId(), "crossServerWarpExpires", System.currentTimeMillis() + (5 * 1000));
        Core.getMessageHandler().sendToProxy(new SendPlayerPacket(player.getUniqueId().toString(), server), UUID.fromString(String.valueOf(player.getRegistry().getEntry("proxy"))));
    }

    public void refreshWarps() {
        warps.clear();
        FindIterable<Document> list = Core.getMongoHandler().getWarps();
        for (Document d : list) {
            Warp warp = new Warp(d.getString("name"),
                    d.getString("server"),
                    d.getDouble("x"),
                    d.getDouble("y"),
                    d.getDouble("z"),
                    d.getInteger("yaw"),
                    d.getInteger("pitch"),
                    d.getString("world"));
            if (d.containsKey("rank")) {
                try {
                    warp.setRank(Rank.fromString(d.getString("rank")));
                } catch (Exception e) {
                    e.printStackTrace();
                    Bukkit.getLogger().warning("Error loading warp " + d.getString("name"));
                }
            }
            warps.add(warp);
        }

        this.toggledWarps = new File("plugins/.disabledWarps").exists();
    }

    public boolean warpExists(String name) {
        return findWarp(name) != null;
    }

    public Warp findWarp(String name) {
        for (Warp w : getWarps()) {
            if (w.getName().equalsIgnoreCase(name)) {
                return w;
            }
        }
        return null;
    }

    public void updateWarps() throws IOException {
        Core.getMessageHandler().sendMessage(new RefreshWarpsPacket(), Core.getMessageHandler().ALL_MC);
    }

    public List<Warp> getWarps() {
        return new ArrayList<>(warps);
    }

    public void clearWarps() {
        warps.clear();
    }

    public void removeWarp(Warp warp) {
        if (warp == null) return;
        Core.getMongoHandler().deleteWarp(warp.getName());
        warps.remove(warp);
    }

    public void addWarp(Warp warp) {
        warps.add(warp);
        Core.getMongoHandler().createWarp(warp.getName(), warp.getServerDatabase(), warp.getX(), warp.getY(), warp.getZ(),
                warp.getYaw(), warp.getPitch(), warp.getWorldName(), warp.getRank());
    }

    public boolean toggleWarps() {
        toggledWarps = !toggledWarps;
        File disabledFile = new File("plugins/.disabledWarps");
        if (toggledWarps) {
            try {
                disabledFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            disabledFile.delete();
        }
        return toggledWarps;
    }
}
