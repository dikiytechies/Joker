package com.dikiytechies.joker.mixin;

import com.dikiytechies.joker.init.power.non_stand.joker.JokerPowerInit;
import com.dikiytechies.joker.power.impl.nonstand.type.JokerData;
import com.github.standobyte.jojo.client.render.entity.layerrenderer.IFirstPersonHandLayer;
import com.github.standobyte.jojo.client.render.entity.layerrenderer.PillarmanLayer;
import com.github.standobyte.jojo.init.power.non_stand.ModPowers;
import com.github.standobyte.jojo.power.impl.nonstand.INonStandPower;
import com.github.standobyte.jojo.power.impl.nonstand.type.pillarman.PillarmanData;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = PillarmanLayer.class, remap = false)
public abstract class PillarmanLayerMixin<T extends LivingEntity, M extends EntityModel<T>> extends LayerRenderer<T, M> implements IFirstPersonHandLayer {
    public PillarmanLayerMixin(IEntityRenderer<T, M> p_i50926_1_) { super(p_i50926_1_); }
    @Shadow @Final
    public static ResourceLocation TEXTURE;

    @Inject(method = "getTexture", at = @At(value = "HEAD"), cancellable = true, remap = false)
    private void jokerStoneLayerFix(EntityModel<?> model, LivingEntity entity, CallbackInfoReturnable<ResourceLocation> cir) {
        if (INonStandPower.getNonStandPowerOptional(entity).resolve().flatMap(
                        power -> power.getTypeSpecificData(JokerPowerInit.JOKER.get()))
                .map(JokerData::isPillarmanStoneFormEnabled).orElse(false)) {
            cir.setReturnValue(TEXTURE);
            cir.cancel();
        }
    }
}
