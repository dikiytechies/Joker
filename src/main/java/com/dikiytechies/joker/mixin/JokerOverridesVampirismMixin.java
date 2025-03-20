package com.dikiytechies.joker.mixin;

import com.dikiytechies.joker.init.power.non_stand.joker.JokerPowerInit;
import com.github.standobyte.jojo.power.impl.nonstand.type.NonStandPowerType;
import com.github.standobyte.jojo.power.impl.nonstand.type.vampirism.VampirismPowerType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = VampirismPowerType.class, remap = false)
public class JokerOverridesVampirismMixin {
    @Inject(method = "isReplaceableWith(Lcom/github/standobyte/jojo/power/impl/nonstand/type/NonStandPowerType;)Z", at = @At(value = "HEAD"), cancellable = true, remap = false)
    public void replacableWithJoker(NonStandPowerType<?> type, CallbackInfoReturnable<Boolean> cir) {
        if (type == JokerPowerInit.JOKER.get()) {
            cir.setReturnValue(true);
            cir.cancel();
        }
    }
}
