package network.palace.parkwarp.packets;

import com.google.gson.JsonObject;
import network.palace.core.messagequeue.packets.MQPacket;

public class RefreshWarpsPacket extends MQPacket {

    public RefreshWarpsPacket(JsonObject object) {
        super(PacketID.Global.REFRESH_WARPS.getId(), object);
    }

    public RefreshWarpsPacket() {
        super(PacketID.Global.REFRESH_WARPS.getId(), null);
    }

    @Override
    public JsonObject getJSON() {
        return getBaseJSON();
    }
}