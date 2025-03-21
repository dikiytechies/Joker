package com.dikiytechies.joker.network.packets.fromserver;

import com.dikiytechies.joker.init.power.non_stand.joker.JokerPowerInit;
import com.dikiytechies.joker.power.impl.nonstand.type.JokerData;
import com.github.standobyte.jojo.client.ClientUtil;
import com.github.standobyte.jojo.client.playeranim.anim.ModPlayerAnimations;
import com.github.standobyte.jojo.init.power.non_stand.ModPowers;
import com.github.standobyte.jojo.network.packets.IModPacketHandler;
import com.github.standobyte.jojo.network.packets.fromserver.TrPillarmanDataPacket;
import com.github.standobyte.jojo.power.impl.nonstand.INonStandPower;
import com.github.standobyte.jojo.power.impl.nonstand.type.pillarman.PillarmanData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class TrJokerPillarmanDataPacket {
    private final int entityId;
    private final boolean stoneFormEnabled;
    private final boolean bladesVisible;
    public TrJokerPillarmanDataPacket(int entityId, JokerData data) {
        this(entityId, data.isPillarmanStoneFormEnabled(), data.getPillarmanBladesVisible());
    }

    public TrJokerPillarmanDataPacket(int entityId, boolean stoneFormEnabled, boolean bladesVisible) {
        this.entityId = entityId;
        this.stoneFormEnabled = stoneFormEnabled;
        this.bladesVisible = bladesVisible;
    }

    public static class Handler implements IModPacketHandler<TrJokerPillarmanDataPacket> {
        @Override
        public void encode(TrJokerPillarmanDataPacket msg, PacketBuffer buf) {
            buf.writeInt(msg.entityId);
            buf.writeBoolean(msg.stoneFormEnabled);
            buf.writeBoolean(msg.bladesVisible);
        }

        @Override
        public TrJokerPillarmanDataPacket decode(PacketBuffer buf) {
            return new TrJokerPillarmanDataPacket(buf.readInt(), buf.readBoolean(), buf.readBoolean());
        }

        @Override
        public void handle(TrJokerPillarmanDataPacket msg, Supplier<NetworkEvent.Context> ctx) {
            Entity entity = ClientUtil.getEntityById(msg.entityId);
            if (entity instanceof LivingEntity) {
                INonStandPower.getNonStandPowerOptional((LivingEntity) entity).resolve()
                        .flatMap(power -> power.getTypeSpecificData(JokerPowerInit.JOKER.get()))
                        .ifPresent(joker -> {
                            boolean prevStoneForm = joker.isPillarmanStoneFormEnabled();
                            joker.setPillarmanStoneFormEnabled(msg.stoneFormEnabled);
                            joker.setPillarmanBladesVisible(msg.bladesVisible);
                            if (entity instanceof PlayerEntity) {
                                PlayerEntity userPlayer = (PlayerEntity) entity;
                                ModPlayerAnimations.stoneForm.setAnimEnabled(userPlayer, msg.stoneFormEnabled);
                        /*if (!prevStoneForm && msg.stoneFormEnabled && userPlayer == ClientUtil.getClientPlayer()) {
                            ClientUtil.setThirdPerson();
                        }*/
                            }
                        });
            }
        }

        @Override
        public Class<TrJokerPillarmanDataPacket> getPacketClass() {
            return TrJokerPillarmanDataPacket.class;
        }
    }
}
