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

public class TrJokerDataPacket {
    private final int entityId;
    private final JokerField field;
    private final byte valueInt;

    public static TrJokerDataPacket stage(int entityId, int value) {
        return new TrJokerDataPacket(entityId, JokerField.STAGE, value);
    }

    private TrJokerDataPacket(int entityId, JokerField flag, int valueInt) {
        this.entityId = entityId;
        this.field = flag;
        this.valueInt = (byte) valueInt;
    }



    public static class Handler implements IModPacketHandler<TrJokerDataPacket> {

        @Override
        public void encode(TrJokerDataPacket msg, PacketBuffer buf) {
            buf.writeInt(msg.entityId);
            buf.writeEnum(msg.field);
            switch (msg.field) {
                case STAGE:
                    buf.writeInt(msg.valueInt);
                    break;
            }
        }

        @Override
        public TrJokerDataPacket decode(PacketBuffer buf) {
            int entityId = buf.readInt();
            JokerField field = buf.readEnum(JokerField.class);
            switch (field) {
                case STAGE:
                    return new TrJokerDataPacket(entityId, field, buf.readInt());
            }
            throw new IllegalStateException("Unknown JoJo joker field being sent!");
        }

        @Override
        public void handle(TrJokerDataPacket msg, Supplier<NetworkEvent.Context> ctx) {
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
        public Class<TrJokerDataPacket> getPacketClass() {
            return TrJokerDataPacket.class;
        }
    }

    private static enum JokerField {
        STAGE,
    }
}
