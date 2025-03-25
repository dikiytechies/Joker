package com.dikiytechies.joker.potion;

import com.dikiytechies.joker.init.power.non_stand.joker.JokerPowerInit;
import com.github.standobyte.jojo.potion.StatusEffect;
import com.github.standobyte.jojo.power.impl.nonstand.INonStandPower;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.EffectType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityPredicates;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class LustStatusEffect extends StatusEffect {
    public LustStatusEffect(EffectType type, int liquidColor) {
        super(type, liquidColor);
        addAttributeModifier(Attributes.MOVEMENT_SPEED, "f8b39b41-2412-4408-a328-99751a7b2448", 0.35, AttributeModifier.Operation.MULTIPLY_TOTAL);
        addAttributeModifier(Attributes.ATTACK_SPEED, "517a41c4-3245-4488-811d-ec3458e54d1a", 0.35, AttributeModifier.Operation.MULTIPLY_TOTAL);
    }

    @Override
    public void applyEffectTick(LivingEntity livingEntity, int amplifier) {
        if (!livingEntity.level.isClientSide()) {
            livingEntity.level.getEntitiesOfClass(LivingEntity.class, livingEntity.getBoundingBox().inflate(1.25),
                    entity -> (livingEntity.getHealth() > 0) && !entity.is(livingEntity) && (!(entity instanceof PlayerEntity) || !entity.isSpectator() && !((PlayerEntity)entity).isCreative())).forEach(entity -> {
                byte negative = (livingEntity.getHealth() - entity.getHealth()) > 0? (byte) 1: -1;
                float healthDifference = negative * Math.min(Math.max(livingEntity.getHealth(), entity.getHealth()) / Math.min(livingEntity.getHealth(), entity.getHealth()), 0.5f);
                final float damageHealAmount = (float) Math.pow(healthDifference, 3) / 160.0f * (amplifier + 1);
                if (negative == 1) {
                    entity.hurt(DamageSource.thorns(livingEntity).bypassInvul().bypassArmor().bypassMagic(), Math.min(4.5f + Math.abs(damageHealAmount), entity.getMaxHealth() * 0.65f));
                    livingEntity.setHealth(0.12f + livingEntity.getHealth());
                } else if (negative == -1 && INonStandPower.getNonStandPowerOptional(livingEntity).map(p -> p.getType() == JokerPowerInit.JOKER.get()).orElse(false)) {
                    livingEntity.setHealth(0.12f + livingEntity.getHealth() + Math.abs(damageHealAmount));
                    entity.hurt(DamageSource.thorns(livingEntity).bypassInvul().bypassArmor().bypassMagic(), 4.5f);
                }
            });
        }
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) { return true; }
}
