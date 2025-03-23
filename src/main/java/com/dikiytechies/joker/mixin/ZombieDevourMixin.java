package com.dikiytechies.joker.mixin;

import com.dikiytechies.joker.init.power.non_stand.joker.JokerPowerInit;
import com.github.standobyte.jojo.action.ActionConditionResult;
import com.github.standobyte.jojo.action.ActionTarget;
import com.github.standobyte.jojo.action.non_stand.ZombieDevour;
import com.github.standobyte.jojo.init.power.non_stand.ModPowers;
import com.github.standobyte.jojo.power.impl.nonstand.INonStandPower;
import com.github.standobyte.jojo.util.mod.JojoModUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.github.standobyte.jojo.action.Action.conditionMessage;

@Mixin(value = ZombieDevour.class, remap = false)
public abstract class ZombieDevourMixin {
    @Inject(method = "checkTarget(Lcom/github/standobyte/jojo/action/ActionTarget;Lnet/minecraft/entity/LivingEntity;Lcom/github/standobyte/jojo/power/impl/nonstand/INonStandPower;)Lcom/github/standobyte/jojo/action/ActionConditionResult;", at = @At(value = "HEAD"), cancellable = true, remap = false)
    private void fixJokerZombie(ActionTarget target, LivingEntity user, INonStandPower power, CallbackInfoReturnable<ActionConditionResult> cir) {
        if (power.getType() == JokerPowerInit.JOKER.get() && power.getTypeSpecificData(JokerPowerInit.JOKER.get()).map(d -> d.getPreviousPowerType() == ModPowers.ZOMBIE.get()).orElse(false)) {
            Entity entityTarget = target.getEntity();
            if (entityTarget instanceof LivingEntity) {
                LivingEntity livingTarget = (LivingEntity) entityTarget;
                if (!JojoModUtil.canBleed(livingTarget) || JojoModUtil.isUndeadOrVampiric(livingTarget)) {
                    cir.setReturnValue(conditionMessage("blood"));
                    cir.cancel();
                    return;
                }
                cir.setReturnValue(ActionConditionResult.POSITIVE);
                cir.cancel();
                return;
            }
            cir.setReturnValue(ActionConditionResult.NEGATIVE_CONTINUE_HOLD);
            cir.cancel();
        }
    }
}
