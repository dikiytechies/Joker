package com.dikiytechies.joker.network.packets.fromserver;

import com.dikiytechies.joker.capability.JokerUtilCapProvider;
import com.dikiytechies.joker.client.ui.screen.EffectSelectionScreen;
import com.github.standobyte.jojo.client.ClientUtil;
import com.github.standobyte.jojo.network.packets.IModPacketHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class TrActiveEffectPacket {
    private final int entityId;
    private final EffectSelectionScreen.EffectTypes favorite;

    public TrActiveEffectPacket(int entityId, EffectSelectionScreen.EffectTypes favorite) {
        this.entityId = entityId;
        this.favorite = favorite;
    }

    public static class Handler implements IModPacketHandler<TrActiveEffectPacket> {
        @Override
        public void encode(TrActiveEffectPacket msg, PacketBuffer buf) {
            buf.writeInt(msg.entityId);
            buf.writeEnum(msg.favorite);
        }

        @Override
        public TrActiveEffectPacket decode(PacketBuffer buf) {
            return new TrActiveEffectPacket(buf.readInt(), buf.readEnum(EffectSelectionScreen.EffectTypes.class));
        }

        @Override
        public void handle(TrActiveEffectPacket msg, Supplier<NetworkEvent.Context> ctx) {
            Entity entity = ClientUtil.getEntityById(msg.entityId);
            if (entity instanceof LivingEntity) {
                entity.getCapability(JokerUtilCapProvider.CAPABILITY).ifPresent(cap -> {
                    cap.setActiveEffect(msg.favorite);
                });
            }
        }

        @Override
        public Class<TrActiveEffectPacket> getPacketClass() {
            return TrActiveEffectPacket.class;
        }
    }
}
