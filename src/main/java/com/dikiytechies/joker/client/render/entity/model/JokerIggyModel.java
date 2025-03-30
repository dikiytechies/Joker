package com.dikiytechies.joker.client.render.entity.model;

import com.dikiytechies.joker.AddonMain;
import com.dikiytechies.joker.entity.mob.JokerIggyEntity;
import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class JokerIggyModel extends AnimatedGeoModel<JokerIggyEntity> {
    @Override
    public ResourceLocation getModelLocation(JokerIggyEntity object) {
        return new ResourceLocation(AddonMain.MOD_ID, "geo/joker_iggy.geo.json");
    }

    @Override
    public ResourceLocation getTextureLocation(JokerIggyEntity object) {
        return new ResourceLocation(AddonMain.MOD_ID, "textures/entity/joker_iggy_texture.png");
    }

    @Override
    public ResourceLocation getAnimationFileLocation(JokerIggyEntity animatable) {
        return new ResourceLocation(AddonMain.MOD_ID, "animations/joker_iggy.animation.json");
    }
}
