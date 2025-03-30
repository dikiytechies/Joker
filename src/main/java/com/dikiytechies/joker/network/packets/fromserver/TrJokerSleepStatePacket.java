package com.dikiytechies.joker.network.packets.fromserver;

import com.dikiytechies.joker.entity.mob.JokerIggyEntity;
import com.github.standobyte.jojo.client.ClientUtil;
import com.github.standobyte.jojo.network.packets.IModPacketHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class TrJokerSleepStatePacket {
    private final int entityId;
    private final boolean sleepy;
    private final int playerId;

    public TrJokerSleepStatePacket(int entityId, boolean sleepy, int playerId) {
        this.entityId = entityId;
        this.sleepy = sleepy;
        this.playerId = playerId;
    }

    public static class Handler implements IModPacketHandler<TrJokerSleepStatePacket> {

        @Override
        public void encode(TrJokerSleepStatePacket msg, PacketBuffer buf) {
            buf.writeInt(msg.entityId);
            buf.writeBoolean(msg.sleepy);
            buf.writeInt(msg.playerId);
        }

        @Override
        public TrJokerSleepStatePacket decode(PacketBuffer buf) {
            return new TrJokerSleepStatePacket(buf.readInt(), buf.readBoolean(), buf.readInt());
        }

        @Override
        public void handle(TrJokerSleepStatePacket msg, Supplier<NetworkEvent.Context> ctx) {
            Entity entity = ClientUtil.getEntityById(msg.entityId);
            Entity player = ClientUtil.getEntityById(msg.playerId);
            if (entity instanceof JokerIggyEntity && player instanceof PlayerEntity) {
                ((JokerIggyEntity) entity).setJokerSleepy(msg.sleepy, (PlayerEntity) player);
            }
        }

        @Override
        public Class<TrJokerSleepStatePacket> getPacketClass() {
            return TrJokerSleepStatePacket.class;
        }
    }
}
