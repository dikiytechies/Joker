package com.dikiytechies.joker.network.packets.fromserver;

import com.dikiytechies.joker.init.power.non_stand.joker.JokerPowerInit;
import com.dikiytechies.joker.power.impl.nonstand.type.JokerData;
import com.github.standobyte.jojo.client.ClientUtil;
import com.github.standobyte.jojo.network.packets.IModPacketHandler;
import com.github.standobyte.jojo.power.impl.nonstand.INonStandPower;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class TrSociopathyPacket {
    private final int entityId;
    private final boolean sociopathyEnabled;

    public TrSociopathyPacket(int entityId, JokerData jokerData) {
        this(entityId, jokerData.isSociopathyEnabled());
    }

    public TrSociopathyPacket(int entityId, boolean sociopathyEnabled) {
        this.entityId = entityId;
        this.sociopathyEnabled = sociopathyEnabled;
    }



    public static class Handler implements IModPacketHandler<TrSociopathyPacket> {

        @Override
        public void encode(TrSociopathyPacket msg, PacketBuffer buf) {
            buf.writeInt(msg.entityId);
            buf.writeBoolean(msg.sociopathyEnabled);
        }

        @Override
        public TrSociopathyPacket decode(PacketBuffer buf) {
            return new TrSociopathyPacket(buf.readInt(), buf.readBoolean());
        }

        @Override
        public void handle(TrSociopathyPacket msg, Supplier<NetworkEvent.Context> ctx) {
            Entity entity = ClientUtil.getEntityById(msg.entityId);
            if (entity instanceof LivingEntity) {
                INonStandPower.getNonStandPowerOptional((LivingEntity) entity).ifPresent(power -> {
                    power.getTypeSpecificData(JokerPowerInit.JOKER.get()).ifPresent(joker -> {
                        joker.setSociopathy(msg.sociopathyEnabled);
                    });
                });
            }
        }

        @Override
        public Class<TrSociopathyPacket> getPacketClass() {
            return TrSociopathyPacket.class;
        }
    }
}
