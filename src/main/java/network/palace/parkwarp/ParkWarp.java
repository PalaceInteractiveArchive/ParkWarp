package network.palace.parkwarp;

import lombok.Getter;
import network.palace.core.plugin.Plugin;
import network.palace.core.plugin.PluginInfo;
import network.palace.parkwarp.commands.*;
import network.palace.parkwarp.listeners.PacketListener;
import network.palace.parkwarp.listeners.PlayerJoin;
import network.palace.parkwarp.utils.WarpUtil;

@PluginInfo(name = "ParkWarp", version = "1.2.9", depend = "Core", canReload = true)
public class ParkWarp extends Plugin {
    @Getter private static ParkWarp instance;
    @Getter private static WarpUtil warpUtil;

    @Override
    protected void onPluginEnable() throws Exception {
        instance = this;
        warpUtil = new WarpUtil();
        registerListener(new PacketListener());
        registerListener(new PlayerJoin());
        warpUtil.refreshWarps();
        getLogger().info("Warps loaded!");

        registerCommand(new DelWarpCommand());
        registerCommand(new NearbyCommand());
        registerCommand(new SetWarpCommand());
        registerCommand(new ToggleWarpsCommand());
        registerCommand(new UpdateWarpCommand());
        registerCommand(new WarpCommand());
        registerCommand(new WarpInfoCommand());
        registerCommand(new WRLCommand());
    }

    @Override
    public void onPluginDisable() {
        warpUtil.clearWarps();
    }
}
