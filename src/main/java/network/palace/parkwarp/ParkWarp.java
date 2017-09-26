package network.palace.parkwarp;

import lombok.Getter;
import network.palace.core.plugin.Plugin;
import network.palace.core.plugin.PluginInfo;
import network.palace.parkwarp.commands.*;
import network.palace.parkwarp.dashboard.PacketListener;
import network.palace.parkwarp.handlers.Warp;
import network.palace.parkwarp.utils.WarpUtil;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.List;

@PluginInfo(name = "ParkWarp", version = "1.0.4", depend = "Core", canReload = true)
public class ParkWarp extends Plugin {
    private static ParkWarp instance;
    private List<Warp> warps = new ArrayList<>();
    @Getter private WarpUtil warpUtil;

    @Override
    protected void onPluginEnable() throws Exception {
        this.instance = this;
        Bukkit.getPluginManager().registerEvents(new PacketListener(), this);
        warpUtil = new WarpUtil();
        warpUtil.refreshWarps();
        getLogger().info("Warps loaded!");
        registerCommand(new Commanddelwarp());
        registerCommand(new Commandsetwarp());
        registerCommand(new Commanduwarp());
        registerCommand(new Commandwarp());
        registerCommand(new Commandwrl());
    }

    @Override
    public void onPluginDisable() {
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
    }
}
