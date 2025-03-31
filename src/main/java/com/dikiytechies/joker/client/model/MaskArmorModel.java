package com.dikiytechies.joker.client.model;

import com.dikiytechies.joker.AddonMain;
import com.dikiytechies.joker.item.MaskItem;
import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class MaskArmorModel extends AnimatedGeoModel<MaskItem> {
    @Override
    public ResourceLocation getModelLocation(MaskItem object) {
        return new ResourceLocation(AddonMain.MOD_ID, "geo/mask.geo.json");
    }

    @Override
    public ResourceLocation getTextureLocation(MaskItem object) {
        return new ResourceLocation(AddonMain.MOD_ID, "textures/armor/mask_texture.png");
    }
    @Override
    public ResourceLocation getAnimationFileLocation(MaskItem animatable) {
        return null;
    }
}
