package com.dikiytechies.joker.action.non_stand;

import com.dikiytechies.joker.capability.JokerUtilCapProvider;
import com.github.standobyte.jojo.action.ActionConditionResult;
import com.github.standobyte.jojo.action.ActionTarget;
import com.github.standobyte.jojo.power.impl.nonstand.INonStandPower;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class JokerQuickEffect extends JokerAction{
    public JokerQuickEffect(Builder builder) { super(builder); }

    @Override
    protected ActionConditionResult checkSpecificConditions(LivingEntity user, INonStandPower power, ActionTarget target) {
        if (user.getCapability(JokerUtilCapProvider.CAPABILITY).map(cap -> cap.getActiveEffect() == cap.getFavoriteEffect()).orElse(false) || user.getCapability(JokerUtilCapProvider.CAPABILITY).map(cap -> cap.getFavoriteEffect() == null).orElse(false)) {
            return ActionConditionResult.NEGATIVE;
        }
        return ActionConditionResult.POSITIVE;
    }

    @Override
    protected void perform(World world, LivingEntity user, INonStandPower power, ActionTarget target) {
        if (!world.isClientSide()) {
            user.getCapability(JokerUtilCapProvider.CAPABILITY).ifPresent(cap -> cap.setActiveEffect(cap.getFavoriteEffect()));
        }
    }

    @NotNull
    @Override
    public ResourceLocation getIconTexture(@Nullable INonStandPower power) {
        if (power.getUser().getCapability(JokerUtilCapProvider.CAPABILITY).map(cap -> cap.getFavoriteEffect() == null).orElse(false)) {
            return super.getIconTexture(power);
        } else return power.getUser().getCapability(JokerUtilCapProvider.CAPABILITY).map(cap ->
                new ResourceLocation(cap.getFavoriteEffect().effect.getRegistryName().getNamespace(), "textures/mob_effect/" + cap.getFavoriteEffect().effect.getRegistryName().getPath() + ".png")).orElse(super.getIconTexture(power));
    }
}
