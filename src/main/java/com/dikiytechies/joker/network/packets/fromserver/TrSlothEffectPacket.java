package com.dikiytechies.joker.network.packets.fromserver;

import com.dikiytechies.joker.capability.JokerUtilCapProvider;
import com.github.standobyte.jojo.client.ClientUtil;
import com.github.standobyte.jojo.network.packets.IModPacketHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class TrSlothEffectPacket {
    private final int entityId;
    private final boolean swanSong;
    private final float borrowedHealth;

    public TrSlothEffectPacket(int entityId, boolean swanSong, float borrowedHealth) {
        this.entityId = entityId;
        this.swanSong = swanSong;
        this.borrowedHealth = borrowedHealth;
    }

    public static class Handler implements IModPacketHandler<TrSlothEffectPacket> {
        @Override
        public void encode(TrSlothEffectPacket msg, PacketBuffer buf) {
            buf.writeInt(msg.entityId);
            buf.writeBoolean(msg.swanSong);
            buf.writeFloat(msg.borrowedHealth);
        }

        @Override
        public TrSlothEffectPacket decode(PacketBuffer buf) {
            return new TrSlothEffectPacket(buf.readInt(), buf.readBoolean(), buf.readFloat());
        }

        @Override
        public void handle(TrSlothEffectPacket msg, Supplier<NetworkEvent.Context> ctx) {
            Entity entity = ClientUtil.getEntityById(msg.entityId);
            if (entity instanceof LivingEntity) {
                entity.getCapability(JokerUtilCapProvider.CAPABILITY).ifPresent(cap -> {
                    cap.setSwanSong(msg.swanSong);
                    cap.setBorrowedHealth(msg.borrowedHealth);
                    });
                }
        }

        @Override
        public Class<TrSlothEffectPacket> getPacketClass() {
            return TrSlothEffectPacket.class;
        }
    }
}
