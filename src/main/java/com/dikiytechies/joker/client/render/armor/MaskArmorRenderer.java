package com.dikiytechies.joker.client.render.armor;

import com.dikiytechies.joker.client.model.MaskArmorModel;
import com.dikiytechies.joker.item.MaskItem;
import software.bernie.geckolib3.renderers.geo.GeoArmorRenderer;

public class MaskArmorRenderer extends GeoArmorRenderer<MaskItem> {
    public MaskArmorRenderer() {
        super(new MaskArmorModel());
        this.headBone = "head";
        this.bodyBone = "chest";
        this.rightArmBone = "rightArm";
        this.leftArmBone = "leftArm";
        this.rightLegBone = "rightLeg";
        this.leftLegBone = "leftLeg";
        this.rightBootBone = "rightBoot";
        this.leftBootBone = "leftBoot";
    }
}
