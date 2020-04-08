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
        super(Bukkit.getWorld(world), x, y, z, yaw, pitch);
        this.name = name;
        switch (server.toLowerCase()) {
            case "ttc":
            case "mk":
            case "epcot":
            case "dhs":
            case "ak":
            case "typhoon":
            case "resorts":
            case "dcl":
            case "seasonal":
                this.server = "WDW";
                break;
            default:
                this.server = server;
        }
        this.world = world;
    }

    public World getWorld() {
        try {
            return Bukkit.getWorld(world);
        } catch (Exception e) {
            return null;
        }
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