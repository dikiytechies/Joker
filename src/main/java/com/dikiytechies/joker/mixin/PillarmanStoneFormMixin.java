package com.dikiytechies.joker.mixin;

import com.dikiytechies.joker.init.power.non_stand.joker.JokerPowerInit;
import com.dikiytechies.joker.power.impl.nonstand.type.JokerData;
import com.github.standobyte.jojo.action.ActionConditionResult;
import com.github.standobyte.jojo.action.ActionTarget;
import com.github.standobyte.jojo.action.non_stand.PillarmanAction;
import com.github.standobyte.jojo.action.non_stand.PillarmanStoneForm;
import com.github.standobyte.jojo.init.power.non_stand.ModPowers;
import com.github.standobyte.jojo.power.impl.nonstand.INonStandPower;
import com.github.standobyte.jojo.power.impl.nonstand.type.pillarman.PillarmanData;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = PillarmanStoneForm.class, remap = false)

public abstract class PillarmanStoneFormMixin extends PillarmanAction {
    public PillarmanStoneFormMixin(Builder builder) { super(builder); }

    @Inject(method = "perform(Lnet/minecraft/world/World;Lnet/minecraft/entity/LivingEntity;Lcom/github/standobyte/jojo/power/impl/nonstand/INonStandPower;Lcom/github/standobyte/jojo/action/ActionTarget;)V", at = @At(value = "INVOKE", shift = At.Shift.AFTER, target = "Lnet/minecraft/world/World;isClientSide()Z"), cancellable = true, remap = false)
    protected void performFix(World world, LivingEntity user, INonStandPower power, ActionTarget target, CallbackInfo ci) {
        if (power.getType() == JokerPowerInit.JOKER.get() && power.getTypeSpecificData(JokerPowerInit.JOKER.get()).map(d -> d.getPreviousPowerType() == ModPowers.PILLAR_MAN.get()).orElse(false)) {
            power.getTypeSpecificData(JokerPowerInit.JOKER.get()).ifPresent(joker -> {
                joker.togglePillarmanStoneForm();
                joker.setPillarmanBladesVisible(false);
                ci.cancel();
            });
        }
    }

    @Inject(method = "greenSelection(Lcom/github/standobyte/jojo/power/impl/nonstand/INonStandPower;Lcom/github/standobyte/jojo/action/ActionConditionResult;)Z", at = @At(value = "HEAD"), cancellable = true, remap = false)
    public void greenSelectionFix(INonStandPower power, ActionConditionResult conditionCheck, CallbackInfoReturnable<Boolean> cir) {
        if (power.getType() == JokerPowerInit.JOKER.get() && power.getTypeSpecificData(JokerPowerInit.JOKER.get()).map(d -> d.getPreviousPowerType() == ModPowers.PILLAR_MAN.get()).orElse(false)) {
            cir.setReturnValue(power.getTypeSpecificData(JokerPowerInit.JOKER.get()).map(JokerData::isPillarmanStoneFormEnabled).orElse(false));
            cir.cancel();
        }
    }
}
