package com.dikiytechies.joker.mixin;

import com.dikiytechies.joker.init.power.non_stand.joker.JokerPowerInit;
import com.github.standobyte.jojo.action.non_stand.PillarmanAction;
import com.github.standobyte.jojo.action.non_stand.PillarmanBladeSlash;
import com.github.standobyte.jojo.action.player.ContinuousActionInstance;
import com.github.standobyte.jojo.action.player.IPlayerAction;
import com.github.standobyte.jojo.capability.entity.PlayerUtilCap;
import com.github.standobyte.jojo.client.playeranim.anim.ModPlayerAnimations;
import com.github.standobyte.jojo.init.power.non_stand.ModPowers;
import com.github.standobyte.jojo.power.impl.nonstand.INonStandPower;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = PillarmanBladeSlash.Instance.class, remap = false)
public abstract class PillarmanBladeSlashMixin extends ContinuousActionInstance<PillarmanBladeSlash, INonStandPower> {


    public PillarmanBladeSlashMixin(LivingEntity user, PlayerUtilCap userCap, INonStandPower playerPower, PillarmanBladeSlash action) {
        super(user, userCap, playerPower, action);
    }
    @Inject(method = "playerTick", at = @At(value = "HEAD"), cancellable = true, remap = false)
    public void playerTickFix(CallbackInfo ci) {
        if (getTick() == 2 && playerPower.getType() == ModPowers.PILLAR_MAN.get() && playerPower.getTypeSpecificData(JokerPowerInit.JOKER.get()).map(j -> j.getPreviousPowerType() == ModPowers.PILLAR_MAN.get()).orElse(false)) {
            playerPower.getTypeSpecificData(JokerPowerInit.JOKER.get()).ifPresent(j -> j.setPillarmanBladesVisible(true));
            ci.cancel();
        }
    }

    @Inject(method = "onStop", at = @At(value = "INVOKE", shift = At.Shift.AFTER, target = "Lnet/minecraft/world/World;isClientSide()Z"), cancellable = true, remap = false)
    public void onStopFix(CallbackInfo ci) {
        if (playerPower.getType() == JokerPowerInit.JOKER.get() && playerPower.getTypeSpecificData(JokerPowerInit.JOKER.get()).map(j -> j.getPreviousPowerType() == ModPowers.PILLAR_MAN.get()).orElse(false)) {
            playerPower.getTypeSpecificData(JokerPowerInit.JOKER.get()).ifPresent(j -> j.setPillarmanBladesVisible(false));
            ModPlayerAnimations.bladeSlash.setAnimEnabled((PlayerEntity) user, false);
            ci.cancel();
        }
    }
    /*
    @Mixin(value = PillarmanBladeSlash.Instance.class, remap = false)
    public static abstract class PBSInstanceMixin extends ContinuousActionInstance<PillarmanBladeSlash, INonStandPower> {
        public PBSInstanceMixin(LivingEntity user, PlayerUtilCap userCap, INonStandPower playerPower, PillarmanBladeSlash action) { super(user, userCap, playerPower, action); }

        @Inject(method = "playerTick", at = @At(value = "INVOKE", shift = At.Shift.BEFORE, target = "Lcom/github/standobyte/jojo/power/impl/nonstand/type/pillarman/PillarmanData;setBladesVisible(Z)V"), cancellable = true, remap = false)
        public void playerTickFix(CallbackInfo ci) {
            if (playerPower.getType() == ModPowers.PILLAR_MAN.get() && playerPower.getTypeSpecificData(JokerPowerInit.JOKER.get()).map(j -> j.getPreviousPowerType() == ModPowers.PILLAR_MAN.get()).orElse(false)) {
                playerPower.getTypeSpecificData(JokerPowerInit.JOKER.get()).ifPresent(j -> j.setPillarmanBladesVisible(true));
                ci.cancel();
            }
        }

        @Inject(method = "onStop", at = @At(value = "INVOKE", shift = At.Shift.AFTER, target = "Lnet/minecraft/world/World;isClientSide()Z"), cancellable = true, remap = false)
        public void onStopFix(CallbackInfo ci) {
            if (playerPower.getType() == JokerPowerInit.JOKER.get() && playerPower.getTypeSpecificData(JokerPowerInit.JOKER.get()).map(j -> j.getPreviousPowerType() == ModPowers.PILLAR_MAN.get()).orElse(false)) {
                playerPower.getTypeSpecificData(JokerPowerInit.JOKER.get()).ifPresent(j -> j.setPillarmanBladesVisible(false));
                ModPlayerAnimations.bladeSlash.setAnimEnabled((PlayerEntity) user, false);
                ci.cancel();
            }
        }
    }*/
}
