package com.dikiytechies.joker.client.render.entity.model;

import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.entity.LivingEntity;

public class GreedArmorModel<T extends LivingEntity> extends PlayerModel<T> {
    public GreedArmorModel(float inflate, boolean slim) { super(inflate, slim); }
}
