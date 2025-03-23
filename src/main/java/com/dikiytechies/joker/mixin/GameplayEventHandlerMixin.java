package com.dikiytechies.joker.mixin;

import com.dikiytechies.joker.init.power.non_stand.joker.JokerPowerInit;
import com.github.standobyte.jojo.init.power.non_stand.ModPowers;
import com.github.standobyte.jojo.power.impl.nonstand.INonStandPower;
import com.github.standobyte.jojo.util.GameplayEventHandler;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Food;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = GameplayEventHandler.class, remap = false)
public abstract class GameplayEventHandlerMixin {
    @Inject(method = "onFoodEaten", at = @At(value = "INVOKE", target = "Lcom/github/standobyte/jojo/power/impl/nonstand/INonStandPower;getNonStandPowerOptional(Lnet/minecraft/entity/LivingEntity;)Lnet/minecraftforge/common/util/LazyOptional;", remap = false))
    private static void foodEatenForJoker(LivingEntityUseItemEvent.Finish event, CallbackInfo ci) {
        LivingEntity entity = event.getEntityLiving();
        ItemStack item = event.getItem();
        Food food = item.getItem().getFoodProperties();
        INonStandPower.getNonStandPowerOptional(entity).ifPresent(power -> {
            power.getTypeSpecificData(JokerPowerInit.JOKER.get()).ifPresent(pillarman -> {
                if (pillarman.getPreviousPowerType() == ModPowers.PILLAR_MAN.get()) power.addEnergy(food.getNutrition() * 10);
            });
            power.getTypeSpecificData(JokerPowerInit.JOKER.get()).ifPresent(zombie -> {
                if (zombie.getPreviousPowerType() == ModPowers.ZOMBIE.get()) {
                    if (food.isMeat()) {
                        power.addEnergy(food.getNutrition() * 10);
                        entity.heal(food.getNutrition());
                    }
                }
            });
        });
    }
}
