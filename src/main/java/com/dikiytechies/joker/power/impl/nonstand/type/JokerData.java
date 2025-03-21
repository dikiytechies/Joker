package com.dikiytechies.joker.power.impl.nonstand.type;

import com.dikiytechies.joker.AddonMain;
import com.dikiytechies.joker.init.power.non_stand.joker.JokerPowerInit;
import com.dikiytechies.joker.network.AddonPackets;
import com.dikiytechies.joker.network.packets.fromserver.TrJokerStageDataPacket;
import com.dikiytechies.joker.network.packets.fromserver.TrJokerPillarmanDataPacket;
import com.dikiytechies.joker.network.packets.fromserver.TrJokerPreviousPowerDataSaverPacket;
import com.dikiytechies.joker.network.packets.fromserver.TrSociopathyPacket;
import com.github.standobyte.jojo.init.ModStatusEffects;
import com.github.standobyte.jojo.init.power.JojoCustomRegistries;
import com.github.standobyte.jojo.init.power.non_stand.ModPowers;
import com.github.standobyte.jojo.network.PacketManager;
import com.github.standobyte.jojo.power.IPowerType;
import com.github.standobyte.jojo.power.impl.nonstand.TypeSpecificData;
import com.github.standobyte.jojo.power.impl.nonstand.type.NonStandPowerType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
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
    private int lastEnergyLevel = Integer.MIN_VALUE;
    private boolean pillarmanStoneFormEnabled;
    private boolean pillarmanBladesVisible;
//TODO previous data buffs + kars alt heavy and dash
    @Override
    public void onPowerGiven(@Nullable NonStandPowerType<?> oldType, @Nullable TypeSpecificData oldData) {
        this.previousPowerType = oldType;
        this.oldData = oldData;
        super.onPowerGiven(oldType, oldData);
        power.setEnergy(0);
    }
    public void pillarmanTick() {
        if (power.getTypeSpecificData(JokerPowerInit.JOKER.get()).map(d -> d.getPreviousPowerType() == ModPowers.PILLAR_MAN.get()).orElse(false)) {
            LivingEntity user = power.getUser();
            if (!user.isAlive()) {
                pillarmanStoneFormEnabled = false;
                pillarmanBladesVisible = false;
            }
            if (!user.level.isClientSide()) {
                if (isPillarmanStoneFormEnabled()) {
                    user.addEffect(new EffectInstance(ModStatusEffects.STUN.get(), 20, 0, false, false, true));
                    user.addEffect(new EffectInstance(Effects.DAMAGE_RESISTANCE, 40, 3, false, false, true));
                    user.addEffect(new EffectInstance(Effects.BLINDNESS, 40, 0, false, false, true));
                    user.addEffect(new EffectInstance(Effects.FIRE_RESISTANCE, 40, 0, false, false, true));
                }
            }
        }
    }
    public boolean togglePillarmanStoneForm() {
        setPillarmanStoneFormEnabled(!pillarmanStoneFormEnabled);
        return pillarmanStoneFormEnabled;
    }

    public void setPillarmanStoneFormEnabled(boolean isEnabled) {
        if (this.pillarmanStoneFormEnabled != isEnabled) {
            this.pillarmanStoneFormEnabled = isEnabled;
            LivingEntity user = power.getUser();
            if (!user.level.isClientSide()) {
                AddonPackets.sendToClientsTrackingAndSelf(new TrJokerPillarmanDataPacket(user.getId(), this), user);
            }
        }
    }
    public boolean isPillarmanStoneFormEnabled() { return pillarmanStoneFormEnabled; }
    public void setPillarmanBladesVisible(boolean visible) {
        this.pillarmanBladesVisible = visible;
        LivingEntity user = power.getUser();
        if (!user.level.isClientSide()) {
            AddonPackets.sendToClientsTrackingAndSelf(new TrJokerPillarmanDataPacket(user.getId(), this), user);
        }
    }

    public boolean getPillarmanBladesVisible() {
        return pillarmanBladesVisible;
    }
    public void setPreviousPowerType(NonStandPowerType<?> power) {this.previousPowerType = power; }
    public NonStandPowerType<?> getPreviousPowerType() { return this.previousPowerType; }
    public void setPreviousData(TypeSpecificData data) { this.oldData = data; }
    public TypeSpecificData getPreviousData() { return this.oldData; }
    public CompoundNBT getPreviousDataNbt() { return this.oldData.writeNBT(); }
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
        if (oldData != null) {
            nbt.put("PreviousData", (oldData).writeNBT());
        }
        if (previousPowerType == ModPowers.PILLAR_MAN.get()) nbt.putBoolean("PillarmanStoneForm", pillarmanStoneFormEnabled);
        return nbt;
    }

    @Override
    public void readNBT(CompoundNBT nbt) {
        stage = nbt.getInt("Stage");
        IForgeRegistry<NonStandPowerType<?>> powerTypeRegistry = JojoCustomRegistries.NON_STAND_POWERS.getRegistry();
        String powerName = nbt.getString("PreviousPowerType");
        if (!powerName.equals(IPowerType.NO_POWER_NAME)) {
            previousPowerType = powerTypeRegistry.getValue(new ResourceLocation(powerName));
            if (previousPowerType != null) {
                CompoundNBT oldDataNBT = nbt.getCompound("PreviousData");
                oldData = previousPowerType.newSpecificDataInstance();
                oldData.readNBT(oldDataNBT);
            }
            if (previousPowerType == ModPowers.PILLAR_MAN.get()) pillarmanStoneFormEnabled = nbt.getBoolean("PillarmanStoneFrom");
        }
    }//data get entity Dev ForgeCaps."jojo:non_stand".AdditionalData
    public boolean refreshVampBloodLevel(int bloodLevel) {
        boolean bloodLevelChanged = this.lastEnergyLevel != bloodLevel;
        this.lastEnergyLevel = bloodLevel;
        return bloodLevelChanged;
    }
    @Override
    public void syncWithUserOnly(ServerPlayerEntity user) { lastEnergyLevel = Integer.MIN_VALUE; }

    @Override
    public void syncWithTrackingOrUser(LivingEntity user, ServerPlayerEntity entity) {
        AddonPackets.sendToClient(TrJokerStageDataPacket.stage(user.getId(), stage), entity);
        AddonPackets.sendToClient(new TrJokerPreviousPowerDataSaverPacket(user.getId(), previousPowerType, oldData), entity);
        if (previousPowerType == ModPowers.PILLAR_MAN.get()) AddonPackets.sendToClient(new TrJokerPillarmanDataPacket(user.getId(), this), entity);
    }
}
