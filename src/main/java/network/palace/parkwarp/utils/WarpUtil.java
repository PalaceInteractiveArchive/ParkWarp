package network.palace.parkwarp.utils;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import network.palace.core.Core;
import network.palace.parkwarp.ParkWarp;
import network.palace.parkwarp.dashboard.packets.parks.PacketRefreshWarps;
import network.palace.parkwarp.dashboard.packets.parks.PacketWarp;
import network.palace.parkwarp.handlers.Warp;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public class WarpUtil {
    private MongoCollection<Document> warpCollection;

    public WarpUtil() {
        warpCollection = Core.getMongoHandler().getDatabase().getCollection("warps");
    }

    private Document getWarp(String name) {
        FindIterable<Document> list = warpCollection.find(new Document("name", name));
        for (Document doc : list) {
            if (doc.getString("name").equalsIgnoreCase(name)) {
                return doc;
            }
        }
        return null;
    }

    public boolean warpExists(String name) {
        Document warp = getWarp(name);
        return warp != null;
    }

    public String getServer(String name) {
        Document warp = getWarp(name);
        return warp != null ? warp.getString("server") : "unknown";
    }

    public Location getLocation(String name) {
        Document warp = getWarp(name);
        String world = warp.getString("world");
        double x = warp.getDouble("x");
        double y = warp.getDouble("y");
        double z = warp.getDouble("z");
        float yaw = warp.getInteger("yaw");
        float pitch = warp.getInteger("pitch");
        return new Location(Bukkit.getWorld(world), x, y, z, yaw, pitch);
    }

    public void crossServerWarp(final UUID uuid, final String warp, final String server) {
        PacketWarp packet = new PacketWarp(uuid, warp, server);
        Core.getDashboardConnection().send(packet.getJSON().toString());
    }

    public List<Warp> getWarps() {
        List<Warp> warps = new ArrayList<>();
        FindIterable<Document> list = warpCollection.find();
        for (Document doc : list) {
            warps.add(new Warp(doc.getString("name"), doc.getString("server"), doc.getDouble("x"),
                    doc.getDouble("y"), doc.getDouble("z"), doc.getInteger("yaw"), doc.getInteger("pitch"),
                    doc.getString("world")));
        }
        warps.sort(new Comparator<Warp>() {
            @Override
            public int compare(Warp o1, Warp o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });
        return warps;
    }

    public void addWarp(Warp warp) {
        ParkWarp.getInstance().addWarp(warp);
        Document doc = new Document("name", warp.getName()).append("server", warp.getServer()).append("x", warp.getX())
                .append("y", warp.getY()).append("z", warp.getZ()).append("yaw", warp.getYaw())
                .append("pitch", warp.getPitch()).append("world", warp.getWorld().getName());
        warpCollection.insertOne(doc);
    }

    public void removeWarp(Warp warp) {
        ParkWarp.getInstance().removeWarp(warp);
        warpCollection.deleteOne(new Document("name", warp.getName()));
    }

    public Warp findWarp(String name) {
        List<Warp> warps = ParkWarp.getInstance().getWarps();
        for (Warp warp : warps) {
            if (warp.getName().toLowerCase().equals(name.toLowerCase())) {
                return warp;
            }
        }
        return null;
    }

    public void updateWarps() {
        PacketRefreshWarps packet = new PacketRefreshWarps(Core.getInstanceName());
        Core.getDashboardConnection().send(packet.getJSON().toString());
    }

    public void refreshWarps() {
        ParkWarp.getInstance().clearWarps();
        getWarps().forEach(w -> ParkWarp.getInstance().addWarp(w));
    }
}