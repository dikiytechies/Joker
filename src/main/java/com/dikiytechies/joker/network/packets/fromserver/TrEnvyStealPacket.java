package com.dikiytechies.joker.network.packets.fromserver;

import com.dikiytechies.joker.capability.JokerUtilCapProvider;
import com.github.standobyte.jojo.client.ClientUtil;
import com.github.standobyte.jojo.network.packets.IModPacketHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.Map;
import java.util.function.Supplier;
import java.util.jar.Attributes;

public class TrEnvyStealPacket {
    private final int entityId;
    private final double amount;
    private final int ticks;

    public TrEnvyStealPacket(int entityId, double amount, int ticks) {
        this.entityId = entityId;
        this.amount = amount;
        this.ticks = ticks;
    }

    public static class Handler implements IModPacketHandler<TrEnvyStealPacket> {
        @Override
        public void encode(TrEnvyStealPacket msg, PacketBuffer buf) {
            buf.writeInt(msg.entityId);
            buf.writeDouble(msg.amount);
            buf.writeInt(msg.ticks);
        }

        @Override
        public TrEnvyStealPacket decode(PacketBuffer buf) {
            return new TrEnvyStealPacket(buf.readInt(), buf.readDouble(), buf.readInt());
        }

        @Override
        public void handle(TrEnvyStealPacket msg, Supplier<NetworkEvent.Context> ctx) {
            Entity entity = ClientUtil.getEntityById(msg.entityId);
            if (entity instanceof LivingEntity) {
                entity.getCapability(JokerUtilCapProvider.CAPABILITY).ifPresent(cap -> {
                    cap.addStolenAmount(msg.amount, msg.ticks);
                });
            }
        }

        @Override
        public Class<TrEnvyStealPacket> getPacketClass() {
            return TrEnvyStealPacket.class;
        }
    }
}
