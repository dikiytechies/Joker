package com.dikiytechies.joker.network.packets.fromserver;

import com.dikiytechies.joker.capability.JokerUtilCapProvider;
import com.dikiytechies.joker.potion.PrideStatusEffect;
import com.github.standobyte.jojo.client.ClientUtil;
import com.github.standobyte.jojo.network.packets.IModPacketHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class TrPrideMultiCastPacket {
    private final int entityId;
    private final PrideStatusEffect.MultiCastType type;

    public TrPrideMultiCastPacket(int entityId, PrideStatusEffect.MultiCastType type) {
        this.entityId = entityId;
        this.type = type;
    }
    public static class Handler implements IModPacketHandler<TrPrideMultiCastPacket> {
        @Override
        public void encode(TrPrideMultiCastPacket msg, PacketBuffer buf) {
            buf.writeInt(msg.entityId);
            buf.writeEnum(msg.type);
        }

        @Override
        public TrPrideMultiCastPacket decode(PacketBuffer buf) {
            return new TrPrideMultiCastPacket(buf.readInt(), buf.readEnum(PrideStatusEffect.MultiCastType.class));
        }

        @Override
        public void handle(TrPrideMultiCastPacket msg, Supplier<NetworkEvent.Context> ctx) {
            Entity entity = ClientUtil.getEntityById(msg.entityId);
            if (entity instanceof LivingEntity) {
                entity.getCapability(JokerUtilCapProvider.CAPABILITY).ifPresent(cap -> {
                    cap.setPrideMultiCast(msg.type);
                });
            }
        }

        @Override
        public Class<TrPrideMultiCastPacket> getPacketClass() {
            return TrPrideMultiCastPacket.class;
        }
    }
}
