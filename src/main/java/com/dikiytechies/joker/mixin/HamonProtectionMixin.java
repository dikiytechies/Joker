package com.dikiytechies.joker.mixin;

import com.dikiytechies.joker.init.power.non_stand.joker.JokerPowerInit;
import com.github.standobyte.jojo.action.ActionConditionResult;
import com.github.standobyte.jojo.action.ActionTarget;
import com.github.standobyte.jojo.action.non_stand.HamonAction;
import com.github.standobyte.jojo.action.non_stand.HamonProtection;
import com.github.standobyte.jojo.init.power.non_stand.ModPowers;
import com.github.standobyte.jojo.power.impl.nonstand.INonStandPower;
import com.github.standobyte.jojo.power.impl.nonstand.type.hamon.HamonData;
import com.github.standobyte.jojo.util.general.LazySupplier;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;

@Mixin(value = HamonProtection.class, remap = false)
public abstract class HamonProtectionMixin extends HamonAction {
    public HamonProtectionMixin(HamonProtectionMixin.AbstractBuilder<?> builder) { super(builder); }
    private final LazySupplier<ResourceLocation> protectionTex =
            new LazySupplier<>(() -> makeIconVariant(this, "_on"));
    @Inject(method = "getIconTexturePath(Lcom/github/standobyte/jojo/power/impl/nonstand/INonStandPower;)Lnet/minecraft/util/ResourceLocation;", at = @At("HEAD"), cancellable = true, remap = false)
    public void getIconTexturePath(@Nullable INonStandPower power, CallbackInfoReturnable<ResourceLocation> cir) {
        if (power.getType() == JokerPowerInit.JOKER.get() && power.getTypeSpecificData(JokerPowerInit.JOKER.get()).map(j -> j.getPreviousPowerType() == ModPowers.HAMON.get()).orElse(false)) {
            if (power != null && power.getTypeSpecificData(JokerPowerInit.JOKER.get()).map(j -> (HamonData) j.getPreviousData()).get().isProtectionEnabled()) {
                cir.setReturnValue(protectionTex.get());
            } else {
                cir.setReturnValue(super.getIconTexturePath(power));
            }
            cir.cancel();
        }
    }
    @Inject(method = "perform(Lnet/minecraft/world/World;Lnet/minecraft/entity/LivingEntity;Lcom/github/standobyte/jojo/power/impl/nonstand/INonStandPower;Lcom/github/standobyte/jojo/action/ActionTarget;)V", at = @At("HEAD"), cancellable = true, remap = false)
    protected void perform(World world, LivingEntity user, INonStandPower power, ActionTarget target, CallbackInfo ci) {
        if (power.getType() == JokerPowerInit.JOKER.get() && power.getTypeSpecificData(JokerPowerInit.JOKER.get()).map(j -> j.getPreviousPowerType() == ModPowers.HAMON.get()).orElse(false)) {
            if (!world.isClientSide()) {
                power.getTypeSpecificData(JokerPowerInit.JOKER.get()).map(j -> (HamonData) j.getPreviousData()).get().toggleHamonProtection();
            }
        }
    }

    @Inject(method = "greenSelection(Lcom/github/standobyte/jojo/power/impl/nonstand/INonStandPower;Lcom/github/standobyte/jojo/action/ActionConditionResult;)Z", at = @At("HEAD"), cancellable = true, remap = false)
    public void greenSelection(INonStandPower power, ActionConditionResult conditionCheck, CallbackInfoReturnable<Boolean> cir) {
        if (power.getType() == JokerPowerInit.JOKER.get() && power.getTypeSpecificData(JokerPowerInit.JOKER.get()).map(j -> j.getPreviousPowerType() == ModPowers.HAMON.get()).orElse(false)) {
            cir.setReturnValue(power.getTypeSpecificData(JokerPowerInit.JOKER.get())
                    .map(j -> (HamonData) j.getPreviousData()).isPresent() && power.getTypeSpecificData(JokerPowerInit.JOKER.get())
                    .map(j -> (HamonData) j.getPreviousData()).get().isProtectionEnabled());
            cir.cancel();
        }
    }
}
