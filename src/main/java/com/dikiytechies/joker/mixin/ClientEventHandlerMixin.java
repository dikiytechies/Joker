package com.dikiytechies.joker.mixin;

import com.dikiytechies.joker.init.power.non_stand.joker.JokerPowerInit;
import com.github.standobyte.jojo.client.ClientEventHandler;
import com.github.standobyte.jojo.init.power.non_stand.ModPowers;
import com.github.standobyte.jojo.power.impl.nonstand.INonStandPower;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ClientEventHandler.class, remap = false)
public abstract class ClientEventHandlerMixin {
    @Shadow @Final
    private Minecraft mc;
    @Inject(method = "disableFoodBar", at = @At(value = "INVOKE", shift = At.Shift.BEFORE, target = "Lcom/github/standobyte/jojo/power/impl/nonstand/INonStandPower;getNonStandPowerOptional(Lnet/minecraft/entity/LivingEntity;)Lnet/minecraftforge/common/util/LazyOptional;"), cancellable = true, remap = false)
    public void disableJokerVampireFoodBar(RenderGameOverlayEvent.Pre event, CallbackInfo ci) {
        INonStandPower.getNonStandPowerOptional(mc.player).ifPresent(power -> {
            if (power.getType() == JokerPowerInit.JOKER.get()) power.getTypeSpecificData(JokerPowerInit.JOKER.get()).ifPresent(d -> {
                if (d.getPreviousPowerType() == ModPowers.VAMPIRISM.get() || d.getPreviousPowerType() == ModPowers.ZOMBIE.get() || (d.getPreviousPowerType() == ModPowers.PILLAR_MAN.get() && d.getPreviousDataNbt().getInt("PillarmanStage") > 1)) {
                    event.setCanceled(true);
                    ci.cancel();
                }
            });
        });
    }
}
