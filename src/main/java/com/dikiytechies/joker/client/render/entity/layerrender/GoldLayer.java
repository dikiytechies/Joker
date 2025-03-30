package com.dikiytechies.joker.client.render.entity.layerrender;

import com.dikiytechies.joker.AddonMain;
import com.dikiytechies.joker.client.render.entity.model.GreedArmorModel;
import com.dikiytechies.joker.init.AddonStatusEffects;
import com.dikiytechies.joker.potion.GreedStatusEffect;
import com.github.standobyte.jojo.client.render.entity.layerrenderer.IFirstPersonHandLayer;
import com.github.standobyte.jojo.power.impl.nonstand.INonStandPower;
import com.github.standobyte.jojo.power.impl.nonstand.type.NonStandPowerType;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.PlayerRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.HandSide;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

import javax.annotation.Nullable;

public class GoldLayer<T extends LivingEntity, M extends EntityModel<T>> extends LayerRenderer<T, M> implements IFirstPersonHandLayer {
    private GreedArmorModel<T> armorModel;
    public static final ResourceLocation PATH = new ResourceLocation(AddonMain.MOD_ID, "textures/entity/layer/gold/gold_stage_");

    public GoldLayer(IEntityRenderer<T, M> renderer, boolean slim) {
        super(renderer);
        this.armorModel = new GreedArmorModel<>(0.0F, slim);
    }
    @Override
    public void render(MatrixStack matrixStack, IRenderTypeBuffer buffer, int packedLight, T entity, float limbSwing, float limbSwingAmount, float partialTick, float ticks, float yRot, float xRot){
        if (entity.hasEffect(AddonStatusEffects.GREED.get())) {
            if (this.getParentModel().young) {
                matrixStack.translate(0.0, 0.75, 0.0);
                matrixStack.scale(0.5F, 0.5F, 0.5F);
            }
            matrixStack.pushPose();
            M model = getParentModel();
            ResourceLocation texture = getTexture(model, entity);
            armorModel.prepareMobModel(entity, limbSwing, limbSwingAmount, partialTick);
            model.copyPropertiesTo(armorModel);
            armorModel.setupAnim(entity, limbSwing, limbSwingAmount, ticks, yRot, xRot);
            if (texture == null) {
                matrixStack.popPose();
                return;
            }
            IVertexBuilder vertexBuilder = buffer.getBuffer(RenderType.entityTranslucent(texture));
            armorModel.renderToBuffer(matrixStack, vertexBuilder, packedLight, LivingRenderer.getOverlayCoords(entity, 0.0F), 1, 1, 1, 1);
            if (model instanceof PlayerModel) {
                PlayerModel player = (PlayerModel) model;
                armorModel.crouching = player.crouching;
                armorModel.rightArmPose = player.rightArmPose;
                armorModel.leftArmPose = player.leftArmPose;
            }
            matrixStack.popPose();
        }
    }
    @Nullable
    private ResourceLocation getTexture(EntityModel<?> model, LivingEntity entity) {
        ResourceLocation texture = null;
        if (entity.hasEffect(AddonStatusEffects.GREED.get())){
            float missingHealthK = entity.getMaxHealth() / GreedStatusEffect.getMaxHealthWithoutGreed(entity);
            if (missingHealthK < 1) {
                //0.1 = 5; 0.3 = 4; 0.5 = 3; 0.7 = 2; 0.9 = 1
                int id = (int) MathHelper.clamp(1 / missingHealthK + 1, 1, 5);
                texture = new ResourceLocation(PATH.toString() + id +".png");
            }
        }
        return texture;
    }
    public void renderHandFirstPerson(HandSide side, MatrixStack matrixStack, IRenderTypeBuffer buffer, int light, AbstractClientPlayerEntity player, PlayerRenderer playerRenderer) {
        PlayerModel<AbstractClientPlayerEntity> model = playerRenderer.getModel();
        IFirstPersonHandLayer.defaultRender(side, matrixStack, buffer, light, player, playerRenderer, model, this.getTexture(model, player));
    }
}
