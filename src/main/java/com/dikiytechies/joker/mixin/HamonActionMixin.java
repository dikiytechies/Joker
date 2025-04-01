package com.dikiytechies.joker.mixin;

import com.dikiytechies.joker.init.Sounds;
import com.dikiytechies.joker.init.power.non_stand.joker.JokerPowerInit;
import com.dikiytechies.joker.power.impl.nonstand.type.JokerData;
import com.github.standobyte.jojo.action.ActionConditionResult;
import com.github.standobyte.jojo.action.ActionTarget;
import com.github.standobyte.jojo.action.non_stand.HamonAction;
import com.github.standobyte.jojo.action.non_stand.NonStandAction;
import com.github.standobyte.jojo.init.ModStatusEffects;
import com.github.standobyte.jojo.init.power.non_stand.ModPowers;
import com.github.standobyte.jojo.modcompat.ModInteractionUtil;
import com.github.standobyte.jojo.power.impl.nonstand.INonStandPower;
import com.github.standobyte.jojo.power.impl.nonstand.type.hamon.HamonData;
import com.github.standobyte.jojo.power.impl.nonstand.type.hamon.skill.CharacterHamonTechnique;
import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = HamonAction.class, remap = false)
public abstract class HamonActionMixin extends NonStandAction {
    @Shadow protected boolean changesAuraColor() { return true; }
    public HamonActionMixin(HamonActionMixin.AbstractBuilder<?> builder) { super(builder); }


    @Inject(method = "checkConditions(Lnet/minecraft/entity/LivingEntity;Lcom/github/standobyte/jojo/power/impl/nonstand/INonStandPower;Lcom/github/standobyte/jojo/action/ActionTarget;)Lcom/github/standobyte/jojo/action/ActionConditionResult;", at = @At("HEAD"), cancellable = true, remap = false)
    public void jokerHamonResult(LivingEntity user, INonStandPower power, ActionTarget target, CallbackInfoReturnable<ActionConditionResult> cir) {
        if (power.getType() == JokerPowerInit.JOKER.get() && power.getTypeSpecificData(JokerPowerInit.JOKER.get()).map(j -> j.getPreviousPowerType() == ModPowers.HAMON.get()).orElse(false)) {
            ActionConditionResult hamonCheck = power.getTypeSpecificData(JokerPowerInit.JOKER.get()).map(JokerData::getPreviousData).map(h -> {
                HamonData hamon = (HamonData) h;
                if (getBloodstreamEfficiency(power) <= 0F) {
                    return conditionMessage("hamon_no_bloodstream");
                }
                if (hamon.isMeditating()) {
                    return ActionConditionResult.NEGATIVE;
                }
                return ActionConditionResult.POSITIVE;
            }).orElse(ActionConditionResult.NEGATIVE);
            if (!hamonCheck.isPositive()) {
                cir.setReturnValue(hamonCheck);
                cir.cancel();
            }
            user.level.playSound(null, user.blockPosition(), Sounds.COUGH.get(), user.getSoundSource(), 1.0f, 1.0f);
            cir.setReturnValue(ActionConditionResult.NEGATIVE);
            cir.cancel();
        }
    }

    @Inject(method = "afterClick(Lnet/minecraft/world/World;Lnet/minecraft/entity/LivingEntity;Lcom/github/standobyte/jojo/power/impl/nonstand/INonStandPower;Z)V", at = @At("HEAD"), cancellable = true, remap = false)
    public void afterClick(World world, LivingEntity user, INonStandPower power, boolean passedRequirements, CallbackInfo ci) {
        if (changesAuraColor() && passedRequirements) {
            if (power.getType() == JokerPowerInit.JOKER.get() && power.getTypeSpecificData(JokerPowerInit.JOKER.get()).map(j -> j.getPreviousPowerType() == ModPowers.HAMON.get()).orElse(false)) {
                power.getTypeSpecificData(JokerPowerInit.JOKER.get()).map(j -> (HamonData) j.getPreviousData()).get().setLastUsedAction(this);
                ci.cancel();
            }
        }
    }
    @Inject(method = "getShout(Lnet/minecraft/entity/LivingEntity;Lcom/github/standobyte/jojo/power/impl/nonstand/INonStandPower;Lcom/github/standobyte/jojo/action/ActionTarget;Z)Lnet/minecraft/util/SoundEvent;", at = @At("HEAD"), cancellable = true, remap = false)
    protected void getShout(LivingEntity user, INonStandPower power, ActionTarget target, boolean wasActive, CallbackInfoReturnable<SoundEvent> cir) {
        if (power.getType() == JokerPowerInit.JOKER.get() && power.getTypeSpecificData(JokerPowerInit.JOKER.get()).map(j -> j.getPreviousPowerType() == ModPowers.HAMON.get()).orElse(false)) {
            SoundEvent shout = null;
            CharacterHamonTechnique technique = power.getTypeSpecificData(JokerPowerInit.JOKER.get()).map(j -> (HamonData) j.getPreviousData()).get().getCharacterTechnique();
            if (technique != null) {
                shout = ((HamonAction) (Object) this).getCharacterShout(technique);
            }
            if (shout == null) {
                shout = super.getShout(user, power, target, wasActive);
            }
            cir.setReturnValue(shout);
            cir.cancel();
        }
    }
    private float getBloodstreamEfficiency(INonStandPower power) {
        float efficiency = 1;
        LivingEntity user = power.getUser();

        float bleeding = 0;
        EffectInstance bleedingEffect = user.getEffect(ModStatusEffects.BLEEDING.get());
        if (bleedingEffect != null) {
            bleeding = Math.min((bleedingEffect.getAmplifier() + 1) * 0.2F, 0.8F);
        }
        efficiency *= (1F - bleeding);

        float freeze = 0;
        EffectInstance freezeEffect = user.getEffect(ModStatusEffects.FREEZE.get());
        if (freezeEffect != null) {
            freeze = Math.min((freezeEffect.getAmplifier() + 1) * 0.25F, 1);
        }
        freeze = Math.max(ModInteractionUtil.getEntityFreeze(user), freeze);
        efficiency *= (1F - freeze);

        return efficiency;
    }
}
