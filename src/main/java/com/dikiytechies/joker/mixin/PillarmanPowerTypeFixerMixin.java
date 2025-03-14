package com.dikiytechies.joker.mixin;

import com.github.standobyte.jojo.init.power.non_stand.ModPowers;
import com.github.standobyte.jojo.power.impl.nonstand.INonStandPower;
import com.github.standobyte.jojo.power.impl.nonstand.type.pillarman.PillarmanPowerType;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PillarmanPowerType.class)
public abstract class PillarmanPowerTypeFixerMixin {
    @Inject(method = "tickUser(Lnet/minecraft/entity/LivingEntity;Lcom/github/standobyte/jojo/power/impl/nonstand/INonStandPower;)V", at = @At(value = "HEAD"), cancellable = true, remap = false)
    public void exceptionFixer(LivingEntity entity, INonStandPower power, CallbackInfo ci) {
        if (!power.getTypeSpecificData(ModPowers.PILLAR_MAN.get()).isPresent()) ci.cancel();
    }
}
