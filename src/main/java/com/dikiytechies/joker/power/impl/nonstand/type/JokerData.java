package com.dikiytechies.joker.power.impl.nonstand.type;

import com.dikiytechies.joker.AddonMain;
import com.dikiytechies.joker.network.AddonPackets;
import com.dikiytechies.joker.network.packets.fromserver.TrJokerDataPacket;
import com.dikiytechies.joker.network.packets.fromserver.TrJokerPreviousPowerDataSaverPacket;
import com.dikiytechies.joker.network.packets.fromserver.TrSociopathyPacket;
import com.github.standobyte.jojo.init.power.JojoCustomRegistries;
import com.github.standobyte.jojo.power.IPowerType;
import com.github.standobyte.jojo.power.impl.nonstand.TypeSpecificData;
import com.github.standobyte.jojo.power.impl.nonstand.type.NonStandPowerType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;
import org.jetbrains.annotations.Nullable;

@Mod.EventBusSubscriber(modid = AddonMain.MOD_ID)
public class JokerData extends TypeSpecificData {
    private int stage;
    private NonStandPowerType<?> previousPowerType;
    private TypeSpecificData oldData;
    private boolean isSociopathyEnabled;
//TODO previous data saving + previous data buffs
    @Override
    public void onPowerGiven(@Nullable NonStandPowerType<?> oldType, @Nullable TypeSpecificData oldData) {
        if (!power.getUser().level.isClientSide()) {
            this.previousPowerType = oldType;
            this.oldData = oldData;
        }
        super.onPowerGiven(oldType, oldData);
        power.setEnergy(0);
    }

    public void setPreviousPowerType(NonStandPowerType<?> power) {this.previousPowerType = power; }
    public NonStandPowerType<?> getPreviousPowerType() { return this.previousPowerType; }
    public void setPreviousData(TypeSpecificData data) { this.oldData = data; }
    public void setStage(int stage) {
        this.stage = stage;
    }
    public int getStage() {
        return stage;
    }
    public boolean toggleSociopathy() {
        setSociopathy(!isSociopathyEnabled);
        return isSociopathyEnabled;
    }
    public boolean isSociopathyEnabled() { return isSociopathyEnabled; }
    public void setSociopathy(boolean isEnabled) {
        if (this.isSociopathyEnabled != isEnabled) {
            this.isSociopathyEnabled = isEnabled;
            LivingEntity user = power.getUser();
            if (!user.level.isClientSide()) {
                AddonPackets.sendToClientsTrackingAndSelf(new TrSociopathyPacket(user.getId(), this), user);
            }
        }
    }

    @Override
    public CompoundNBT writeNBT() {
        CompoundNBT nbt = new CompoundNBT();
        nbt.putInt("Stage", stage);
        if (previousPowerType != null) nbt.putString("PreviousPowerType", JojoCustomRegistries.NON_STAND_POWERS.getKeyAsString(previousPowerType));
        if (previousPowerType != null && oldData != null) {
            nbt.put("PreviousData", (oldData).writeNBT());
        }
        return nbt;
    }

    @Override
    public void readNBT(CompoundNBT nbt) {
        stage = nbt.getInt("Stage");
        IForgeRegistry<NonStandPowerType<?>> powerTypeRegistry = JojoCustomRegistries.NON_STAND_POWERS.getRegistry();
        String powerName = nbt.getString("PreviousPowerType");
        if (powerName != IPowerType.NO_POWER_NAME) {
            previousPowerType = powerTypeRegistry.getValue(new ResourceLocation(powerName));
//            if (previousPowerType != null) {
//                oldData = INonStandPower.getNonStandPowerOptional(this.power.getUser()).map(p -> p.getTypeSpecificData(previousPowerType).map(d -> {
//                    d.readNBT(nbt.getCompound("PreviousData"));
//                    return d;
//                }).get()).get();
//            }
        }
    }//data get entity Dev ForgeCaps."jojo:non_stand".AdditionalData

    @Override
    public void syncWithUserOnly(ServerPlayerEntity user) { }

    @Override
    public void syncWithTrackingOrUser(LivingEntity user, ServerPlayerEntity entity) {
        AddonPackets.sendToClient(TrJokerDataPacket.stage(user.getId(), stage), entity);
        AddonPackets.sendToClient(new TrJokerPreviousPowerDataSaverPacket(user.getId(), previousPowerType/*, oldData*/), entity);
    }
}
