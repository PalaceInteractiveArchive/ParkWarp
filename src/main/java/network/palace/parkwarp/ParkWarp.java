package network.palace.parkwarp;

import lombok.Getter;
import network.palace.core.plugin.Plugin;
import network.palace.core.plugin.PluginInfo;
import network.palace.parkwarp.commands.*;
import network.palace.parkwarp.dashboard.PacketListener;
import network.palace.parkwarp.utils.WarpUtil;
import org.bukkit.Bukkit;

<<<<<<< HEAD
import java.util.ArrayList;
import java.util.List;

@PluginInfo(name = "ParkWarp", version = "1.0.4", depend = "Core", canReload = true)
public class ParkWarp extends Plugin {
    private static ParkWarp instance;
    private List<Warp> warps = new ArrayList<>();
=======
@PluginInfo(name = "ParkWarp", version = "1.0.5", depend = "Core", canReload = true)
public class ParkWarp extends Plugin {
    @Getter private static ParkWarp instance;
>>>>>>> master
    @Getter private WarpUtil warpUtil;

    @Override
    protected void onPluginEnable() throws Exception {
        instance = this;
        warpUtil = new WarpUtil();
        Bukkit.getPluginManager().registerEvents(new PacketListener(), this);
        warpUtil = new WarpUtil();
        warpUtil.refreshWarps();
        getLogger().info("Warps loaded!");
        registerCommand(new DelWarpCommand());
        registerCommand(new SetWarpCommand());
        registerCommand(new UpdateWarpCommand());
        registerCommand(new WarpCommand());
        registerCommand(new WRLCommand());
    }

    @Override
    public void onPluginDisable() {
<<<<<<< HEAD
        clearWarps();
    }

    public static ParkWarp getInstance() {
        return instance;
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

    public void addWarp(Warp warp) {
        warps.add(warp);
=======
        warpUtil.clearWarps();
>>>>>>> master
    }
}
