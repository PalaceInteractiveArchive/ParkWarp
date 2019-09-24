package network.palace.parkwarp.handlers;

import lombok.Getter;
import lombok.Setter;
import network.palace.core.player.Rank;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

@Setter
public class Warp extends Location {
    @Getter private final String name;
    @Getter private final String server;
    @Getter private Rank rank = null;
    private final String world;

    public Warp(String name, String server, double x, double y, double z, float yaw, float pitch, String world) {
        super(Bukkit.getWorld(world), x, y, z);
        this.name = name;
        this.server = server;
        this.world = world;
    }

    public World getWorld() {
        if (Bukkit.getWorlds().get(0).getName().equals(world)) {
            return Bukkit.getWorld(world);
        }
        return null;
    }

    public String getWorldName() {
        return world;
    }

    @Deprecated
    public Location getLocation() {
        return this;
    }

    public String toDatabaseString() {
        return name + ":" + server + ":" + getX() + ":" + getY() + ":" + getZ() + ":" + getYaw() + ":" + getPitch() + ":" + world;
    }

    @Override
    public String toString() {
        return getName();
    }
}