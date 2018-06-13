package network.palace.parkwarp.utils;

import com.mongodb.client.FindIterable;
import network.palace.core.Core;
import network.palace.parkwarp.dashboard.packets.parks.PacketRefreshWarps;
import network.palace.parkwarp.dashboard.packets.parks.PacketWarp;
import network.palace.parkwarp.handlers.Warp;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class WarpUtil {

    private List<Warp> warps = new ArrayList<>();

    public WarpUtil() {
        refreshWarps();
    }

    public void crossServerWarp(final UUID uuid, final String warp, final String server) {
        PacketWarp packet = new PacketWarp(uuid, warp, server);
        Core.getDashboardConnection().send(packet.getJSON().toString());
    }

    public void refreshWarps() {
        warps.clear();
        FindIterable<Document> list = Core.getMongoHandler().getWarps();
        for (Document d : list) {
            Warp w = new Warp(d.getString("name"),
                    d.getString("server"),
                    d.getDouble("x"),
                    d.getDouble("y"),
                    d.getDouble("z"),
                    d.getInteger("yaw"),
                    d.getInteger("pitch"),
                    d.getString("world"));
            warps.add(w);
        }
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
        Core.getMongoHandler().createWarp(warp.getName(), warp.getServer(), warp.getX(), warp.getY(), warp.getZ(), warp.getYaw(), warp.getPitch(), warp.getWorldName());
    }
}
