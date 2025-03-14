package com.dikiytechies.joker.power.impl.nonstand.type;

import com.dikiytechies.joker.init.power.non_stand.joker.JokerPowerInit;
import com.github.standobyte.jojo.action.Action;
import com.github.standobyte.jojo.init.power.non_stand.ModPowers;
import com.github.standobyte.jojo.power.impl.nonstand.INonStandPower;
import com.github.standobyte.jojo.power.impl.nonstand.type.NonStandPowerType;
import com.github.standobyte.jojo.power.impl.nonstand.type.hamon.HamonPowerType;
import com.github.standobyte.jojo.power.impl.nonstand.type.vampirism.VampirismPowerType;
import com.github.standobyte.jojo.power.impl.stand.IStandPower;
import com.github.standobyte.jojo.util.mod.JojoModUtil;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nullable;
import java.util.Objects;

public class JokerPowerType extends NonStandPowerType<JokerData> {
    private ResourceLocation[] iconTexture = new ResourceLocation[4];

    public JokerPowerType(Action<INonStandPower>[] startingAttacks, Action<INonStandPower>[] startingAbilities, Action<INonStandPower> defaultQuickAccess) {
        super(startingAttacks, startingAbilities, defaultQuickAccess, JokerData::new);
    }

    @Override
    public void afterClear(INonStandPower power) {
        super.afterClear(power);
    }

    @Override
    public float getMaxEnergy(INonStandPower power) {
        if (INonStandPower.getNonStandPowerOptional(power.getUser()).isPresent()) {
            int stage = INonStandPower.getNonStandPowerOptional(power.getUser()).map(pow -> pow.getTypeSpecificData(JokerPowerInit.JOKER.get()).map(JokerData::getStage)).get().get();
            switch (stage) {
                case 1:
                    return 750;
                case 2:
                    return 1200;
                case 3:
                    return 1500;
                default:
                    return 500;
            }
        }
        return 500;
    }

    @Override
    public void tickUser(LivingEntity entity, INonStandPower power) {
        if (INonStandPower.getNonStandPowerOptional(power.getUser()).isPresent()) {
            if (power.getEnergy() == power.getMaxEnergy() && INonStandPower.getNonStandPowerOptional(power.getUser()).map(pow -> pow.getTypeSpecificData(JokerPowerInit.JOKER.get()).map(JokerData::getStage)).get().get() != 3)
                INonStandPower.getNonStandPowerOptional(power.getUser()).ifPresent(pow -> pow.getTypeSpecificData(JokerPowerInit.JOKER.get()).ifPresent(joker -> joker.setStage(joker.getStage() + 1)));
        }
    }

    @Override
    public float tickEnergy(INonStandPower power) {
        return power.getEnergy();
    }

    @Override
    public boolean isLeapUnlocked(INonStandPower power) {
        if (INonStandPower.getNonStandPowerOptional(power.getUser()).isPresent()) {
            int stage = INonStandPower.getNonStandPowerOptional(power.getUser()).map(pow -> pow.getTypeSpecificData(JokerPowerInit.JOKER.get()).map(JokerData::getStage)).get().get();
            return stage > 1;
        }
        return false;
    }

    @Override
    public void onLeap(INonStandPower power) {
        LivingEntity entity = power.getUser();
        if (!power.getTypeSpecificData(JokerPowerInit.JOKER.get()).map(JokerData::getPreviousPowerType).isPresent() && ((power.getUser() instanceof PlayerEntity && !((PlayerEntity) power.getUser()).abilities.instabuild) || !(power.getUser() instanceof PlayerEntity)))
            entity.hurt(new EntityDamageSource("injure", power.getUser()).bypassArmor().bypassInvul().bypassMagic(), 3.5f);
        entity.hurtMarked = false;
    }


    @Override
    public float getLeapStrength(INonStandPower power) {
        if (power.getTypeSpecificData(JokerPowerInit.JOKER.get()).map(JokerData::getPreviousPowerType).isPresent()) {
            NonStandPowerType<?> oldPower = power.getTypeSpecificData(JokerPowerInit.JOKER.get()).map(JokerData::getPreviousPowerType).get();
            if (oldPower == ModPowers.HAMON.get()) {
                return 2.5f;
            } else if (oldPower == ModPowers.PILLAR_MAN.get()) {
                return 1.5f;
            } else if (oldPower == ModPowers.VAMPIRISM.get()) {
                return 2.0f;
            } else if (oldPower == ModPowers.ZOMBIE.get()) {
                return 1.2f;
            }
        }
        return 2.0f;
    }

    @Override
    public int getLeapCooldownPeriod() { return 25; }
    @Override
    public boolean isReplaceableWith(NonStandPowerType<?> newType) {
        return false;
    }

    @Override
    public boolean keepOnDeath(INonStandPower power) {
        return true;
    }
    @Override
    public ResourceLocation getIconTexture(@Nullable INonStandPower power) {
        if (INonStandPower.getNonStandPowerOptional(power.getUser()).isPresent()) {
            int stage = INonStandPower.getNonStandPowerOptional(power.getUser()).map(pow -> pow.getTypeSpecificData(JokerPowerInit.JOKER.get()).map(JokerData::getStage)).get().get();
            if (iconTexture[stage] == null) {
                iconTexture[stage] = JojoModUtil.makeTextureLocation("power", getRegistryName().getNamespace(),
                        getRegistryName().getPath() + "_" + Objects.toString(stage));
            }
            return this.iconTexture[stage];
        }
        return JojoModUtil.makeTextureLocation("power", getRegistryName().getNamespace(),
                getRegistryName().getPath());
    }
    @Override
    public float getTargetResolveMultiplier(INonStandPower power, IStandPower attackingStand) {
        return 0;
    }
}