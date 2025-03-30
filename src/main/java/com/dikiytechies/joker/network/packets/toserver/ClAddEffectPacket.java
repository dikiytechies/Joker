package com.dikiytechies.joker.network.packets.toserver;

import com.dikiytechies.joker.capability.JokerUtilCapProvider;
import com.dikiytechies.joker.client.ui.screen.EffectSelectionScreen;
import com.github.standobyte.jojo.network.packets.IModPacketHandler;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class ClAddEffectPacket {
    private final EffectSelectionScreen.EffectTypes effectType;

    public ClAddEffectPacket(EffectSelectionScreen.EffectTypes effectType) {
        this.effectType = effectType;
    }

    public static class Handler implements IModPacketHandler<ClAddEffectPacket> {
        @Override
        public void encode(ClAddEffectPacket msg, PacketBuffer buf) {
            buf.writeEnum(msg.effectType);
        }

        @Override
        public ClAddEffectPacket decode(PacketBuffer buf) {
            return new ClAddEffectPacket(buf.readEnum(EffectSelectionScreen.EffectTypes.class));
        }

        @Override
        public void handle(ClAddEffectPacket msg, Supplier<NetworkEvent.Context> ctx) {
            NetworkEvent.Context context = ctx.get();
            ServerPlayerEntity player = ctx.get().getSender();
            if (player != null) {
                player.getCapability(JokerUtilCapProvider.CAPABILITY).ifPresent(cap -> cap.setActiveEffect(msg.effectType));
            }
            context.setPacketHandled(true);
        }

        @Override
        public Class<ClAddEffectPacket> getPacketClass() {
            return ClAddEffectPacket.class;
        }
    }
}
