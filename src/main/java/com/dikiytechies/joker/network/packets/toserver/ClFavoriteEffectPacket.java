package com.dikiytechies.joker.network.packets.toserver;

import com.dikiytechies.joker.capability.JokerUtilCapProvider;
import com.dikiytechies.joker.client.ui.screen.EffectSelectionScreen;
import com.github.standobyte.jojo.network.packets.IModPacketHandler;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class ClFavoriteEffectPacket {
    private final EffectSelectionScreen.EffectTypes effectType;

    public ClFavoriteEffectPacket(EffectSelectionScreen.EffectTypes effectType) {
        this.effectType = effectType;
    }

    public static class Handler implements IModPacketHandler<ClFavoriteEffectPacket> {
        @Override
        public void encode(ClFavoriteEffectPacket msg, PacketBuffer buf) {
            buf.writeEnum(msg.effectType);
        }

        @Override
        public ClFavoriteEffectPacket decode(PacketBuffer buf) {
            return new ClFavoriteEffectPacket(buf.readEnum(EffectSelectionScreen.EffectTypes.class));
        }

        @Override
        public void handle(ClFavoriteEffectPacket msg, Supplier<NetworkEvent.Context> ctx) {
            NetworkEvent.Context context = ctx.get();
            ServerPlayerEntity player = ctx.get().getSender();
            if (player != null) {
                player.getCapability(JokerUtilCapProvider.CAPABILITY).resolve().ifPresent(cap -> {
                    if (msg.effectType != null) {
                        cap.setFavoriteEffect(msg.effectType);
                    }
                });
            }
            context.setPacketHandled(true);
        }

        @Override
        public Class<ClFavoriteEffectPacket> getPacketClass() {
            return ClFavoriteEffectPacket.class;
        }
    }
}
