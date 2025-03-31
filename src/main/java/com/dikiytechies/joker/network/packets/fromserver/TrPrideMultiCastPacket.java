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
    private final float damage;
    private final LivingEntity target;

    public TrPrideMultiCastPacket(int entityId, PrideStatusEffect.MultiCastType type, float damage, LivingEntity target) {
        this.entityId = entityId;
        this.type = type;
        this.damage = damage;
        this.target = target;
    }
    public static class Handler implements IModPacketHandler<TrPrideMultiCastPacket> {
        @Override
        public void encode(TrPrideMultiCastPacket msg, PacketBuffer buf) {
            buf.writeInt(msg.entityId);
            buf.writeEnum(msg.type);
            buf.writeFloat(msg.damage);
            if (msg.target != null) {
                buf.writeInt(msg.target.getId());
            } else buf.writeInt(-1);
        }

        @Override
        public TrPrideMultiCastPacket decode(PacketBuffer buf) {
            int id = buf.readInt();
            PrideStatusEffect.MultiCastType type = buf.readEnum(PrideStatusEffect.MultiCastType.class);
            float damage = buf.readFloat();
            int tId = buf.readInt();
            if (tId != -1) {
                return new TrPrideMultiCastPacket(id, type, damage, (LivingEntity) ClientUtil.getEntityById(tId));
            } else return new TrPrideMultiCastPacket(id, type, damage, null);
        }

        @Override
        public void handle(TrPrideMultiCastPacket msg, Supplier<NetworkEvent.Context> ctx) {
            Entity entity = ClientUtil.getEntityById(msg.entityId);
            if (entity instanceof LivingEntity) {
                entity.getCapability(JokerUtilCapProvider.CAPABILITY).ifPresent(cap -> {
                    cap.setPrideMultiCast(msg.type, msg.damage, msg.target);
                });
            }
        }

        @Override
        public Class<TrPrideMultiCastPacket> getPacketClass() {
            return TrPrideMultiCastPacket.class;
        }
    }
}
