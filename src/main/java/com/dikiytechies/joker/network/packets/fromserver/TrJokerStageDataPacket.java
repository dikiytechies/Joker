package com.dikiytechies.joker.network.packets.fromserver;

import com.dikiytechies.joker.init.power.non_stand.joker.JokerPowerInit;
import com.github.standobyte.jojo.client.ClientUtil;
import com.github.standobyte.jojo.network.packets.IModPacketHandler;
import com.github.standobyte.jojo.power.impl.nonstand.INonStandPower;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class TrJokerStageDataPacket {
    private final int entityId;
    private final JokerField field;
    private final byte valueInt;

    public static TrJokerStageDataPacket stage(int entityId, int value) {
        return new TrJokerStageDataPacket(entityId, JokerField.STAGE, value);
    }

    private TrJokerStageDataPacket(int entityId, JokerField flag, int valueInt) {
        this.entityId = entityId;
        this.field = flag;
        this.valueInt = (byte) valueInt;
    }



    public static class Handler implements IModPacketHandler<TrJokerStageDataPacket> {

        @Override
        public void encode(TrJokerStageDataPacket msg, PacketBuffer buf) {
            buf.writeInt(msg.entityId);
            buf.writeEnum(msg.field);
            switch (msg.field) {
                case STAGE:
                    buf.writeInt(msg.valueInt);
                    break;
            }
        }

        @Override
        public TrJokerStageDataPacket decode(PacketBuffer buf) {
            int entityId = buf.readInt();
            JokerField field = buf.readEnum(JokerField.class);
            switch (field) {
                case STAGE:
                    return new TrJokerStageDataPacket(entityId, field, buf.readInt());
            }
            throw new IllegalStateException("Unknown JoJo joker field being sent!");
        }

        @Override
        public void handle(TrJokerStageDataPacket msg, Supplier<NetworkEvent.Context> ctx) {
            Entity entity = ClientUtil.getEntityById(msg.entityId);
            if (entity instanceof LivingEntity) {
                INonStandPower.getNonStandPowerOptional((LivingEntity) entity).ifPresent(power -> {
                    switch(msg.field) {
                        case STAGE:
                            power.getTypeSpecificData(JokerPowerInit.JOKER.get()).ifPresent(joker -> {
                                joker.setStage(msg.valueInt);
                            });
                            break;
                    }
                });
            }
        }

        @Override
        public Class<TrJokerStageDataPacket> getPacketClass() {
            return TrJokerStageDataPacket.class;
        }
    }

    private static enum JokerField {
        STAGE,
    }
}
