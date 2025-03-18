package com.dikiytechies.joker.mixin;

import com.dikiytechies.joker.init.power.non_stand.joker.JokerPowerInit;
import com.dikiytechies.joker.power.impl.nonstand.type.JokerData;
import com.github.standobyte.jojo.init.power.non_stand.ModPowers;
import com.github.standobyte.jojo.power.impl.nonstand.INonStandPower;
import com.github.standobyte.jojo.power.impl.nonstand.type.NonStandPowerType;
import com.github.standobyte.jojo.util.mod.JojoModUtil;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Arrays;

@Mixin(JojoModUtil.class)
public abstract class MakeJokerUndeadMixin {
    @Unique
    private final static NonStandPowerType<?>[] rotP_JI$undeadPowers = { ModPowers.PILLAR_MAN.get(), ModPowers.VAMPIRISM.get(), ModPowers.ZOMBIE.get() };
    @Inject(method = "isPlayerJojoVampiric(Lnet/minecraft/entity/player/PlayerEntity;)Z", at = @At(value = "HEAD"), cancellable = true, remap = false)
    private static void makeJokerUndead(PlayerEntity player, CallbackInfoReturnable<Boolean> cir) {
        if (INonStandPower.getNonStandPowerOptional(player).map(p -> p.getType() == JokerPowerInit.JOKER.get()).get()) {
            JokerData data = INonStandPower.getNonStandPowerOptional(player).map(p -> p.getTypeSpecificData(JokerPowerInit.JOKER.get())).get().get();
            if (Arrays.stream(rotP_JI$undeadPowers).anyMatch(d -> d == data.getPreviousPowerType())) {
                cir.setReturnValue(true);
                cir.cancel();
            }
        }
    }
}
