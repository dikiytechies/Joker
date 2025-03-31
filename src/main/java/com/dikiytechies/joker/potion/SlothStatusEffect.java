package com.dikiytechies.joker.potion;

import com.dikiytechies.joker.capability.JokerUtilCap;
import com.dikiytechies.joker.capability.JokerUtilCapProvider;
import com.github.standobyte.jojo.potion.StatusEffect;
import com.github.standobyte.jojo.power.impl.nonstand.INonStandPower;
import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.EffectType;

public class SlothStatusEffect extends StatusEffect {
    public SlothStatusEffect(EffectType type, int liquidColor) { super(type, liquidColor); }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        if (entity.getCapability(JokerUtilCapProvider.CAPABILITY).map(JokerUtilCap::isSwanSong).orElse(false) && INonStandPower.getNonStandPowerOptional(entity).map(p -> p.getType() != null).orElse(false)) {
            INonStandPower power = INonStandPower.getNonStandPowerOptional(entity).resolve().get();
            final float penalty = 3f / (amplifier + 0.5f);
            /*if (power.getEnergy() <= penalty) {
                entity.removeEffect(this);
                entity.getCapability(JokerUtilCapProvider.CAPABILITY).ifPresent(cap -> {
                    if (!(entity instanceof PlayerEntity) || (entity instanceof PlayerEntity && !((PlayerEntity)entity).abilities.instabuild) && !(entity.isSpectator())) {
                        cap.setSwanSong(false);
                        System.out.println(cap.getBorrowedHealth());
                        entity.hurt(DamageSource.WITHER.bypassMagic().bypassArmor().bypassInvul(), cap.getBorrowedHealth());
                    }
                    cap.setBorrowedHealth(0.0f);
                });
            } else */if (!power.consumeEnergy(penalty)) {
                power.setEnergy(0);
            }
        }
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) { return true; }
}
