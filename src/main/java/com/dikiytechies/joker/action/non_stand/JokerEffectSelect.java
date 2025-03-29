package com.dikiytechies.joker.action.non_stand;

import com.dikiytechies.joker.util.ClientUtil;
import com.github.standobyte.jojo.action.ActionTarget;
import com.github.standobyte.jojo.power.impl.nonstand.INonStandPower;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

public class JokerEffectSelect extends JokerAction {
    public JokerEffectSelect(Builder builder) {
        super(builder);
        stage = 2;
    }

    @Override
    protected void perform(World world, LivingEntity user, INonStandPower power, ActionTarget target) {
        if (world.isClientSide() && user instanceof PlayerEntity) {
            ClientUtil.openTargetSelection((PlayerEntity) user);
        }
    }
}
