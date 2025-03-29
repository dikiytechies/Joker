package com.dikiytechies.joker.mixin;

import com.dikiytechies.joker.init.AddonStatusEffects;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.FoodStats;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity {
    protected PlayerEntityMixin(EntityType<? extends LivingEntity> p_i48577_1_, World p_i48577_2_) { super(p_i48577_1_, p_i48577_2_); }
    @Inject(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getDifficulty()Lnet/minecraft/world/Difficulty;"))
    public void greedHeal(CallbackInfo ci) {
        if (this.hasEffect(AddonStatusEffects.GREED.get())) {
            if ((this.level.getDifficulty() == Difficulty.PEACEFUL && this.level.getGameRules().getBoolean(GameRules.RULE_NATURAL_REGENERATION))) {
                if (this.getHealth() == this.getMaxHealth() && this.tickCount % 20 == 0) {
                    this.heal(1.0f);
                }
            }
        }
    }
}
