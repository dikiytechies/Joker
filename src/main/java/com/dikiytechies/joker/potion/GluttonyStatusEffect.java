package com.dikiytechies.joker.potion;

import com.github.standobyte.jojo.potion.StatusEffect;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.EffectType;

public class GluttonyStatusEffect extends StatusEffect {
    public GluttonyStatusEffect(EffectType type, int liquidColor) {
        super(type, liquidColor);
    }
    @Override
    public void applyEffectTick(LivingEntity livingEntity, int amplifier) {
        if (!livingEntity.level.isClientSide()) {
            livingEntity.level.getEntitiesOfClass(LivingEntity.class, livingEntity.getBoundingBox().inflate(16.5),
                entity -> (livingEntity.getHealth() > 0) && !entity.is(livingEntity) && (!(entity instanceof PlayerEntity) || !entity.isSpectator() && !((PlayerEntity)entity).isCreative())).forEach(entity -> {
                float heal = Math.min(entity.getHealth(), entity.getMaxHealth() / 4000.0f * (amplifier + 1));
                final float absorptionLimit = livingEntity.getMaxHealth() / 2;
                boolean result = false;
                if (heal != entity.getHealth()) {
                    if (livingEntity.getHealth() < livingEntity.getMaxHealth()) {
                        livingEntity.setHealth(livingEntity.getHealth() + heal);
                        result = true;
                    } else if (livingEntity.getAbsorptionAmount() < absorptionLimit) {
                        livingEntity.setAbsorptionAmount(livingEntity.getAbsorptionAmount() + heal / 2);
                        result = true;
                    } else if (livingEntity instanceof PlayerEntity) {
                        PlayerEntity player = (PlayerEntity) livingEntity;
                        if (player.getFoodData().getSaturationLevel() < 6) {
                            player.getFoodData().setSaturation(player.getFoodData().getSaturationLevel() + heal);
                            result = true;
                        }
                    }
                    if (result) entity.setHealth(entity.getHealth() - heal);
                }
            });
        }
    }
    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) { return true; }
}
