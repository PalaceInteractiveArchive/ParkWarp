package network.palace.parkwarp.listeners;

import com.google.gson.JsonObject;
import network.palace.core.events.IncomingMessageEvent;
import network.palace.parkwarp.ParkWarp;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class PacketListener implements Listener {

    @EventHandler
    public void onIncomingMessage(IncomingMessageEvent event) {
        JsonObject object = event.getPacket();
        if (!object.has("id")) return;
        int id = object.get("id").getAsInt();
        //noinspection SwitchStatementWithTooFewBranches
        switch (id) {
            case 25: {
                ParkWarp.getWarpUtil().refreshWarps();
                break;
            }
        }
    }
}
