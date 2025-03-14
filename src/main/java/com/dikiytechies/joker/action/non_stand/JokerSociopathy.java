package com.dikiytechies.joker.action.non_stand;

import com.dikiytechies.joker.init.power.non_stand.joker.JokerPowerInit;
import com.dikiytechies.joker.power.impl.nonstand.type.JokerData;
import com.github.standobyte.jojo.action.ActionConditionResult;
import com.github.standobyte.jojo.action.ActionTarget;
import com.github.standobyte.jojo.power.impl.nonstand.INonStandPower;
import com.github.standobyte.jojo.util.general.LazySupplier;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class JokerSociopathy extends JokerAction{
    public JokerSociopathy(JokerAction.Builder builder) {
        super(builder);
    }

    private final LazySupplier<ResourceLocation> sociopathyTex =
            new LazySupplier<>(() -> makeIconVariant(this, "_on"));

    @Override
    public ResourceLocation getIconTexturePath(@Nullable INonStandPower power) {
        if (power != null && power.getTypeSpecificData(JokerPowerInit.JOKER.get()).get().isSociopathyEnabled()) {
            return sociopathyTex.get();
        }
        else {
            return super.getIconTexturePath(power);
        }
    }

    @Override
    protected void perform(World world, LivingEntity user, INonStandPower power, ActionTarget target) {
        if (!world.isClientSide()) {
            power.getTypeSpecificData(JokerPowerInit.JOKER.get()).get().toggleSociopathy();
        }

    }

    @Override
    public boolean greenSelection(INonStandPower power, ActionConditionResult conditionCheck) {
        return power.getTypeSpecificData(JokerPowerInit.JOKER.get()).map(JokerData::isSociopathyEnabled).orElse(false);
    }
}
