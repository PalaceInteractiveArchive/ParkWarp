package network.palace.parkwarp.utils;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import network.palace.core.Core;
import network.palace.parkwarp.dashboard.packets.parks.PacketRefreshWarps;
import network.palace.parkwarp.dashboard.packets.parks.PacketWarp;
import network.palace.parkwarp.handlers.Warp;
import org.bson.Document;

public class WarpUtil {

    private MongoCollection<Document> warps;

    public WarpUtil() {
        refreshWarps();
    }

    public void crossServerWarp(final UUID uuid, final String warp, final String server) {
        PacketWarp packet = new PacketWarp(uuid, warp, server);
        Core.getDashboardConnection().send(packet.getJSON().toString());
    }

    public void refreshWarps() {
        warps = Core.getMongoHandler().getDatabase().getCollection("warps");
    }

    public boolean warpExists(String name) {
        return findWarp(name) != null;
    }

    public Warp findWarp(String name) {
        Document document = warps.find(Filters.not(Filters.eq(name, null))).first();
        if (document == null) return null;
        return new Warp(name, document.getString("server"), document.getDouble("x"), document.getDouble("y"), document.getDouble("z"), document.get("yaw", Float.class), document.get("pitch", Float.class), document.getString("world"));
    }

    public void updateWarps() {
        PacketRefreshWarps packet = new PacketRefreshWarps(Core.getInstanceName());
        Core.getDashboardConnection().send(packet);
    }

    public List<Warp> getWarps() {
        Document document = warps.find().first();
        if (document == null) {
            return Collections.emptyList();
        }

        return document.keySet().stream().map(this::findWarp).collect(Collectors.toList());
    }

    public void clearWarps() {
        getWarps().forEach(this::removeWarp);
    }

    public void removeWarp(Warp warp) {
        warps.deleteOne(Filters.not(Filters.eq(warp.getName(), null)));
    }

    public void addWarp(Warp warp) {
        Map<String, Object> map = new HashMap<>();
        map.put("server", warp.getServer());
        map.put("x", warp.getX());
        map.put("y", warp.getY());
        map.put("z", warp.getZ());
        map.put("yaw", warp.getYaw());
        map.put("pitch", warp.getPitch());
        map.put("world", warp.getWorld().getName());
        warps.insertOne(new Document(warp.getName(), map));
    }
}
