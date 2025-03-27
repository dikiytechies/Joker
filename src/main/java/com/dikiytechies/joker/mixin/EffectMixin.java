package com.dikiytechies.joker.mixin;

import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectType;
import net.minecraft.potion.Effects;
import net.minecraftforge.registries.ForgeRegistryEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Effect.class)
public abstract class EffectMixin extends net.minecraftforge.registries.ForgeRegistryEntry<Effect> implements net.minecraftforge.common.extensions.IForgeEffect {
    @Inject(method = "applyEffectTick", at = @At("HEAD"))
    public void greedRegeneration(LivingEntity entity, int amplifier, CallbackInfo ci) {
        if ((ForgeRegistryEntry<Effect>) this == Effects.REGENERATION) {
            if (entity.getHealth() == entity.getMaxHealth()) {
                entity.heal(1.0F);
            }
        }
    }
}
