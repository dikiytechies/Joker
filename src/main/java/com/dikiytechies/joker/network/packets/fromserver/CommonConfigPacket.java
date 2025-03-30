package com.dikiytechies.joker.network.packets.fromserver;

import com.dikiytechies.joker.AddonConfig;
import com.github.standobyte.jojo.network.packets.IModPacketHandler;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class CommonConfigPacket {
    private final AddonConfig.Common.SyncedValues values;

    public CommonConfigPacket(AddonConfig.Common.SyncedValues values) {
        this.values = values;
    }

    public static class Handler implements IModPacketHandler<CommonConfigPacket> {

        @Override
        public void encode(CommonConfigPacket msg, PacketBuffer buf) {
            msg.values.writeToBuf(buf);
        }

        @Override
        public CommonConfigPacket decode(PacketBuffer buf) {
            return new CommonConfigPacket(new AddonConfig.Common.SyncedValues(buf));
        }

        @Override
        public void handle(CommonConfigPacket msg, Supplier<NetworkEvent.Context> ctx) {
            msg.values.changeConfigValues();
        }

        @Override
        public Class<CommonConfigPacket> getPacketClass() {
            return CommonConfigPacket.class;
        }
    }
}