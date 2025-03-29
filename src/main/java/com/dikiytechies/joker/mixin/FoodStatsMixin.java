package com.dikiytechies.joker.mixin;

import com.dikiytechies.joker.capability.JokerUtilCapProvider;
import com.dikiytechies.joker.init.AddonStatusEffects;
import com.dikiytechies.joker.potion.GreedStatusEffect;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.DamageSource;
import net.minecraft.util.FoodStats;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FoodStats.class)
public abstract class FoodStatsMixin {
    @Shadow private int foodLevel;
    @Shadow private float saturationLevel;
    @Shadow private int tickTimer;
    @Shadow private int lastFoodLevel;
    @Shadow private float exhaustionLevel;

    @Shadow public void addExhaustion(float p_75113_1_) {}
    @Inject(method = "tick", at = @At(value = "HEAD"), cancellable = true)
    public void greedHealTick(PlayerEntity player, CallbackInfo ci) {
        if (player.hasEffect(AddonStatusEffects.GREED.get())) {
            Difficulty difficulty = player.level.getDifficulty();
            this.lastFoodLevel = this.foodLevel;
            if (this.exhaustionLevel > 4.0F) {
                this.exhaustionLevel -= 4.0F;
                if (this.saturationLevel > 0.0F) {
                    this.saturationLevel = Math.max(this.saturationLevel - 1.0F, 0.0F);
                } else if (difficulty != Difficulty.PEACEFUL) {
                    this.foodLevel = Math.max(this.foodLevel - 1, 0);
                }
            }

            boolean flag = player.level.getGameRules().getBoolean(GameRules.RULE_NATURAL_REGENERATION);
            boolean needsHealth = player.getHealth() - GreedStatusEffect.getMaxHealthWithoutGreed(player) <= GreedStatusEffect.getMaxArmorWithoutGreed(player);
            if (flag && this.saturationLevel > 0.0F && needsHealth && this.foodLevel >= 20) {
                ++this.tickTimer;
                if (this.tickTimer >= 10) {
                    float f = Math.min(this.saturationLevel, 6.0F);
                    player.heal(f / 6.0F);
                    this.addExhaustion(f);
                    this.tickTimer = 0;
                }
            } else if (flag && this.foodLevel >= 18 && needsHealth) {
                ++this.tickTimer;
                if (this.tickTimer >= 80) {
                    player.heal(1.0F);
                    this.addExhaustion(6.0F);
                    this.tickTimer = 0;
                }
            } else if (this.foodLevel <= 0) {
                ++this.tickTimer;
                if (this.tickTimer >= 80) {
                    if (player.getHealth() > 10.0F || difficulty == Difficulty.HARD || player.getHealth() > 1.0F && difficulty == Difficulty.NORMAL) {
                        player.hurt(DamageSource.STARVE, 1.0F);
                    }

                    this.tickTimer = 0;
                }
            } else {
                this.tickTimer = 0;
            }
            ci.cancel();
        }
    }
}
