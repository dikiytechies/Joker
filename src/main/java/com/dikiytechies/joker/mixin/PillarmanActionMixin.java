package com.dikiytechies.joker.mixin;

import com.dikiytechies.joker.init.power.non_stand.joker.JokerPowerInit;
import com.dikiytechies.joker.power.impl.nonstand.type.JokerData;
import com.github.standobyte.jojo.action.non_stand.NonStandAction;
import com.github.standobyte.jojo.action.non_stand.PillarmanAction;
import com.github.standobyte.jojo.init.power.non_stand.ModPowers;
import com.github.standobyte.jojo.power.impl.nonstand.INonStandPower;
import com.github.standobyte.jojo.power.impl.nonstand.type.pillarman.PillarmanData;
import com.github.standobyte.jojo.util.mc.MCUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = PillarmanAction.class, remap = false)
public abstract class PillarmanActionMixin extends NonStandAction {
    @Shadow protected int stage;
    @Shadow protected PillarmanData.Mode mode;

    public PillarmanActionMixin(AbstractBuilder<?> builder) { super(builder); }

    @Inject(method = "isUnlocked(Lcom/github/standobyte/jojo/power/impl/nonstand/INonStandPower;)Z", at = @At(value = "HEAD"), cancellable = true, remap = false)
    public void isUnlockedForJoker(INonStandPower power, CallbackInfoReturnable<Boolean> cir) {
        if (power.getType() == JokerPowerInit.JOKER.get() && power.getTypeSpecificData(JokerPowerInit.JOKER.get()).map(d -> d.getPreviousPowerType() == ModPowers.PILLAR_MAN.get()).orElse(false)) {
            JokerData joker = power.getTypeSpecificData(JokerPowerInit.JOKER.get()).get();
            cir.setReturnValue((this.stage == -1 || this.stage <= joker.getPreviousDataNbt().getInt("PillarmanStage"))
                    && (this.mode == PillarmanData.Mode.NONE || this.mode == MCUtil.nbtGetEnum(joker.getPreviousDataNbt(), "PillarmanMode", PillarmanData.Mode.class)));
            cir.cancel();
        }
    }
}
