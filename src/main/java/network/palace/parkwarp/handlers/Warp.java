package network.palace.parkwarp.handlers;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

<<<<<<< HEAD
@Getter
@Setter
@AllArgsConstructor
public class Warp {
    private String name;
    private String server;
    private double x;
    private double y;
    private double z;
    private float yaw;
    private float pitch;
    private String world;
=======
@AllArgsConstructor
@Setter
public class Warp {
    @Getter public String name;
    @Getter public String server;
    @Getter public double x;
    @Getter public double y;
    @Getter public double z;
    @Getter public float yaw;
    @Getter public float pitch;
    public String world;
>>>>>>> master

    public World getWorld() {
        if (Bukkit.getWorlds().get(0).getName().equals(world)) {
            return Bukkit.getWorld(world);
        }
        return null;
    }

    public Location getLocation() {
        return new Location(getWorld(), x, y, z, yaw, pitch);
    }

    public static Warp fromDatabaseString(String warpString) {
        if (warpString == null || warpString.equals(""))
            return null;
        String[] tokens = warpString.split(":");
        return new Warp(tokens[0], tokens[1], Double.parseDouble(tokens[2]),
                Double.parseDouble(tokens[3]), Double.parseDouble(tokens[4]),
                Float.parseFloat(tokens[5]), Float.parseFloat(tokens[6]), tokens[7]);
    }

    public String toDatabaseString() {
        return name + ":" + server + ":" + Double.toString(x) + ":" + Double.toString(y) + ":" +
                Double.toString(z) + ":" + Float.toString(yaw) + ":" + Float.toString(pitch) + ":" +
                world;
    }

    @Override
    public String toString() {
        return getName();
    }
}