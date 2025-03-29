package com.dikiytechies.joker.client;

import com.dikiytechies.joker.AddonMain;
import com.dikiytechies.joker.init.AddonStatusEffects;
import com.dikiytechies.joker.potion.GreedStatusEffect;
import com.github.standobyte.jojo.client.ClientUtil;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = AddonMain.MOD_ID,value = Dist.CLIENT)
public class ClientHandler {
    private static boolean originalHatVisibility;
    private static boolean originalJacketVisibility;
    private static boolean originalLeftPantsVisibility;
    private static boolean originalRightPantsVisibility;
    private static boolean originalRightSleeveVisibility;
    private static boolean originalLeftSleeveVisibility;
    private static boolean hasStoredOriginalValues = false;

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    @OnlyIn(Dist.CLIENT)
    public static void onRenderPlayerPre(RenderPlayerEvent.Pre event) {
        if (!ClientUtil.canSeeStands()) return;
        PlayerEntity player = event.getPlayer();
        PlayerModel<AbstractClientPlayerEntity> model = event.getRenderer().getModel();
        if (player.hasEffect(AddonStatusEffects.GREED.get())) {
            if (!hasStoredOriginalValues) {
                originalHatVisibility = model.hat.visible;
                originalJacketVisibility = model.jacket.visible;
                originalLeftPantsVisibility = model.leftPants.visible;
                originalRightPantsVisibility = model.rightPants.visible;
                originalRightSleeveVisibility = model.rightSleeve.visible;
                originalLeftSleeveVisibility = model.leftSleeve.visible;
                hasStoredOriginalValues = true;
            }

            model.hat.visible = false;
            model.jacket.visible = false;
            model.leftPants.visible = false;
            model.rightPants.visible = false;
            model.rightSleeve.visible = false;
            model.leftSleeve.visible = false;
        } else {
            if (hasStoredOriginalValues) {
                model.hat.visible = originalHatVisibility;
                model.jacket.visible = originalJacketVisibility;
                model.leftPants.visible = originalLeftPantsVisibility;
                model.rightPants.visible = originalRightPantsVisibility;
                model.rightSleeve.visible = originalRightSleeveVisibility;
                model.leftSleeve.visible = originalLeftSleeveVisibility;
            }
        }
    }
}
