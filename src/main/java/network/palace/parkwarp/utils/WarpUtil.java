package network.palace.parkwarp.utils;

import com.mongodb.client.FindIterable;
import lombok.Getter;
import network.palace.core.Core;
import network.palace.core.player.Rank;
import network.palace.parkwarp.dashboard.packets.parks.PacketRefreshWarps;
import network.palace.parkwarp.dashboard.packets.parks.PacketWarp;
import network.palace.parkwarp.handlers.Warp;
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

    private List<Warp> warps = new ArrayList<>();

    public void crossServerWarp(final UUID uuid, final String warp, final String server) {
        Core.getDashboardConnection().send(new PacketWarp(uuid, warp, server));
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

    public void updateWarps() {
        PacketRefreshWarps packet = new PacketRefreshWarps(Core.getInstanceName());
        Core.getDashboardConnection().send(packet);
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
