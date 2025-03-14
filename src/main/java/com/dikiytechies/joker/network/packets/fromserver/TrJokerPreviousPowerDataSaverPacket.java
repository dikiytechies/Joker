package com.dikiytechies.joker.network.packets.fromserver;

import com.dikiytechies.joker.init.power.non_stand.joker.JokerPowerInit;
import com.github.standobyte.jojo.client.ClientUtil;
import com.github.standobyte.jojo.network.packets.IModPacketHandler;
import com.github.standobyte.jojo.power.impl.nonstand.INonStandPower;
import com.github.standobyte.jojo.power.impl.nonstand.type.NonStandPowerType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class TrJokerPreviousPowerDataSaverPacket {
    private final int entityId;
    private final NonStandPowerType<?> powerType;
    //private final TypeSpecificData data;

    public TrJokerPreviousPowerDataSaverPacket(int entityId, NonStandPowerType<?> powerType/*, TypeSpecificData data*/) {
        this.entityId = entityId;
        this.powerType = powerType;
        //this.data = data;
    }

    public static class Handler implements IModPacketHandler<TrJokerPreviousPowerDataSaverPacket> {
        @Override
        public void encode(TrJokerPreviousPowerDataSaverPacket msg, PacketBuffer buf) {
            buf.writeInt(msg.entityId);
            boolean noPower = msg.powerType == null;
            buf.writeBoolean(noPower);
            if (!noPower) {
                buf.writeRegistryId(msg.powerType);
                //buf.writeUtf(msg.data.getClass().getName());
                //buf.writeNbt(msg.data.writeNBT());
            }
        }

        @Override
        public TrJokerPreviousPowerDataSaverPacket decode(PacketBuffer buf) {
            int entityId = buf.readInt();
            boolean noPower = buf.readBoolean();
            if (noPower) return new TrJokerPreviousPowerDataSaverPacket(entityId, null/*, null*/);
            NonStandPowerType<?> power = buf.readRegistryIdSafe(NonStandPowerType.class);
            //String dataClass = buf.readUtf();
            //try {
                //Class<?> clazz = Class.forName(dataClass);
                //Object data = clazz.getConstructor().newInstance();
                //if (data instanceof TypeSpecificData) {
                    //((TypeSpecificData) data).readNBT(buf.readNbt());
                    return new TrJokerPreviousPowerDataSaverPacket(entityId, power/*, ((TypeSpecificData)data)*/);
                //}
            //} catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException |
            //         InvocationTargetException e) {
            //    throw new RuntimeException(e);
            //}
            //return null;
        }

        @Override
        public void handle(TrJokerPreviousPowerDataSaverPacket msg, Supplier<NetworkEvent.Context> ctx) {
            Entity entity = ClientUtil.getEntityById(msg.entityId);
            if (entity instanceof LivingEntity) {
                INonStandPower.getNonStandPowerOptional((LivingEntity) entity).ifPresent(p -> p.getTypeSpecificData(JokerPowerInit.JOKER.get()).ifPresent(d -> d.setPreviousPowerType(msg.powerType)));
                //INonStandPower.getNonStandPowerOptional((LivingEntity) entity).ifPresent(p -> p.getTypeSpecificData(JokerPowerInit.JOKER.get()).ifPresent(d -> d.setPreviousData(msg.data)));
            }
        }
        @Override
        public Class<TrJokerPreviousPowerDataSaverPacket> getPacketClass() {
            return TrJokerPreviousPowerDataSaverPacket.class;
        }
    }
}
