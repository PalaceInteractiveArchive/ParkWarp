package network.palace.parkwarp.handlers;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import network.palace.core.player.Rank;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

@RequiredArgsConstructor
@Setter
public class Warp {
    @Getter private final String name;
    @Getter private final String server;
    @Getter private final double x;
    @Getter private final double y;
    @Getter private final double z;
    @Getter private final float yaw;
    @Getter private final float pitch;
    @Getter private Rank rank = null;
    private final String world;

    public World getWorld() {
        if (Bukkit.getWorlds().get(0).getName().equals(world)) {
            return Bukkit.getWorld(world);
        }
        return null;
    }

    public String getWorldName() {
        return world;
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
        return name + ":" + server + ":" + x + ":" + y + ":" + z + ":" + yaw + ":" + pitch + ":" + world;
    }

    @Override
    public String toString() {
        return getName();
    }
}