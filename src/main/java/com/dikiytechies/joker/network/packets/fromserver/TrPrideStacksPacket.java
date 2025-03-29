package com.dikiytechies.joker.network.packets.fromserver;

import com.dikiytechies.joker.capability.JokerUtilCapProvider;
import com.github.standobyte.jojo.client.ClientUtil;
import com.github.standobyte.jojo.network.packets.IModPacketHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class TrPrideStacksPacket {
    private final int entityId;
    private final int stacks;
    private final StackType stackType;

    public TrPrideStacksPacket(int entityId, int stacks, StackType stackType) {
        this.entityId = entityId;
        this.stacks = stacks;
        this.stackType = stackType;
    }
    public static class Handler implements IModPacketHandler<TrPrideStacksPacket> {
        @Override
        public void encode(TrPrideStacksPacket msg, PacketBuffer buf) {
            buf.writeInt(msg.entityId);
            buf.writeInt(msg.stacks);
            buf.writeEnum(msg.stackType);
        }

        @Override
        public TrPrideStacksPacket decode(PacketBuffer buf) {
            return new TrPrideStacksPacket(buf.readInt(), buf.readInt(), buf.readEnum(StackType.class));
        }

        @Override
        public void handle(TrPrideStacksPacket msg, Supplier<NetworkEvent.Context> ctx) {
            Entity entity = ClientUtil.getEntityById(msg.entityId);
            if (entity instanceof LivingEntity) {
                entity.getCapability(JokerUtilCapProvider.CAPABILITY).ifPresent(cap -> {
                    cap.setPrideStacks(msg.stacks, msg.stackType);
                });
            }
        }

        @Override
        public Class<TrPrideStacksPacket> getPacketClass() {
            return TrPrideStacksPacket.class;
        }
    }

    public static enum StackType {
        TARGET,
        ATTACKER,
        TARGET_TICK,
        ATTACKER_TICK
    }
}
