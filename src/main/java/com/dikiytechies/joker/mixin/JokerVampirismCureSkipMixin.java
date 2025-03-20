package com.dikiytechies.joker.mixin;

import com.dikiytechies.joker.init.power.non_stand.joker.JokerPowerInit;
import com.github.standobyte.jojo.action.ActionConditionResult;
import com.github.standobyte.jojo.action.ActionTarget;
import com.github.standobyte.jojo.action.non_stand.NonStandAction;
import com.github.standobyte.jojo.action.non_stand.VampirismAction;
import com.github.standobyte.jojo.power.impl.nonstand.INonStandPower;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = VampirismAction.class, remap = false)
public abstract class JokerVampirismCureSkipMixin extends NonStandAction {

    public JokerVampirismCureSkipMixin(AbstractBuilder<?> builder) {
        super(builder);
    }

    @Inject(method = "checkConditions(Lnet/minecraft/entity/LivingEntity;Lcom/github/standobyte/jojo/power/impl/nonstand/INonStandPower;Lcom/github/standobyte/jojo/action/ActionTarget;)Lcom/github/standobyte/jojo/action/ActionConditionResult;", at = @At(value = "HEAD"), cancellable = true, remap = false)
    private void skipCuringCheck(LivingEntity user, INonStandPower power, ActionTarget target, CallbackInfoReturnable<ActionConditionResult> cir) {
        if (power.getType() == JokerPowerInit.JOKER.get()) {
            cir.setReturnValue(super.checkConditions(user, power, target));
            cir.cancel();
        }
    }
}
