package com.dikiytechies.joker.network.packets.fromserver;

import com.dikiytechies.joker.entity.mob.JokerIggyEntity;
import com.github.standobyte.jojo.client.ClientUtil;
import com.github.standobyte.jojo.network.packets.IModPacketHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class TrJokerStatePacket {
    private final int entityId;
    private final boolean sleepy;
    private final int playerId;
    private final JokerDataType type;
    private final int ticks;

    public TrJokerStatePacket(int entityId, boolean sleepy, int playerId, JokerDataType type) {
        this.entityId = entityId;
        this.sleepy = sleepy;
        this.playerId = playerId;
        this.type = type;
        this.ticks = 0;
    }
    public TrJokerStatePacket(int entityId, boolean sleepy, int playerId, JokerDataType type, int ticks) {
        this.entityId = entityId;
        this.sleepy = sleepy;
        this.playerId = playerId;
        this.type = type;
        this.ticks = ticks;
    }

    public static class Handler implements IModPacketHandler<TrJokerStatePacket> {

        @Override
        public void encode(TrJokerStatePacket msg, PacketBuffer buf) {
            buf.writeInt(msg.entityId);
            buf.writeBoolean(msg.sleepy);
            buf.writeInt(msg.playerId);
            buf.writeEnum(msg.type);
            buf.writeInt(msg.ticks);
        }

        @Override
        public TrJokerStatePacket decode(PacketBuffer buf) {
            return new TrJokerStatePacket(buf.readInt(), buf.readBoolean(), buf.readInt(), buf.readEnum(JokerDataType.class), buf.readInt());
        }

        @Override
        public void handle(TrJokerStatePacket msg, Supplier<NetworkEvent.Context> ctx) {
            Entity entity = ClientUtil.getEntityById(msg.entityId);
            Entity player = ClientUtil.getEntityById(msg.playerId);
            if (entity instanceof JokerIggyEntity && player instanceof PlayerEntity) {
                switch (msg.type) {
                    case SLEEPY:
                    ((JokerIggyEntity) entity).setJokerSleepy(msg.sleepy, (PlayerEntity) player);
                    break;
                    case SMOKING:
                        ((JokerIggyEntity) entity).setJokerSmoking(msg.sleepy, (PlayerEntity) player, msg.ticks);
                        break;
                    case COUGHING:
                        ((JokerIggyEntity) entity).setJokerCoughing(msg.sleepy, msg.ticks, (PlayerEntity) player);
                        break;
                }
            }
        }

        @Override
        public Class<TrJokerStatePacket> getPacketClass() {
            return TrJokerStatePacket.class;
        }
    }
    public static enum JokerDataType {
        SLEEPY,
        SMOKING,
        COUGHING
    }
}
