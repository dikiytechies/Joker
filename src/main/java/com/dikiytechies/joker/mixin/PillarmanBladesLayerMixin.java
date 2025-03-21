package com.dikiytechies.joker.mixin;

import com.dikiytechies.joker.init.power.non_stand.joker.JokerPowerInit;
import com.dikiytechies.joker.power.impl.nonstand.type.JokerData;
import com.github.standobyte.jojo.client.render.entity.layerrenderer.IFirstPersonHandLayer;
import com.github.standobyte.jojo.client.render.entity.layerrenderer.PillarmanBladesLayer;
import com.github.standobyte.jojo.client.render.entity.layerrenderer.PillarmanLayer;
import com.github.standobyte.jojo.power.impl.nonstand.INonStandPower;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.HandSide;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = PillarmanBladesLayer.class, remap = false)
public abstract class PillarmanBladesLayerMixin<T extends LivingEntity, M extends EntityModel<T>> extends LayerRenderer<T, M> implements IFirstPersonHandLayer {
    public PillarmanBladesLayerMixin(IEntityRenderer<T, M> p_i50926_1_) { super(p_i50926_1_); }
    @Shadow @Final
    private static ResourceLocation TEXTURE;
    @Shadow
    private void renderBlade(LivingEntity entity, HandSide side, MatrixStack matrixStack, IRenderTypeBuffer buffer) {}

    @Inject(method = "render(Lcom/mojang/blaze3d/matrix/MatrixStack;Lnet/minecraft/client/renderer/IRenderTypeBuffer;ILnet/minecraft/entity/LivingEntity;FFFFFF)V", at = @At(value = "HEAD"), cancellable = true, remap = false)
    private void jokerStoneLayerFix(MatrixStack matrixStack, IRenderTypeBuffer buffer, int packedLight, T entity,
                                    float limbSwing, float limbSwingAmount, float partialTick, float ticks, float yRot, float xRot, CallbackInfo ci) {
        if (INonStandPower.getNonStandPowerOptional(entity).resolve().flatMap(
                        power -> power.getTypeSpecificData(JokerPowerInit.JOKER.get()))
                .map(JokerData::getPillarmanBladesVisible).orElse(false)) {
            matrixStack.pushPose();
            if (getParentModel().young) {
                matrixStack.translate(0.0D, 0.75D, 0.0D);
                matrixStack.scale(0.5F, 0.5F, 0.5F);
            }

            renderBlade(entity, HandSide.RIGHT, matrixStack, buffer);
            renderBlade(entity, HandSide.LEFT, matrixStack, buffer);
            matrixStack.popPose();
            ci.cancel();
        }
    }
}
