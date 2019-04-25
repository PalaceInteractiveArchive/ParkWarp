package network.palace.parkwarp.dashboard;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import network.palace.core.Core;
import network.palace.core.events.IncomingPacketEvent;
import network.palace.parkwarp.ParkWarp;
import network.palace.parkwarp.dashboard.packets.parks.PacketRefreshWarps;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * Created by Marc on 9/18/16
 */
public class PacketListener implements Listener {

    @EventHandler
    public void onIncomingPacket(IncomingPacketEvent event) {
        String data = event.getPacket();
        JsonObject object = (JsonObject) new JsonParser().parse(data);
        if (!object.has("id")) {
            return;
        }
        int id = object.get("id").getAsInt();
        /**
         * Refresh Warps
         */
        if (id == 62) {
            PacketRefreshWarps packet = new PacketRefreshWarps().fromJSON(object);
            if (packet.getServer().equals(Core.getInstanceName())) return;
            ParkWarp.getWarpUtil().refreshWarps();
        }
    }
}