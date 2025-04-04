package com.dikiytechies.joker.client.render.entity.renderer;

import com.dikiytechies.joker.AddonMain;
import com.dikiytechies.joker.client.render.entity.model.JokerIggyModel;
import com.dikiytechies.joker.entity.mob.JokerIggyEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class JokerIggyRenderer extends GeoEntityRenderer<JokerIggyEntity> {
    public JokerIggyRenderer(EntityRendererManager renderManager) {
        super(renderManager, new JokerIggyModel());
        this.shadowRadius = 0.3f;
    }

    @Override
    public ResourceLocation getTextureLocation(JokerIggyEntity instance) {
        int frameId = instance.animatedTicks >= 20? 1: 0;
        return new ResourceLocation(AddonMain.MOD_ID, "textures/entity/animated/joker_iggy_texture_" + frameId + ".png");
    }

    @Override
    public RenderType getRenderType(JokerIggyEntity animatable, float partialTicks, MatrixStack stack, @Nullable IRenderTypeBuffer renderTypeBuffer, @Nullable IVertexBuilder vertexBuilder, int packedLightIn, ResourceLocation textureLocation) {

        return super.getRenderType(animatable, partialTicks, stack, renderTypeBuffer, vertexBuilder, packedLightIn, textureLocation);
    }
}
