package com.dikiytechies.joker.mixin;

import com.dikiytechies.joker.init.power.non_stand.joker.JokerPowerInit;
import com.dikiytechies.joker.power.impl.nonstand.type.JokerData;
import com.github.standobyte.jojo.power.impl.nonstand.TypeSpecificData;
import com.github.standobyte.jojo.power.impl.nonstand.type.NonStandPowerType;
import com.github.standobyte.jojo.power.impl.nonstand.type.zombie.ZombieData;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ZombieData.class, remap = false)
public abstract class ZombieDataMixin extends TypeSpecificData {
    @Inject(method = "onPowerGiven", at = @At("HEAD"), cancellable = true, remap = false)
    public void restoreData(NonStandPowerType<?> oldType, TypeSpecificData oldData, CallbackInfo ci) {
        if (oldType == JokerPowerInit.JOKER.get()) {
            JokerData data = (JokerData) oldData;
            LivingEntity user = power.getUser();
            this.readNBT(((JokerData) oldData).getPreviousDataNbt());
            if (!user.level.isClientSide()) {
                power.setEnergy(data.getPreviousDataNbt().getFloat("Energy"));
            }
            ci.cancel();
        }
    }
}
