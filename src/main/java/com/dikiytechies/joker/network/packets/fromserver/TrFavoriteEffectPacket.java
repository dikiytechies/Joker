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

public class TrFavoriteEffectPacket {
    private final int entityId;
    private final EffectSelectionScreen.EffectTypes favorite;

    public TrFavoriteEffectPacket(int entityId, EffectSelectionScreen.EffectTypes favorite) {
        this.entityId = entityId;
        this.favorite = favorite;
    }

    public static class Handler implements IModPacketHandler<TrFavoriteEffectPacket> {
        @Override
        public void encode(TrFavoriteEffectPacket msg, PacketBuffer buf) {
            buf.writeInt(msg.entityId);
            buf.writeEnum(msg.favorite);
        }

        @Override
        public TrFavoriteEffectPacket decode(PacketBuffer buf) {
            return new TrFavoriteEffectPacket(buf.readInt(), buf.readEnum(EffectSelectionScreen.EffectTypes.class));
        }

        @Override
        public void handle(TrFavoriteEffectPacket msg, Supplier<NetworkEvent.Context> ctx) {
            Entity entity = ClientUtil.getEntityById(msg.entityId);
            if (entity instanceof LivingEntity) {
                entity.getCapability(JokerUtilCapProvider.CAPABILITY).ifPresent(cap -> {
                    cap.setFavoriteEffect(msg.favorite);
                });
            }
        }

        @Override
        public Class<TrFavoriteEffectPacket> getPacketClass() {
            return TrFavoriteEffectPacket.class;
        }
    }
}
