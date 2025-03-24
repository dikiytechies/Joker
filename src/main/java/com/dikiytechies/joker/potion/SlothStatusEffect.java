package com.dikiytechies.joker.potion;

import com.dikiytechies.joker.capability.JokerUtilCap;
import com.dikiytechies.joker.capability.JokerUtilCapProvider;
import com.github.standobyte.jojo.potion.StatusEffect;
import com.github.standobyte.jojo.power.impl.nonstand.INonStandPower;
import com.github.standobyte.jojo.power.impl.nonstand.NonStandPower;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.EffectType;
import net.minecraft.util.DamageSource;

public class SlothStatusEffect extends StatusEffect {
    public SlothStatusEffect(EffectType type, int liquidColor) { super(type, liquidColor); }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        if (!entity.level.isClientSide() && entity.getCapability(JokerUtilCapProvider.CAPABILITY).map(JokerUtilCap::isSwanSong).orElse(false) && INonStandPower.getNonStandPowerOptional(entity).map(p -> p.getType() != null).orElse(false)) {
            INonStandPower power = INonStandPower.getNonStandPowerOptional(entity).resolve().get();
            final float penalty = 6.48f / (amplifier + 1);
            if (power.getEnergy() <= penalty) {
                entity.removeEffect(this);
                entity.getCapability(JokerUtilCapProvider.CAPABILITY).ifPresent(cap -> {
                    if (!(entity instanceof PlayerEntity) || (entity instanceof PlayerEntity && !((PlayerEntity)entity).abilities.instabuild) && !(entity.isSpectator())) {
                        cap.setSwanSong(false);
                        entity.hurt(DamageSource.MAGIC.bypassMagic().bypassArmor().bypassInvul(), cap.getBorrowedHealth());
                    }
                    cap.setBorrowedHealth(0.0f);
                });
            } else power.consumeEnergy(penalty);
        }
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) { return true; }
}
