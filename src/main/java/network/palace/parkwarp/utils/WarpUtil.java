package network.palace.parkwarp.utils;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import network.palace.core.Core;
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
<<<<<<< HEAD
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
=======
    public List<Warp> warps = new ArrayList<>();

    public boolean warpExistsSql(String warp) {
        try (Connection connection = Core.getSqlUtil().getConnection()) {
            PreparedStatement sql = connection
                    .prepareStatement("SELECT * FROM warps WHERE name = ?");
            sql.setString(1, warp);
            ResultSet result = sql.executeQuery();
            boolean contains = result.next();
            result.close();
            sql.close();
            return contains;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public String getServer(String warp) {
        try (Connection connection = Core.getSqlUtil().getConnection()) {
            PreparedStatement sql = connection
                    .prepareStatement("SELECT * FROM warps WHERE name = ?");
            sql.setString(1, warp);
            ResultSet result = sql.executeQuery();
            result.next();
            String server = result.getString("server");
            result.close();
            sql.close();
            return server;
        } catch (SQLException e) {
            e.printStackTrace();
>>>>>>> master
        }
        return null;
    }

<<<<<<< HEAD
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

=======
    public Location getLocation(String warp) {
        try (Connection connection = Core.getSqlUtil().getConnection()) {
            PreparedStatement sql = connection
                    .prepareStatement("SELECT * FROM warps WHERE name=?");
            sql.setString(1, warp);
            ResultSet result = sql.executeQuery();
            result.next();
            String world = result.getString("world");
            double x = result.getDouble("x");
            double y = result.getDouble("y");
            double z = result.getDouble("z");
            float yaw = result.getFloat("yaw");
            float pitch = result.getFloat("pitch");
            result.close();
            sql.close();
            return new Location(Bukkit.getWorld(world), x, y, z, yaw, pitch);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

>>>>>>> master
    public void crossServerWarp(final UUID uuid, final String warp, final String server) {
        PacketWarp packet = new PacketWarp(uuid, warp, server);
        Core.getDashboardConnection().send(packet.getJSON().toString());
    }

<<<<<<< HEAD
    public List<Warp> getWarps() {
=======
    public List<Warp> getWarpsSql() {
        List<String> names = new ArrayList<>();
>>>>>>> master
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

<<<<<<< HEAD
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
=======
    public void addWarpSql(Warp warp) {
        try (Connection connection = Core.getSqlUtil().getConnection()) {
            PreparedStatement sql = connection
                    .prepareStatement("INSERT INTO warps values(0,?,?,?,?,?,?,?,?)");
            sql.setString(1, warp.getName());
            sql.setDouble(2, warp.getX());
            sql.setDouble(3, warp.getY());
            sql.setDouble(4, warp.getZ());
            sql.setFloat(5, warp.getYaw());
            sql.setFloat(6, warp.getPitch());
            sql.setString(7, warp.getWorld().getName());
            sql.setString(8, warp.getServer());
            sql.execute();
            sql.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void removeWarpSql(Warp warp) {
        try (Connection connection = Core.getSqlUtil().getConnection()) {
            PreparedStatement sql = connection
                    .prepareStatement("DELETE FROM warps WHERE name=?");
            sql.setString(1, warp.getName());
            sql.execute();
            sql.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean warpExists(String name) {
        return findWarp(name) != null;
    }

    public Warp findWarp(String name) {
        for (Warp warp : getWarps()) {
            if (warp.getName().equalsIgnoreCase(name)) return warp;
>>>>>>> master
        }
        return null;
    }

    public void updateWarps() {
        PacketRefreshWarps packet = new PacketRefreshWarps(Core.getInstanceName());
        Core.getDashboardConnection().send(packet);
    }

    public void refreshWarps() {
        clearWarps();
        getWarpsSql().forEach(this::addWarp);
    }

    public List<Warp> getWarps() {
        return new ArrayList<>(warps);
    }

    public void clearWarps() {
        warps.clear();
    }

    public void removeWarp(Warp warp) {
        warps.remove(warp);
    }

<<<<<<< HEAD
    public void refreshWarps() {
        ParkWarp.getInstance().clearWarps();
        getWarps().forEach(w -> ParkWarp.getInstance().addWarp(w));
=======
    public void addWarp(Warp warp) {
        warps.add(warp);
>>>>>>> master
    }
}