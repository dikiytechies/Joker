package com.dikiytechies.joker.network.packets.fromserver;

import com.dikiytechies.joker.capability.JokerUtilCapProvider;
import com.github.standobyte.jojo.client.ClientUtil;
import com.github.standobyte.jojo.network.packets.IModPacketHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class TrSlothDebuffPacket {
    private final int entityId;
    private final float delayedDamage;

    public TrSlothDebuffPacket(int entityId, float delayedDamage) {
        this.entityId = entityId;
        this.delayedDamage = delayedDamage;
    }

    public static class Handler implements IModPacketHandler<TrSlothDebuffPacket> {
        @Override
        public void encode(TrSlothDebuffPacket msg, PacketBuffer buf) {
            buf.writeInt(msg.entityId);
            buf.writeFloat(msg.delayedDamage);
        }

        @Override
        public TrSlothDebuffPacket decode(PacketBuffer buf) {
            return new TrSlothDebuffPacket(buf.readInt(), buf.readFloat());
        }

        @Override
        public void handle(TrSlothDebuffPacket msg, Supplier<NetworkEvent.Context> ctx) {
            Entity entity = ClientUtil.getEntityById(msg.entityId);
            if (entity instanceof LivingEntity) {
                entity.getCapability(JokerUtilCapProvider.CAPABILITY).ifPresent(cap -> {
                    cap.addDelayedDamage(msg.delayedDamage);
                });
            }
        }

        @Override
        public Class<TrSlothDebuffPacket> getPacketClass() {
            return TrSlothDebuffPacket.class;
        }
    }
}
